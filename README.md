## Database Verifier

This project is a utility that can be used to test data between two databases (like MySQL), same or different. It can be used for making sure that data migrations are accurate or database upgrades work correctly. 

It is built using the PrestoDB's verifier code-base. It improves upon Presto-DB in two ways:

1. The JDBC Driver's are external dependencies of the project. The JDBC driver jar's can be provided as maven co-ordinates which are resolved at runtime. As a result, using this project we can verify data of any database as long as it supports JDBC.
2. The queries for verifying the data is provided as JSON. This removes any external dependency of storing the queries MySQL.


To use the project, do the following:

1. Download the project code.
2. Perform maven install.

	```mvn clean install```
	
3. Prepare the config.properties.

	```query-json-path=examples/queries.json 
	
       suite=default
	   
       max-row-count=100000
	   
       always-report=true
	   
       test.username=root
	   
       test.password=
	   
       test.gateway=jdbc:mysql://localhost:3306/employees
	   
       test-jdbc-driver-maven-coordinates=mysql:mysql-connector-java:5.1.31
	   
       test-jdbc-driver-name=com.mysql.jdbc.Driver
	   
       control.username=root
	   
       control.password=
	   
       control.gateway=jdbc:mysql://localhost:3306/employees
	   
       control-jdbc-driver-maven-coordinates=mysql:mysql-connector-java:5.1.31
	   
	   thread-count=1```
	   
4. Rename the db-verifier-{version}-executable.jar to  ```db-verifier```, make it executable with ```chmod +x```, then run it:
	 ```./db-verifier config.properties```

