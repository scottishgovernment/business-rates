package org.mygovscot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public void notAUtility() {
        // This is to stop Sonar thinking that this is a utility class.
        // spring-boot requires a default constructor. Checkstyle says that a
        // class with only static methods is a utility class and should not have
        // a default constructor.
    }
}
