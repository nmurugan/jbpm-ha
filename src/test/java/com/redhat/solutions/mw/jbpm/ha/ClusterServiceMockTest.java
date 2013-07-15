package com.redhat.solutions.mw.jbpm.ha;

import org.junit.Before;
import org.junit.Test;

import com.redhat.solutions.mw.jbpm.ha.impl.ClusterServiceImpl;



public class ClusterServiceMockTest {

	private ClusterService clusterService;	
	
	@Before
	public void initializeClusterService() {
		ClusterServiceImpl cs = new ClusterServiceImpl();
		cs.setNodeName("node1");
		cs.setPollIntervalSeconds(1);
		
		
		
		clusterService = cs;
	}
	
	
	@Test
	public void updateLocalNode() {
		
	}
	
	
	
}
