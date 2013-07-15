package com.redhat.solutions.mw.jbpm.ha.impl;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.conf.EventProcessingOption;
import org.drools.conf.MBeansOption;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.redhat.solutions.mw.jbpm.ha.ClusterService;
import com.redhat.solutions.mw.jbpm.ha.HeartbeatProducer;
import com.redhat.solutions.mw.jbpm.ha.KnowledgeSessionService;
import com.redhat.solutions.mw.jbpm.ha.Node;
import com.redhat.solutions.mw.jbpm.ha.NodeFailoverEvent;
import com.redhat.solutions.mw.jbpm.ha.NodeHeartbeatEvent;
import com.redhat.solutions.mw.jbpm.ha.NodeRepository;
import com.redhat.solutions.mw.jbpm.util.AgendaEventListener;

public class ClusterServiceImpl implements ClusterService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String CLASSPATH_RESOURCE = "classpath";
	private static final String FILE_RESOURCE = "file";

	private String clusterRulesChangesetPath = "cluster-rules-changeset.xml";
	private String clusterRulesChangesetPathType = CLASSPATH_RESOURCE;
	private String kloggerOutputPath = "knowledgeLogger.log";
	private String eventEntryPointName = "status-stream";
	private long pollIntervalSeconds = 10;
	private String nodeName;

	@Autowired
	private NodeRepository nodeRepository;
	@Autowired
	private KnowledgeSessionService ksessionService;
	@Autowired
	private HeartbeatProducer heartbeatPublisher;

	private boolean initialized = false;
	private long pollCount = 0;
	private KnowledgeBase clusterRuleBase;
	private StatefulKnowledgeSession clusterRuleSession;
	private AgendaEventListener agendaEventListener;
	private KnowledgeRuntimeLogger klogger;
	private Node localNode;

	private ClusterServicePoller clusterServicePoller;
	private ScheduledExecutorService scheduler;

	public void init() throws Exception {
		registerLocalNode();
		loadClusterRules();
		createClusterRuleSession();
		startClusterServicePoller();
		initialized = true;
	}

	protected class ClusterServicePoller implements Runnable {
		@Override
		public void run() {
			invokeHeartbeat();
			fireClusterRules();
		}
	}

	public void invokeHeartbeat() {
		updateLocalNode();
		refreshClusterFacts();
		heartbeatPublisher.sendHeartbeat(localNode);
	}

	public void onHeartbeat(NodeHeartbeatEvent heartbeatEvent) {
		WorkingMemoryEntryPoint eventEntryPoint = clusterRuleSession.getWorkingMemoryEntryPoint(eventEntryPointName);
		eventEntryPoint.insert(heartbeatEvent);
	}

	public void onFailover(NodeFailoverEvent failoverEvent) {
		WorkingMemoryEntryPoint eventEntryPoint = clusterRuleSession.getWorkingMemoryEntryPoint(eventEntryPointName);
		eventEntryPoint.insert(failoverEvent);
	}

	protected void registerLocalNode() {
		Node node = nodeRepository.findNode(nodeName);
		if (node == null) {
			node = new Node();
			node.setNodeName(nodeName);
		}
		node.setStatus(Node.ACTIVE_STATUS);
		node.setClusterEntyTime(new Date());
		nodeRepository.saveOrUpdate(node);
		this.localNode = node;
	}

	public void updateLocalNode() {
		this.localNode.setLastHeartbeat(new Date());
		nodeRepository.saveOrUpdate(this.localNode);
	}

	protected void refreshClusterFacts() {
		// Remove all the node objects from the knowledge session
		retractNodeFacts();

		// Reload cluster nodes from database, and insert them into the session
		List<Node> nodes = nodeRepository.findAllNodes();
		clusterRuleSession.insert(nodes);
	}

	protected void retractNodeFacts() {
		ObjectFilter nodeFactFilter = new ObjectFilter() {
			@Override
			public boolean accept(Object fact) {
				return fact.getClass().equals(Node.class);
			}
		};
		clusterRuleSession.getFactHandles(nodeFactFilter);
	}

	public void fireClusterRules() {
		log.trace("Fired rules [" + pollCount + "]");

		clusterRuleSession.fireAllRules();
		pollCount++;

		if (log.isTraceEnabled()) {
			String facts = "FACTS[" + clusterRuleSession.getObjects().size() + "]: ";
			for (Object fact : clusterRuleSession.getObjects()) {
				facts += fact.toString() + " | ";
			}
			log.trace(facts);
		}
	}

	protected void loadClusterRules() throws Exception {
		try {
			log.info("Initializing cluster rules knowledge base: " + clusterRulesChangesetPath + " "
					+ clusterRulesChangesetPathType);

			System.setProperty("drools.dateformat", "dd-MMM-yyyy");
			Properties agentProps = new Properties();
			agentProps.setProperty("drools.agent.scanResources", "false");
			agentProps.setProperty("drools.agent.monitorChangeSetEvents", "false");
			agentProps.setProperty("drools.agent.scanDirectories", "false");

			KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
			conf.setOption(MBeansOption.ENABLED);
			conf.setOption(EventProcessingOption.STREAM);

			KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(conf);

			KnowledgeAgentConfiguration kconfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration(agentProps);
			KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("clusterRuleAgent", kbase, kconfig);

			Resource changeset;
			if (clusterRulesChangesetPathType == CLASSPATH_RESOURCE)
				changeset = ResourceFactory.newClassPathResource(clusterRulesChangesetPath);
			else
				changeset = ResourceFactory.newFileResource(clusterRulesChangesetPath);

			kagent.applyChangeSet(changeset);

			this.clusterRuleBase = kagent.getKnowledgeBase();
		} catch (Exception e) {
			log.error("Unable to load cluster rules " + clusterRulesChangesetPath, e);
			throw e;
		}
	}

	protected void createClusterRuleSession() {
		KnowledgeSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
		// sessConfig.setOption(ClockTypeOption.get("pseudo"));

		clusterRuleSession = clusterRuleBase.newStatefulKnowledgeSession(sessionConfig, null);

		agendaEventListener = new AgendaEventListener();
		clusterRuleSession.addEventListener(agendaEventListener);

		clusterRuleSession.setGlobal("clusterService", this);
		clusterRuleSession.setGlobal("localNodeName", nodeName);
		clusterRuleSession.setGlobal("nodeRepository", nodeRepository);

		klogger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(clusterRuleSession, kloggerOutputPath, 1000);

		log.info("Cluster rule session initialized");
	}

	protected void startClusterServicePoller() {
		scheduler = Executors.newScheduledThreadPool(1);
		clusterServicePoller = new ClusterServicePoller();
		scheduler.scheduleWithFixedDelay(clusterServicePoller, pollIntervalSeconds, pollIntervalSeconds, TimeUnit.SECONDS);
	}

	public boolean isInitialized() {
		return initialized;
	}

	public String getClusterRulesChangesetPath() {
		return clusterRulesChangesetPath;
	}

	public void setClusterRulesChangesetPath(String clusterRulesChangesetPath) {
		this.clusterRulesChangesetPath = clusterRulesChangesetPath;
	}

	public String getClusterRulesChangesetPathType() {
		return clusterRulesChangesetPathType;
	}

	public void setClusterRulesChangesetPathType(String clusterRulesChangesetPathType) {
		this.clusterRulesChangesetPathType = clusterRulesChangesetPathType;
	}

	public String getKloggerOutputPath() {
		return kloggerOutputPath;
	}

	public void setKloggerOutputPath(String kloggerOutputPath) {
		this.kloggerOutputPath = kloggerOutputPath;
	}

	public String getEventEntryPointName() {
		return eventEntryPointName;
	}

	public void setEventEntryPointName(String eventEntryPointName) {
		this.eventEntryPointName = eventEntryPointName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public long getPollIntervalSeconds() {
		return pollIntervalSeconds;
	}

	public void setPollIntervalSeconds(long pollIntervalSeconds) {
		this.pollIntervalSeconds = pollIntervalSeconds;
	}

}
