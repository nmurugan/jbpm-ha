package com.redhat.solutions.mw.jbpm.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;

public class AgendaEventListener extends DefaultAgendaEventListener {
	private final Logger log = Logger.getLogger(this.getClass());

	private List<String> firedRuleList = new ArrayList<String>();
	private int count;

	public Logger getLogger() {
		return log;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<String> getFiredRuleList() {
		return firedRuleList;
	}

	public void setFiredRuleList(List<String> firedRuleList) {
		this.firedRuleList = firedRuleList;
	}

	@Override
	public void activationCancelled(ActivationCancelledEvent event) {
		super.activationCancelled(event);
	}

	@Override
	public void activationCreated(ActivationCreatedEvent event) {
		super.activationCreated(event);

		String rulename = event.getActivation().getRule().getName();
		log.debug("Rule activation created ---> " + rulename);

		List<Object> activatedObjects = event.getActivation().getObjects();
		for (Object ob : activatedObjects) {
			log.debug("activated object: " + ob);
		}
	}

	@Override
	public void afterActivationFired(AfterActivationFiredEvent event) {
		String packageName = event.getActivation().getRule().getPackageName();
		String rulename = event.getActivation().getRule().getName();
		log.debug("Rule fired ---> " + rulename);

		List<Object> activatedObjects = event.getActivation().getObjects();
		for (Object ob : activatedObjects) {
			log.debug("activated object: " + ob);
		}

		firedRuleList.add(rulename);
		count++;
	}

	@Override
	public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
		super.agendaGroupPopped(event);
	}

	@Override
	public void agendaGroupPushed(AgendaGroupPushedEvent event) {
		super.agendaGroupPushed(event);
	}

	@Override
	public void beforeActivationFired(BeforeActivationFiredEvent event) {
		super.beforeActivationFired(event);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public boolean isRuleFired(String ruleName) {
		boolean retBoolean = false;
		for (String firedRule : firedRuleList) {
			if (ruleName.equals(firedRule)) {
				retBoolean = true;
				break;
			}
		}
		return retBoolean;
	}

	public void clear() {
		firedRuleList.clear();
		count = 0;
	}

	@Override
	public String toString() {
		log.debug("-------------total number of Rules fired------------->> " + count);
		/*
		 * if(logger.isDebugEnabled()){
		 * logger.debug("-------------Fired Rules-------------");
		 * System.out.println("-------------Fired Rules-------------"); } for
		 * (String firedRule : firedRuleList ){
		 * 
		 * if(logger.isDebugEnabled()){
		 * logger.debug("Rule information ----> "+firedRule);
		 * System.out.println("Rule information ----> "+firedRule); } }
		 * if(logger.isDebugEnabled()){ System.out.println(
		 * "--------------------------------------------------------------------"
		 * ); System.out.println("total number of rules fired----->>" +
		 * firedRuleList.size()); System.out.println(
		 * "-------------Fired Rules---------------------------------------------"
		 * ); System.out.println(
		 * "--------------------------------------------------------------------"
		 * );
		 * 
		 * logger.debug(
		 * "-------------Fired Rules---------------------------------------------"
		 * ); logger.debug("total number of rules fired-----> " +
		 * firedRuleList.size()); logger.debug(
		 * "-------------Fired Rules---------------------------------------------"
		 * );
		 * 
		 * }
		 */return "";
	}

}
