# spring-cloud-sleuth-jdbc-starter

# Over view
This is a module for tracing jdbc usages.
This module is implemented by Spring AOP. So If you want to adopt this module, You shoud consider a cost about proxy latency.
Also, here and now, this project tested in development environment. 

#Dependency
* Spring cloud Sleuth
* Spring cloud zipkin

# Support
* H2 
* Oracle
* Mysql
* MariaDB
* SqlServer(tested by mssql-jdbc)
* PostgreSQL

# TODO
* Test for production
* Deploy to maven repo.
* Code test-cases.