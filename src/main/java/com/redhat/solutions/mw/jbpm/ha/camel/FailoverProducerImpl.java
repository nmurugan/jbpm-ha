package com.redhat.solutions.mw.jbpm.ha.camel;

import java.util.Date;

import org.apache.camel.EndpointInject;
import org.apache.camel.InOnly;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.solutions.mw.jbpm.ha.FailoverProducer;
import com.redhat.solutions.mw.jbpm.ha.Node;
import com.redhat.solutions.mw.jbpm.ha.NodeFailoverEvent;

@InOnly
public class FailoverProducerImpl implements FailoverProducer {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String destinationName;
	
	@EndpointInject
    protected ProducerTemplate producer;
	
	public void broadcastFailover(Node fromNode, Node toNode) {
		NodeFailoverEvent failover = new NodeFailoverEvent();
		failover.setFailedNode(fromNode.getNodeName());
		failover.setTargetNode(toNode.getNodeName());
		failover.setTimestamp(new Date());
		
		log.debug("Producing heartbeat on destination '" + destinationName + "': " + failover);
   	 	producer.sendBody(destinationName, failover);
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

}