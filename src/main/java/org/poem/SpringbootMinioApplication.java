package org.poem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Administrator
 */
@SpringBootApplication
public class SpringbootMinioApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringbootMinioApplication.class);

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(SpringbootMinioApplication.class, args);
        ConfigurableApplicationContext application = SpringApplication.run(SpringbootMinioApplication.class, args);

        Environment env = application.getEnvironment();
        logger.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t http://localhost:{}\n\t" +
                        "External: \t http://{}:{}\n\t" +
                        "Doc: \t http://{}:{}/doc.html\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port") + env.getProperty("server.servlet.context-path"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port") + env.getProperty("server.servlet.context-path"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port") + env.getProperty("server.servlet.context-path"));
    }

}
