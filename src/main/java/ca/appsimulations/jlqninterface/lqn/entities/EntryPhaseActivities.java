package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public class EntryPhaseActivities extends PhaseActivities {

    public EntryPhaseActivities(LqnModel lqnModel) {
        super(lqnModel);
    }

    public EntryPhaseActivities duplicateDepth(Entry parentEntry) {
        EntryPhaseActivities ePhA = parentEntry.getEntryPhaseActivities();
        for (ActivityPhases ap : this.activityPhases) {
            ap.duplicateDepth(parentEntry);
        }
        return ePhA;

    }

    @Override
    public Result getResult() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getInformation() {
        // TODO Auto-generated method stub
        return null;
    }

}
