package com.redhat.solutions.mw.jbpm.ha;

import java.io.Serializable;
import java.util.Map;

public class NodeCommand implements Serializable {
	private static final long serialVersionUID = 1L;

	private int sessionId;
	private long workItemId;
	private int processId;
	private int processInstanceId;
	private Map<String, Object> content;
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public long getWorkItemId() {
		return workItemId;
	}
	public void setWorkItemId(long workItemId) {
		this.workItemId = workItemId;
	}
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public int getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(int processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public Map<String, Object> getContent() {
		return content;
	}
	public void setContent(Map<String, Object> content) {
		this.content = content;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + processId;
		result = prime * result + processInstanceId;
		result = prime * result + sessionId;
		result = prime * result + (int) (workItemId ^ (workItemId >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeCommand other = (NodeCommand) obj;
		if (processId != other.processId)
			return false;
		if (processInstanceId != other.processInstanceId)
			return false;
		if (sessionId != other.sessionId)
			return false;
		if (workItemId != other.workItemId)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NodeCommand [sessionId=").append(sessionId).append(", workItemId=").append(workItemId)
				.append(", processId=").append(processId).append(", processInstanceId=").append(processInstanceId).append("]");
		return builder.toString();
	}
}
