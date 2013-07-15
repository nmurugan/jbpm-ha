package com.redhat.solutions.mw.jbpm.ha;

import java.util.List;


public interface NodeRepository {

	Node findNode(String nodeName);

	List<Node> findNodesByStatus(String status);
	
	List<NodeSession> findAllSessions();

	List<Node> findAllNodes();
	
	void saveNode(Node node);

	void saveOrUpdate(Node node);
	
}
