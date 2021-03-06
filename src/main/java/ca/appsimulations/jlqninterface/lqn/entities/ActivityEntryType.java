package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public abstract class ActivityEntryType extends ActivityDefBase {
    protected String first_activity;

    public ActivityEntryType(LqnModel lqnModel, String name, double host_demand_mean, String first_activity) {
        super(lqnModel, name, host_demand_mean);

        this.first_activity = first_activity;
    }

}
