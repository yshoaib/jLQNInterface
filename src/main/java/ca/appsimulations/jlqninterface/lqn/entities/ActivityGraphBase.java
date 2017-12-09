package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

import java.util.ArrayList;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */
public abstract class ActivityGraphBase extends Entity {
    ArrayList<ActivityDef> activities = new ArrayList<ActivityDef>();

    public ActivityGraphBase(LqnModel lqnModel) {
        this.lqnModel = lqnModel;
    }

    public ArrayList<ActivityDef> getActivities() {
        return activities;
    }
}
