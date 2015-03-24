package org.mygovscot;

import org.mygovscot.beta.config.BetaConfigInitializer;
import org.mygovscot.beta.config.Dump;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

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


    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return (container -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.FORBIDDEN, "/403.html");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");

            container.addErrorPages(error401Page, error404Page, error500Page);
        });
    }
}
