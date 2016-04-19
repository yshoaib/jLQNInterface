package ca.appsimulations.jlqninterface.drivers;

import ca.appsimulations.jlqninterface.algorithms.Algorithm;
import ca.appsimulations.jlqninterface.algorithms.Algorithm1;
import ca.appsimulations.jlqninterface.core.Model;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ca.appsimulations.jlqninterface.algorithms.Algorithm;
import ca.appsimulations.jlqninterface.algorithms.Algorithm1;
/**
 * Created by maverick on 2016-04-12.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        Model m = new Model("/Users/maverick/Documents/git/Software/Workspace/jLQNInterface/Demonstration/src/jLQNInterface/src/main/resources/application.properties");
        new Algorithm1(m).run();

    }
}
