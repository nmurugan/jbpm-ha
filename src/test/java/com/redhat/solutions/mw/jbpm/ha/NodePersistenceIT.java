package com.redhat.solutions.mw.jbpm.ha;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static com.redhat.solutions.mw.jbpm.ha.NodeTestUtil.addSessions;
import static com.redhat.solutions.mw.jbpm.ha.NodeTestUtil.createNode;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.redhat.solutions.mw.jbpm.ha.Node;
import com.redhat.solutions.mw.jbpm.ha.NodeSession;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-persistence-context.xml")
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class NodePersistenceIT {

	@PersistenceContext
	private EntityManager em;
	
	@Test
	@Rollback(false)
	public void setupTestData() {
		clearTestData();
		Node nodeX = createNode("nodeX");
		addSessions(nodeX, 2);
		em.persist(nodeX);
		
		Node nodeY = createNode("nodeY");
		addSessions(nodeY, 2);
		em.persist(nodeY);
	}

	@Ignore
	@Test	
	@Rollback(false)
	public void clearTestData() {
		Node nodeX = em.find(Node.class, "nodeX");
		if(nodeX != null) 
			em.remove(nodeX);
		
		Node nodeY = em.find(Node.class, "nodeY");
		if(nodeY != null) 
			em.remove(nodeY);
	}
	
	@Test
	public void saveTest() {
		Node node = createNode("nodeA");
		em.persist(node);

		Node readNode = em.find(Node.class, "nodeA");
		assertNotNull(readNode);
		assertEquals("nodeA", readNode.getNodeName());
	}

	//@Rollback(false)
	@Test
	public void moveSessionTest() {
		Node nodeX = em.find(Node.class, "nodeX");
		Node nodeY = em.find(Node.class, "nodeY");

		NodeSession sess1 = nodeX.getSessions().get(0);
		sess1.moveTo(nodeY);

		em.merge(nodeY);
	}
	
	@Test
	public void saveWithSessionsTest() {
		Node node = createNode("nodeB");
		addSessions(node, 2);
		em.persist(node);

		Node readNode = em.find(Node.class, "nodeB");
		assertNotNull(readNode);
		assertEquals("nodeB", readNode.getNodeName());	
		assertEquals(2, readNode.getSessions().size());
	}
	
	@Test
	public void deleteWithSessionsTest() {
		Node node = createNode("nodeC");
		addSessions(node, 3);
		em.persist(node);

		Node readNode = em.find(Node.class, "nodeC");
		assertNotNull(readNode);
		assertEquals("nodeC", readNode.getNodeName());
		assertEquals(3, readNode.getSessions().size());
		
		long sessionId = readNode.getSessions().get(0).getId();
		NodeSession session1 = em.find(NodeSession.class, sessionId);
		assertNotNull(session1);
		assertNotNull(session1.getNode());
		assertEquals("nodeC", session1.getNode().getNodeName());
				
		List<NodeSession> readSessions = getSessionsByName("nodeC");
		assertNotNull(readSessions);
		assertEquals(3, readSessions.size());
		
		em.remove(readNode);

		List<NodeSession> deletedSessions = getSessionsByName("nodeC");
		assertNotNull(deletedSessions);
		assertEquals(0, deletedSessions.size());
		
		Node deletedNode = em.find(Node.class, "nodeC");
		assertNull(deletedNode);
	}

	@Test
	public void jndiTest() throws NamingException {
		InitialContext context = new InitialContext();
		assertNotNull(context);
		
		Object jbpmDS = context.lookup("jbpmDS");
		assertNotNull(jbpmDS);
	}

	@SuppressWarnings("unchecked")
	private List<NodeSession> getSessionsByName(String nodeName) {
		Query query = em.createQuery("SELECT s FROM NodeSession s WHERE s.node.nodeName=:name");
		query.setParameter("name", nodeName);
		List<NodeSession> sessions = (List<NodeSession>)query.getResultList();
		return sessions;
	}
	
	@BeforeClass
	public static void init() {
	   PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}
}
