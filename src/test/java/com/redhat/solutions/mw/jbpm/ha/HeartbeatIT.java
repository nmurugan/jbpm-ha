package com.redhat.solutions.mw.jbpm.ha;

import static com.redhat.solutions.mw.jbpm.ha.NodeTestUtil.createNode;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-camel-context.xml")
//@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = {"classpath:test-camel-context.xml"})
//@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, DirtiesMocksTestContextListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class HeartbeatIT {

	@Autowired
	private HeartbeatProducer heartbeatProducer;

	//@InjectMocks
	//private HeartbeatConsumerImpl heartbeatConsumer;
	
    //@ReplaceWithMock
	//private KnowledgeSessionService ksessionService;
    //@ReplaceWithMock
	//private ClusterService clusterService;
	
	@Before
	public void setup() {
	//	MockitoAnnotations.initMocks(this);
	}

	@Test
	public void roundtripTest() {
		Node node = createNode("testNode1");
		heartbeatProducer.sendHeartbeat(node);

//		// Get the heartbeat event passed to the mock clusterService onHeartbeat
//		// method
//		ArgumentCaptor<NodeHeartbeatEvent> eventArg = ArgumentCaptor.forClass(NodeHeartbeatEvent.class);
//		verify(clusterService).onHeartbeat(eventArg.capture());
//
//		// assert heartbeat
//		NodeHeartbeatEvent heartbeat = eventArg.getValue();
//		assertNotNull(heartbeat);
//		assertEquals("testNode1", heartbeat.getNodeName());
//		assertNotNull(heartbeat.getTimestamp());
	}
	
	@BeforeClass
	public static void init() {
	   PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}
}
