package com.redhat.solutions.mw.jbpm.ha.camel;

import java.util.Date;

import org.apache.camel.EndpointInject;
import org.apache.camel.InOnly;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.solutions.mw.jbpm.ha.HeartbeatProducer;
import com.redhat.solutions.mw.jbpm.ha.Node;
import com.redhat.solutions.mw.jbpm.ha.NodeHeartbeatEvent;

@InOnly
public class HeartbeatProducerImpl implements HeartbeatProducer {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String destinationName;
	
	private NodeHeartbeatEvent lastHeartbeat; 
	
	@EndpointInject
    protected ProducerTemplate producer;
	
	public void sendHeartbeat(Node node) {
		NodeHeartbeatEvent heartbeat = new NodeHeartbeatEvent();
		heartbeat.setNodeName(node.getNodeName());
		heartbeat.setTimestamp(new Date());
		
		log.debug("Producing heartbeat on destination '" + destinationName + "': " + heartbeat);
   	 	producer.sendBody(destinationName, heartbeat);
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public NodeHeartbeatEvent getLastHeartbeat() {
		return lastHeartbeat;
	}
	
}