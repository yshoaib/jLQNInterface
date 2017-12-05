package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */
public class TaskActivity extends TaskActivityGraph {

    public TaskActivity(LqnModel lqnModel) {
        super(lqnModel, null, null);
    }

    @Override
    public Result getResult() {
        return null;
    }

    @Override
    public String getInformation() {
        return null;
    }

}
