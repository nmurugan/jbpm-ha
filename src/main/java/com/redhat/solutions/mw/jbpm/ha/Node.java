package com.redhat.solutions.mw.jbpm.ha;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


@Entity
@Table(name = "ha_node")
public class Node implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ACTIVE_STATUS = "active";
	public static final String FAILED_STATUS = "failed";
	public static final String STOPPED_STATUS = "removed";
	
	@Id
	@Column(name="NODE_NAME")
	private String nodeName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ENTRY_TS")
	private Date clusterEntyTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_HEARTBEAT_TS")
	private Date lastHeartbeat;
	
	@Column(name="STATUS")
	private String status;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy = "node", targetEntity = NodeSession.class)
	private List<NodeSession> sessions;
	
	@Transient
	private boolean isMaster;
	
	@Transient
	private boolean isSecondary;

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Date getClusterEntyTime() {
		return clusterEntyTime;
	}

	public void setClusterEntyTime(Date clusterEntyTime) {
		this.clusterEntyTime = clusterEntyTime;
	}

	public Date getLastHeartbeat() {
		return lastHeartbeat;
	}

	public void setLastHeartbeat(Date lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<NodeSession> getSessions() {
		return sessions;
	}

	public void setSessions(List<NodeSession> sessions) {
		this.sessions = sessions;
	}
	
	public void addSession(NodeSession session) {
		if(sessions == null)
			sessions = new ArrayList<NodeSession>();
		
		sessions.add(session);
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public boolean isSecondary() {
		return isSecondary;
	}

	public void setSecondary(boolean isSecondary) {
		this.isSecondary = isSecondary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeName == null) ? 0 : nodeName.hashCode());
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
		Node other = (Node) obj;
		if (nodeName == null) {
			if (other.nodeName != null)
				return false;
		} else if (!nodeName.equals(other.nodeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Node [nodeName=").append(nodeName).append(", clusterEntyTime=")
			   .append(clusterEntyTime).append(", lastHeartbeat=").append(lastHeartbeat)
			   .append(", status=").append(status).append(", sessions=").append(sessions)
			   .append(", isMaster=").append(isMaster).append(", isSecondary=")
			   .append(isSecondary).append("]");
		return builder.toString();
	}
}

