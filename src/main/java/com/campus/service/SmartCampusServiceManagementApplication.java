package com.campus.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * MAIN APPLICATION CLASS - The Entry Point of the Application
 * ============================================================
 *
 * This is where the Spring Boot application starts.
 *
 * KEY CONCEPTS:
 *
 * 1. @SpringBootApplication
 *    This single annotation combines THREE annotations:
 *
 *    a) @Configuration
 *       - Marks this class as a source of bean definitions.
 *       - Spring uses this to configure the application context.
 *
 *    b) @EnableAutoConfiguration
 *       - Tells Spring Boot to automatically configure your application
 *         based on the dependencies in pom.xml.
 *       - For example: since we have spring-boot-starter-web,
 *         Spring Boot auto-configures an embedded Tomcat server.
 *       - Since we have spring-boot-starter-data-jpa,
 *         it auto-configures JPA and Hibernate.
 *
 *    c) @ComponentScan
 *       - Tells Spring to scan the current package (com.campus.service)
 *         and ALL sub-packages for Spring components.
 *       - Components are classes annotated with @Controller, @Service,
 *         @Repository, @Component, etc.
 *       - Spring automatically creates objects (beans) from these classes
 *         and manages their lifecycle.
 *
 * 2. SpringApplication.run()
 *    - This method bootstraps the application:
 *      a) Creates the Spring ApplicationContext (the IoC container)
 *      b) Performs component scanning
 *      c) Creates and wires all beans (Dependency Injection)
 *      d) Starts the embedded Tomcat server
 *      e) The application is now ready to accept HTTP requests
 *
 * INTERVIEW QUESTIONS:
 *
 * Q: "What does @SpringBootApplication do?"
 * A: "It is a convenience annotation that combines @Configuration,
 *     @EnableAutoConfiguration, and @ComponentScan. It tells Spring Boot
 *     to auto-configure the application based on classpath dependencies,
 *     scan for components in the current and sub-packages, and treat
 *     this class as a configuration source."
 *
 * Q: "What is Component Scanning?"
 * A: "Component scanning is a process where Spring automatically
 *     discovers classes annotated with stereotypes like @Controller,
 *     @Service, @Repository in the specified packages, and registers
 *     them as beans in the application context."
 *
 * Q: "What is the ApplicationContext?"
 * A: "The ApplicationContext is the Spring IoC (Inversion of Control)
 *     container. It creates, configures, and manages the lifecycle of
 *     all Spring beans. It also handles dependency injection."
 *
 * Q: "What is an embedded server?"
 * A: "Spring Boot includes an embedded Tomcat server inside the JAR.
 *     This means we don't need to install or configure an external
 *     server. We just run the JAR and the server starts automatically."
 */
@SpringBootApplication
public class SmartCampusServiceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCampusServiceManagementApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  Smart Campus Service Management System");
        System.out.println("  Application started successfully!");
        System.out.println("  Server: http://localhost:8080");
        System.out.println("==============================================");
    }

}
