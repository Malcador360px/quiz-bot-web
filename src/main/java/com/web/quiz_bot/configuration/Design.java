package com.web.quiz_bot.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import javax.annotation.PostConstruct;

@Configuration
@PropertySource({"classpath:design.properties"})
public class Design {

    private final Environment environment;
    public static String DEFAULT_FONT;

    @Autowired
    public Design(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void setDesign() {
        DEFAULT_FONT = environment.getProperty("default_font");
    }
}
