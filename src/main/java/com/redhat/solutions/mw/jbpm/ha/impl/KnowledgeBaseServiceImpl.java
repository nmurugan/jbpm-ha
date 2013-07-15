package com.redhat.solutions.mw.jbpm.ha.impl;

import java.util.Properties;

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
import org.slf4j.Logger;

public class KnowledgeBaseServiceImpl {
	private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	
	private KnowledgeBase knowledgeBase;

	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}
	
	public void init() {
		log.info("Initializing knowledge base");

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
		KnowledgeAgent knowledgeAgent = KnowledgeAgentFactory.newKnowledgeAgent("MyAgent", kbase, kconfig);

		Resource resource = ResourceFactory.newClassPathResource("ChangeSet.xml");
		knowledgeAgent.applyChangeSet(resource);
		this.knowledgeBase = knowledgeAgent.getKnowledgeBase();
	}

}
