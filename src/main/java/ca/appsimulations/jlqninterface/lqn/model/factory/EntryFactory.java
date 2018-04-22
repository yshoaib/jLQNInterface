package ca.appsimulations.jlqninterface.lqn.model.factory;

import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

public class EntryFactory {
    public static Entry build(String name, String activityName, LqnModel lqnModel, Task task, double serviceDemand) {
        return buildEntry(name, activityName, lqnModel, task, serviceDemand);
    }

    private static Entry buildEntry(String name,
                                    String activityName,
                                    LqnModel lqnModel,
                                    Task task,
                                    double serviceDemand) {
        Entry entry = new Entry(lqnModel, name, task);
        entry.setEntryType(EntryAcType.PH1PH2);
        ActivityPhases activityPhases = new ActivityPhases(lqnModel, activityName, entry, 1);
        activityPhases.setHost_demand_mean(serviceDemand);
        return entry;
    }

    public static Entry build(String name, String activityName, LqnModel lqnModel, Task task, double serviceDemand,
                              double thinkTime) {
        Entry entry = buildEntry(name, activityName, lqnModel, task, serviceDemand);
        ActivityDefBase activity = entry.getActivityAtPhase(1);
        activity.setThinkTime(thinkTime);
        return entry;

    }
}
