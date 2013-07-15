HA Component Goals
	Keep all ksessions alive so that timers will fire.
	Route requests to appropriate node to prevent ksession concurrency issues.
HA Component Protocol
	Each node in the cluster has a duplicate configuration, except for a node identifier.
	Each node has a set of Spring beans that broadcast a heartbeat. 
	Node and Session information is stored in a shared database.
	Nodes delegate to each other over messaging - to route commands to the proper session.
	Business rules process the status information to allow for clustering behavior to be modified.
Business Rules 
	All nodes will watch the heartbeat, only the master will act.
	A master node is elected to act on a server failure detection. 
	If the master node fails the next master in the election chain will take over.
	Nodes will have a natural election order based on cluster join time, session count, process count, etc.
DataModel
	Node
	  nodeName : String
	  clusterEntryTime : Date
	  clusterExitTime : Date
	  status : String
	  isMaster : boolean
	  isSecondary : boolean
	NodeSession
	  node : Node
	  sessionId : int
	NodeHeartbeatEvent
	  nodeName : String
	  status : String
	  timestamp : Date
	NodeFailoverEvent
	  failedNode : String
	  targetNode : String
	  timestamp : Date
	NodeCommand
	  sessionId : int
	  workIt : long
	  processId : int
	  processInstanceId : int
	  content : Map<String,Object>
Messaging Destinations
	activemq:topic:jbpmnode.heartbeat : NodeHeartbeatEvent
	activemq:topic:jbpmnode.failure   : NodeFailoverEvent
	activemq:topic:jbpmnode.command   : NodeCommand
Beans
    NodeClusterService
	  nodeName : String <java property jbpm.cluster.node>
      clusterRulesPath : String
	  clusterKnowledgeBase
	  clusterKnowledgeService
	  heartbeatPublisher : HeartbeatPublisher
	  knowledgeSessionService : KnowledgeSessionService
	  recoverNode(NodeFailoverEvent)
	  - startHeartbeat() : HeartbeatPublisher
	  - loadNodes()
	  - init()
	  - loadClusterRules()
	  - onHeartbeat(NodeHeartbeatEvent)
	  - onFailover(NodeFailoverEvent)
	HeartbeatPublisher
	  lastHeartbeat : NodeStatusEvent
	  sendHeartbeat()	
	HeartbeatConsumer
	  nodeClusterService : NodeClusterService
	  receive(msg)
	FailoverConsumer
	  nodeClusterService : NodeClusterService
	  receive(msg)
	CommandConsumer
	  nodeClusterService : NodeClusterService
	  knowledgeSessionService : KnowledgeSessionService
	  receive(msg)
	  locateSession() : NodeSession
	KnowledgeSessionService  
	  entityManager : EntityManager
	  loadStatefulKnowledgeSession(String sessionId)
	  loadSession(String node)
	  recoverSession(int sessionId)
	  completeTask(long id, String userId)
	  delegateTask(long id, String userId, String targetServer)
	  getNodes() : List<Nodes>
	  saveNode() : Node
	  getSessionsByNode(String nodeName) : List<NodeSession>
	  getSessions() : List<NodeSession>
	KnowledgeBaseService
	  knowledgeBase : KnowledgeBase
	  init()

Sample Application
	LoanApplication
