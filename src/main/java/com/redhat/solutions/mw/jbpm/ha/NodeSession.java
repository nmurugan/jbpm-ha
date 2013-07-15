package com.redhat.solutions.mw.jbpm.ha;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "ha_node_session")
public class NodeSession implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
    @JoinColumn(name = "NODE_NAME")
	private Node node;
	
	@Column(name="SESSION_ID")
	private int sessionId;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public void moveTo(Node otherNode) {
		if(otherNode == null || otherNode == this.node || otherNode.getNodeName() == this.node.getNodeName() )
			throw new IllegalArgumentException("Cannot reassign NodeSession to " + otherNode);
		
		this.node.getSessions().remove(this);
		this.node = otherNode;
		otherNode.getSessions().add(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		NodeSession other = (NodeSession) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		String nodeName = (node == null)? "null" : node.getNodeName();
		StringBuilder builder = new StringBuilder();
		builder.append("NodeSession [id=").append(id).append(", node=").append(nodeName).append(", sessionId=").append(sessionId).append("]");
		return builder.toString();
	}
}
