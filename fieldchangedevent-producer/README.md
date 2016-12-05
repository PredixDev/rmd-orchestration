What is this Project?

	This project produces an Insight analytic compliant message and queues that into RabbitMQ. 

How to integrate this to your java application

	1) Add the following maven coordinates

			<groupId>com.ge.predix.dispatcherqproducer</groupId>
			<artifactId>dispatcherq-producer</artifactId>
			<version>1.0</version>

	2) Autowire this bean into your application like this:
	
		    @Autowired
         	    private NotifyFieldChangedEvent notifyToQ;

	3) Create FieldChangedEvent object

	4) Invoke
		notifyToQ.notify(FieldChangedEvent object)

How to build this project

	1) mvn clean install

	2) Start rabbitmqserver

	    rabbitmq-server

	3) To run integration tests

		mvn clean install -P testharness  -Dserver=<rabbitmqhost> -Dport=<rabbitmq port>
