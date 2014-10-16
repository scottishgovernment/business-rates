package org.mygovscot;

import org.mygovscot.beta.config.BetaConfigInitializer;
import org.mygovscot.beta.config.Dump;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    public String getConfiguration() {
        return "business-rates-rest-service";
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(Application.class).showBanner(false).application();
        application.addInitializers(new BetaConfigInitializer());
        application.run(args);
        Dump.main(args);
    }

}
