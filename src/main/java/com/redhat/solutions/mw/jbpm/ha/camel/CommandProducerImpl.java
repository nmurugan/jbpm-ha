package com.redhat.solutions.mw.jbpm.ha.camel;

import java.util.Date;

import org.apache.camel.EndpointInject;
import org.apache.camel.InOnly;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.solutions.mw.jbpm.ha.CommandProducer;
import com.redhat.solutions.mw.jbpm.ha.FailoverProducer;
import com.redhat.solutions.mw.jbpm.ha.Node;
import com.redhat.solutions.mw.jbpm.ha.NodeCommand;
import com.redhat.solutions.mw.jbpm.ha.NodeFailoverEvent;

@InOnly
public class CommandProducerImpl implements CommandProducer {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String destinationName;
	
	@EndpointInject
    protected ProducerTemplate producer;
	
	public void sendCommand(NodeCommand command) {
		log.debug("Producing command to destination '" + destinationName + "': " + command);
   	 	producer.sendBody(destinationName, command);
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

}