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

public abstract class EntryType extends Entity {

    protected String name;
    protected EntryAcType entryType;
    protected double open_arrival_rate;
    protected int priority;
    protected EntrySemaphoreType semaphore;
    protected EntryPhaseActivities entryPhaseActivities;

    public EntryType(LqnModel lqnModel, String name, EntryAcType entryType) {
        this.lqnModel = lqnModel;
        this.name = name;
        this.entryType = entryType;
        this.entryPhaseActivities = new EntryPhaseActivities(this.lqnModel);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOpen_arrival_rate() {
        return open_arrival_rate;
    }

    public void setOpen_arrival_rate(double openArrivalRate) {
        open_arrival_rate = openArrivalRate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public EntrySemaphoreType getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(EntrySemaphoreType semaphore) {
        this.semaphore = semaphore;
    }

    public EntryAcType getEntryType() {
        return entryType;
    }

    public void setEntryType(EntryAcType entryType) {
        this.entryType = entryType;
    }

    public EntryPhaseActivities getEntryPhaseActivities() {
        return entryPhaseActivities;
    }

    public void setEntryPhaseActivities(EntryPhaseActivities entryPhaseActivities) {
        this.entryPhaseActivities = entryPhaseActivities;
    }

    public void addActivity(ActivityPhases activity, int phase) {
        entryPhaseActivities.addActivityPhase(activity, phase);
    }

    public ActivityPhases getActivityByName(String name) {
        if (entryPhaseActivities == null) {
            return null;
        }

        int size = entryPhaseActivities.getActivitiesSize();
        for (int i = 0; i < size; i++) {
            ActivityPhases a = entryPhaseActivities.getActivityAtPhase(i + 1);
            if (a.getName().equals(name)) {
                return a;
            }
        }

        return null;
    }

    public ArrayList<ActivityPhases> getActivitiesByStrDest(String destStr) {
        ArrayList<ActivityPhases> al = new ArrayList<ActivityPhases>();

        for (int phase = 1; phase < entryPhaseActivities.getActivitiesSize() + 1; phase++) {
            ActivityPhases ap = entryPhaseActivities.getActivityAtPhase(phase);
            SynchCall s = ap.getSynchCallByStrDestEntry(destStr);

            if (s != null) {
                al.add(ap);
            }
        }
        return al;
    }

    public ActivityDefBase getActivityAtPhase(int phase) {
        return entryPhaseActivities.getActivityAtPhase(phase);
    }

    public int getActivityPhasesSize() {
        return entryPhaseActivities.getActivitiesSize();
    }

}
