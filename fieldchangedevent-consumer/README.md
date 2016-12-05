What is this Project?


   This project consumes an Insight analytic compliant message from RabbitMQ and invokes Insight analytic execution based on the data changes in the message.


Steps to run this project

1) mvn clean install 
	

2) Start rabbitmqserver		

		rabbitmq-server

3) Rabbitmq server will listen typically on 5672 and rabbitmq administration will listen typically on 15672
	
		Go to the following rabbitmq administration web page: 
			
		http://localhost:15672

		login as guest/guest

4) Run the Dispatcherq consumer code as follows:


	java -jar target/dispatcher-q-1.0.jar --port=<rabbitmq server port> --server=<rabbitmq server host> --mainq=<rabbitmq main queue name> --errorq=<rabbitmq error queue name> --dispatcherhost=<server/hostname only where insight service is running> --dispatcherport=<server port where insight service is listening>

	eg: java -jar target/dispatcher-q-1.0.jar --port=5672 --server=localhost --mainq=fieldchangedeventMainQ --errorq=fieldchangedeventErrorQ --dispatcherhost=localhost --dispatcherport=19090 

5) To run integration tests
	
	mvn clean install -P testharness    
