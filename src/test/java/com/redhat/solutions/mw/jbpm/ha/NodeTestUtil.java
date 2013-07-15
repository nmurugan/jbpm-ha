package com.redhat.solutions.mw.jbpm.ha;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import com.redhat.solutions.mw.jbpm.ha.Node;
import com.redhat.solutions.mw.jbpm.ha.NodeSession;


public class NodeTestUtil {

	public static Node createNode(String name, String status, int count) {
		Node node = createNode(name);
		node.setStatus(status);
		addSessions(node, count);
		return node;
	}

	public static Node createNode(String name) {
		Node node = new Node();
		node.setNodeName(name);
		node.setStatus("active");
		node.setClusterEntyTime(new Date());
		node.setMaster(true);
		node.setSecondary(false);
		return node;
	}

	public static Node addSessions(Node node, int count) {
		for (int i = 0; i < count; i++) {
			NodeSession session = new NodeSession();
			session.setNode(node);
			session.setSessionId(i);
			node.addSession(session);
		}
		return node;
	}

	public static void assertNode(Node node, String name) {
		assertNotNull(node);
		assertEquals(name, node.getNodeName());
	}

	public static void assertNode(Node node, String name, String status, int count) {
		assertNode(node, name);
		assertEquals(status, node.getStatus());
		assertEquals(2, node.getSessions().size());
	}

	public static void assertSession(NodeSession session, String name, int sessionId) {
		assertNotNull(session);
		assertNotNull(session.getNode());
		assertEquals(name, session.getNode().getNodeName());
		assertEquals(sessionId, session.getSessionId());
	}

	public static void assertContains(List<Node> nodes, String name, String status, int count) {
		Node node = findNodeInList(nodes, name);
		assertNode(node, name, status, count);
	}

	public static void assertContains(List<NodeSession> sessions, String nodeName, int sessionId) {
		NodeSession session = findSessionInList(sessions, nodeName, sessionId);
		assertSession(session, nodeName, sessionId);
	}

	public static Node findNodeInList(List<Node> nodes, String name) {
		for (Node n : nodes) {
			if (n.getNodeName() == name)
				return n;
		}
		return null;
	}
	
	public static NodeSession findSessionInList(List<NodeSession> sessions, String nodeName, int sessionId) {
		for (NodeSession s : sessions) {
			if (s.getNode().getNodeName() == nodeName && s.getSessionId() == sessionId) {
				return s;
			}
		}
		return null;
	}
}
