package com.redhat.solutions.mw.jbpm.ha.camel;

import org.apache.camel.Exchange;
import org.apache.camel.InOnly;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.redhat.solutions.mw.jbpm.ha.ClusterService;
import com.redhat.solutions.mw.jbpm.ha.NodeHeartbeatEvent;

public class HeartbeatConsumerImpl implements Processor {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ClusterService clusterService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Object event = exchange.getIn().getBody();
		if (!(event instanceof NodeHeartbeatEvent)) {
			log.warn("Received incorrect object type on heartbeat destination: " + event);
			return;
		}

		NodeHeartbeatEvent heartbeat = (NodeHeartbeatEvent) event;

		log.debug("Handling heartbeat event: " + heartbeat);
		clusterService.onHeartbeat(heartbeat);
	}

}
