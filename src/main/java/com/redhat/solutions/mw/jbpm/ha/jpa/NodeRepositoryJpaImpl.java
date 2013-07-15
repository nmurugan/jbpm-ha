package com.redhat.solutions.mw.jbpm.ha.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.redhat.solutions.mw.jbpm.ha.Node;
import com.redhat.solutions.mw.jbpm.ha.NodeRepository;
import com.redhat.solutions.mw.jbpm.ha.NodeSession;

@Transactional
public class NodeRepositoryJpaImpl implements NodeRepository {
	
	@PersistenceContext
	private EntityManager em;
	
	public Node findNode(String nodeName) {
		Node node = em.find(Node.class, nodeName);
		return node;
	}
	
	@SuppressWarnings("unchecked")
	public List<Node> findAllNodes() {
		Query activeNodeQuery = em.createQuery("SELECT n FROM Node n");
		return (List<Node>)activeNodeQuery.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Node> findNodesByStatus(String status) {
		Query activeNodeQuery = em.createQuery("SELECT n FROM Node n WHERE n.status=:nodeStatus");
		activeNodeQuery.setParameter("nodeStatus", status);
		return (List<Node>)activeNodeQuery.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<NodeSession> findAllSessions() {
		Query activeNodeQuery = em.createQuery("SELECT s FROM NodeSession s");
		return (List<NodeSession>)activeNodeQuery.getResultList();
	}
	
	public void saveNode(Node node) {
		em.persist(node);
	}
	
	public void saveOrUpdate(Node node) {
		em.merge(node);
	}
}
