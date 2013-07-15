package com.redhat.solutions.mw.jbpm.ha;

import org.drools.KnowledgeBase;


public interface KnowledgeSessionService {

	KnowledgeBase getKnowledgeBase();
	
	void recoverSession(long sessionId);
	
	void completeCommand(NodeCommand command);

}
