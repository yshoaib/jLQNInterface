package ca.appsimulations.jlqninterface.lqn.model.builder;

import ca.appsimulations.jlqninterface.lqn.entities.ActivityPhases;
import ca.appsimulations.jlqninterface.lqn.entities.Entry;
import ca.appsimulations.jlqninterface.lqn.entities.SynchCall;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

public class SyncCallBuilder {
    public static SynchCall build(LqnModel lqnModel, Entry srcEntry, Entry destEntry, double callsMean) {
        SynchCall synchCall = new SynchCall(lqnModel, destEntry, callsMean);
        ActivityPhases activityPhases = srcEntry.getEntryPhaseActivities().getActivityAtPhase(1);
        activityPhases.addSynchCall(synchCall);
        return synchCall;
    }

}