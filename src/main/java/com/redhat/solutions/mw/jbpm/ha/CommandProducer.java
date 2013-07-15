package com.redhat.solutions.mw.jbpm.ha;


public interface CommandProducer {
	
	void sendCommand(NodeCommand command);
	
}
