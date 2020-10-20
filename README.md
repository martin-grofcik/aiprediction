# Getting Started

## How to run 
Install java 11.
and
```
$ mvnw install
$ cd ai-flow
ai-flow$ mvnw spring-boot:run
```
go to http://localhost:8080/ai-flow
login: admin
passwd: test

## ai
The module for the ai implementation.

### How to get request to spark and the results from spark
* [install spark & hadoop]() - description in the discussion thread
* [install kafka](https://kafka.apache.org/quickstart)
* create 2 topics in/out
```
kafka-topics.bat --create --topic in --bootstrap-server localhost:9092
kafka-topics.bat --create --topic out --bootstrap-server localhost:9092
```  
* compile and run ``JavaStructuredKafkaWordCount`` example
```
$ mvn clean package
$ spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1 --class org.crp.aiflow.ml.JavaStructuredKafkaWordCount target\ai-0.0.1-SNAPSHOT-jar-with-dependencies.jar localhost:
9092 subscribe in out
```
* run producer and consumer
```
kafka-console-producer.bat --topic in --bootstrap-server localhost:9092
kafka-console-consumer.bat --from-beginning --topic out -bootstrap-server localhos
```


## ai-flow
Spring boot flowable application.  

[How to integrate with kafka](https://blog.flowable.org/2020/03/24/flowable-business-processing-from-kafka-events/)

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/reference/htmlsingle/#using-boot-devtools)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/reference/htmlsingle/#boot-features-security)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/reference/htmlsingle/#production-ready)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

