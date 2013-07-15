package com.redhat.solutions.mw.jbpm.ha;


public interface FailoverProducer {

	void broadcastFailover(Node fromNode, Node toNode);

}
