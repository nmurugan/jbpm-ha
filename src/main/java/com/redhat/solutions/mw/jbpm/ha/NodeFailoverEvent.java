package com.redhat.solutions.mw.jbpm.ha;

import java.io.Serializable;
import java.util.Date;

public class NodeFailoverEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	private String failedNode;
	private String targetNode;
	private Date timestamp;
	
	public String getFailedNode() {
		return failedNode;
	}
	public void setFailedNode(String failedNode) {
		this.failedNode = failedNode;
	}
	public String getTargetNode() {
		return targetNode;
	}
	public void setTargetNode(String targetNode) {
		this.targetNode = targetNode;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((failedNode == null) ? 0 : failedNode.hashCode());
		result = prime * result + ((targetNode == null) ? 0 : targetNode.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
		NodeFailoverEvent other = (NodeFailoverEvent) obj;
		if (failedNode == null) {
			if (other.failedNode != null)
				return false;
		} else if (!failedNode.equals(other.failedNode))
			return false;
		if (targetNode == null) {
			if (other.targetNode != null)
				return false;
		} else if (!targetNode.equals(other.targetNode))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NodeFailoverEvent [failedNode=").append(failedNode).append(", targetNode=").append(targetNode)
				.append(", timestamp=").append(timestamp).append("]");
		return builder.toString();
	}
}
