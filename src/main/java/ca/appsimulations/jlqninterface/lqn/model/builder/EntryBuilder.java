package ca.appsimulations.jlqninterface.lqn.model.builder;

import ca.appsimulations.jlqninterface.lqn.entities.ActivityPhases;
import ca.appsimulations.jlqninterface.lqn.entities.Entry;
import ca.appsimulations.jlqninterface.lqn.entities.EntryAcType;
import ca.appsimulations.jlqninterface.lqn.entities.Task;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

public class EntryBuilder {
    public static Entry build(String name, LqnModel lqnModel, Task task, double serviceDemand) {
        Entry entry = new Entry(lqnModel, name, task);
        entry.setEntryType(EntryAcType.PH1PH2);
        ActivityPhases activityPhases = new ActivityPhases(lqnModel, name, entry, 1);
        activityPhases.setHost_demand_mean(serviceDemand);
        return entry;
    }
}
