Reactor is an automation framework, which includes case management, execution engine, and reporting web app, which is in project https://github.com/tascape/reactor-report.  
  
Cases are written in JUnit format, with customized data provider.  
Cases are organized in suites, which also manage the execution environment.  
  
```
<dependency>
    <groupId>com.tascape</groupId>
    <artifactId>reactor</artifactId>
    <version>1.2.0</version>
</dependency>
```

# Setup
1. Clone this project
2. Open it in Intellij
3. Run main class src/main/java/com/tascape/reactor/suite/JUnit4Suite.java
4. In this class src/main/java/com/tascape/reactor/task/JUnit4Case.java#L141, 
   update parameter = “3” to parameter = “100"
5. Run com/tascape/reactor/suite/JUnit4Suite.java again
6. check your local folder for logs ~/.reactor/logs/
