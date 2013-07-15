package com.redhat.solutions.mw.jbpm.ha;


public interface ClusterService {

	void invokeHeartbeat();

	void onHeartbeat(NodeHeartbeatEvent heartbeatEvent);

	void onFailover(NodeFailoverEvent failoverEvent);

	void updateLocalNode();

	void fireClusterRules();

	boolean isInitialized();
}
