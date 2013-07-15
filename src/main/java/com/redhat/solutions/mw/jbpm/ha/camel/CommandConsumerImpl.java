package com.redhat.solutions.mw.jbpm.ha.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.redhat.solutions.mw.jbpm.ha.ClusterService;
import com.redhat.solutions.mw.jbpm.ha.KnowledgeSessionService;
import com.redhat.solutions.mw.jbpm.ha.NodeCommand;

public class CommandConsumerImpl implements Processor {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private KnowledgeSessionService ksessionService;
	@Autowired
	private ClusterService clusterService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Object event = exchange.getIn().getBody();
		if(!(event instanceof NodeCommand)) {
			log.warn("Received incorrect object type on command destination: " + event);
			return;
		}
		
		NodeCommand command = (NodeCommand)event;
		
		//if(command.getSessionId() == clusterService.) {
		//  log.debug("Ignoring command event : " + command);
		//	return;
		//}

		log.info("Completing command: " + command);
		ksessionService.completeCommand(command);		
	}
	
}
