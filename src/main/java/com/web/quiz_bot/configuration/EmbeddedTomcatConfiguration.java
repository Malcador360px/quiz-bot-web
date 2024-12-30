package com.web.quiz_bot.configuration;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.*;

@Configuration
@EnableWebMvc
@ComponentScan
public class EmbeddedTomcatConfiguration implements WebMvcConfigurer {
    @Value("${server.port}")
    private String serverPort;

    @Value("${management.port:${server.management}}")
    private String managementPort;

    @Value("${server.additionalPorts:null}")
    private String additionalPorts;

    private final String keystoreAlias = "quiz-bot";

    private final String keystorePass = "Alphazetaomega1!";

    private final Path keystoreFile = FileSystems.getDefault().getPath("src", "main", "resources",
            "META-INF", "resources", "ssl", "quiz-bot.web.jks");

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {

        return factory -> {
            Connector[] additionalConnectors = additionalConnector();
            if (additionalConnectors.length > 0) {
                factory.addAdditionalTomcatConnectors(additionalConnectors);
            }
        };
    }

    private Connector[] additionalConnector() {
        if (this.additionalPorts == null || this.additionalPorts.equals("")) {
            return new Connector[0];
        }
        Set<String> defaultPorts = new HashSet<>(Arrays.asList(this.serverPort, this.managementPort));
        String[] ports = this.additionalPorts.split(",");
        List<Connector> result = new ArrayList<>();
        for (String port : ports) {
            if (StringUtils.hasText(port) && !"null".equalsIgnoreCase(port)
                    && !defaultPorts.contains(port)) {
                Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
                connector.setPort(Integer.parseInt(port.trim()));
                connector.setSecure(true);
                connector.setScheme("https");
                connector.setAttribute("keystoreAlias", keystoreAlias);
                connector.setAttribute("keystorePass", keystorePass);
                connector.setAttribute("keystoreFile", keystoreFile.toFile().getAbsolutePath());
                connector.setAttribute("clientAuth", "false");
                connector.setAttribute("sslProtocol", "TLS");
                connector.setAttribute("SSLEnabled", true);
                result.add(connector);
            }
        }
        return result.toArray(new Connector[] {});
    }
}
