package ca.appsimulations.jlqninterface.algorithm;

import ca.appsimulations.jlqninterface.configuration.ConfigurationService;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

@Data
@Accessors(fluent = true, chain = true)
public abstract class Algorithm {
    protected LqnModel lqnModel;
    protected ConfigurationService configurationService;

    public Algorithm(ConfigurationService configurationService, LqnModel lqnModel) {
        this.lqnModel = lqnModel;
        this.configurationService = configurationService;
    }

    public abstract void setup();

    public abstract void run();
}
