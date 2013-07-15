package com.redhat.solutions.mw.jbpm.ha;

import static com.redhat.solutions.mw.jbpm.ha.NodeTestUtil.addSessions;
import static com.redhat.solutions.mw.jbpm.ha.NodeTestUtil.assertContains;
import static com.redhat.solutions.mw.jbpm.ha.NodeTestUtil.assertNode;
import static com.redhat.solutions.mw.jbpm.ha.NodeTestUtil.createNode;

import java.util.List;

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

import com.redhat.solutions.mw.jbpm.ha.Node;
import com.redhat.solutions.mw.jbpm.ha.NodeRepository;
import com.redhat.solutions.mw.jbpm.ha.NodeSession;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-persistence-context.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class NodeRepositoryIT {

	@Autowired
	private NodeRepository repo;

	private Node testNode1;
	private Node testNode2;
	
	@Before
	public void before() {
		testNode1 = createNode("testNode1");
		testNode1.setStatus(Node.ACTIVE_STATUS);
		addSessions(testNode1, 2);
		repo.saveNode(testNode1);
		
		testNode2 = createNode("testNode2");
		testNode2.setStatus(Node.FAILED_STATUS);
		addSessions(testNode2, 2);
		repo.saveNode(testNode2);
	}
	
	@Test
	public void findNodeTest() {
		Node node = repo.findNode("testNode1");
		assertNode(node, "testNode1", Node.ACTIVE_STATUS, 2);
	}
	
	@Test
	public void findAllNodesTest() {
		List<Node> nodes = repo.findAllNodes();
		assertContains(nodes, "testNode1", Node.ACTIVE_STATUS, 2);
		assertContains(nodes, "testNode2", Node.FAILED_STATUS, 2);
	}
	
	@Test
	public void findNodesByStatusTest() {
		List<Node> activeNodes = repo.findNodesByStatus(Node.ACTIVE_STATUS);
		assertContains(activeNodes, "testNode1", Node.ACTIVE_STATUS, 2);
		
		List<Node> failedNodes = repo.findNodesByStatus(Node.FAILED_STATUS);
		assertContains(failedNodes, "testNode2", Node.FAILED_STATUS, 2);
	}

	@Test
	public void findAllSessionsTest() {
		List<NodeSession> sessions = repo.findAllSessions();
		assertContains(sessions, "testNode1", 0);
		assertContains(sessions, "testNode1", 1);
		assertContains(sessions, "testNode2", 0);
		assertContains(sessions, "testNode2", 1);
	}
	
	@Test
	public void saveNodeTest() {
		assertNode(testNode1, "testNode1", Node.ACTIVE_STATUS, 2);
	}

	@Test
	public void updateNodeTest() {
		assertNode(testNode1, "testNode1", Node.ACTIVE_STATUS, 2);
		testNode1.setStatus(Node.STOPPED_STATUS);
		testNode1.getSessions().get(1).setNode(testNode2);
		repo.saveOrUpdate(testNode1);
		
		Node updatedNode = repo.findNode("testNode1");
		assertNode(updatedNode, "testNode1", Node.STOPPED_STATUS, 1);
		
		Node affectedNode = repo.findNode("testNode2");
		assertNode(affectedNode, "testNode2", Node.FAILED_STATUS, 3);
	}
	
	@BeforeClass
	public static void init() {
	   PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}
}
