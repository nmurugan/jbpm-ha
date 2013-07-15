package com.redhat.solutions.mw.jbpm.ha;


public interface HeartbeatProducer {

	void sendHeartbeat(Node node);
	
	NodeHeartbeatEvent getLastHeartbeat();
	
}
