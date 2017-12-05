package ca.appsimulations.jlqninterface.lqn.entities;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

import java.util.ArrayList;

public class Entry extends EntryType {

    private static int nextId = 1;
    private Task task;
    private int nextRepId = 1;
    private ArrayList<String> synchDestStr = new ArrayList<String>();
    private ArrayList<Entry> synchDestEntries = new ArrayList<Entry>();
    private ArrayList<Entry> synchSrcEntries = new ArrayList<Entry>();
    private boolean isDuplicate = false;
    private String duplicatedFrom = "";

    public Entry(LqnModel lqnModel) {
        this(lqnModel, "", null);
    }

    public Entry(LqnModel lqnModel, String name, Task task) {
        super(lqnModel, name, EntryAcType.PH1PH2);
        this.id = nextId++;
        this.task = task;
        lqnModel.addEntry(this);
        task.addEntry(this);

        result = new Result();
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return name;
        /*
         * String s = "Entry " + this.id + " " + this.name + "\n";
         *
         * for (int i = 0; i < destEntries.size(); i++) { s = s + "\t\t\t" +
         * "sync-call dest: " + destEntries.get(i).getName() + "\n"; }
         *
         * return s;
         */
    }

    // mainly called during parsing or during duplicate
    public void addSynchDestStr(String strDestEntry) {
        // TODO
        this.synchDestStr.add(strDestEntry);
    }

    public String getSyncDestStr(int index) {
        return synchDestStr.get(index);
    }

    public int getSyncDestStrSize() {
        return synchDestStr.size();
    }

    // called from linkEntries
    public void addSyncDest(Entry to) {
        if (!this.synchDestEntries.contains(to)) {
            this.synchDestEntries.add(to);
        }

        if (!to.synchSrcEntries.contains(this)) {
            to.synchSrcEntries.add(this);
        }

        this.task.addDestTask(to.task);
    }

    public void clearDestSrcEntries() {
        this.task.clearDestSrcTasks();
    }

    public Result getResult() {
        return result;
    }

    public Entry duplicateDepth(Task parentTask) {
        String newName = this.name + "_re" + nextRepId++;
        Entry e = new Entry(this.lqnModel, newName, parentTask);
        e.isDuplicate = true;
        e.duplicatedFrom = this.name;
        e.entryType = this.entryType;
        e.open_arrival_rate = this.open_arrival_rate;
        e.semaphore = this.semaphore;
        e.priority = this.priority;

        e.entryPhaseActivities = this.entryPhaseActivities.duplicateDepth(e);

        //find srcEntries without calling linkEntries
        ArrayList<Entry> srcEntries = lqnModel.buildSrcEntries(this.name);

        for (Entry src : srcEntries) {
            src.addSyncDest(e); // TODO check this
            ArrayList<ActivityPhases> al = src.getActivitiesByStrDest(this.getName());
            for (ActivityPhases ap : al) {
                SynchCall s = ap.getSynchCallByStrDestEntry(this.getName());
                SynchCall dup = s.duplicate();
                dup.setDestEntry(e);
                ap.addSynchCall(dup);
            }
        }

        return e;
    }

    public String getInformation() {
        StringBuilder strB = new StringBuilder();

        strB.append("\t\t Entry: " + id + " " + name + "\n");
        strB.append("\t\t Throughput: " + result.getThroughput() + "\n");
        strB.append("\t\t Utilization: " + result.getUtilization() + "\n");
        strB.append("\t\t Squared-Coeff-Variaation: " + result.getSquared_coeff_variation() + "\n");
        strB.append("\t\t Proc-Utilization: " + result.getProc_utilization() + "\n");
        if (this.entryType == EntryAcType.NONE) {
            strB.append("\t\t Phase1-service-time: " + result.getPhase1_service_time() + "\n");
        }

        strB.append("\t\t EntryPhaseActivities: ");
        strB.append(entryPhaseActivities);
        strB.append("\n");

        return strB.toString();
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    public String getDuplicatedFrom() {
        return duplicatedFrom;
    }

    public void setDuplicatedFrom(String duplicatedFrom) {
        this.duplicatedFrom = duplicatedFrom;
    }

    public void linkSynchCalls() {
        for (int phase = 1; phase < entryPhaseActivities.getActivitiesSize() + 1; phase++) {
            ActivityPhases ap = entryPhaseActivities.getActivityAtPhase(phase);
            ap.linkSynchCalls();
        }
    }

    public ArrayList<String> getSyncDestStr() {
        return synchDestStr;
    }

    public ArrayList<Entry> getSyncDestEntries() {
        return synchDestEntries;
    }

    public ArrayList<Entry> getSynchSrcEntries() {
        return synchSrcEntries;
    }

    /*
     * public ArrayList<Entry> getDestEntries() { return destEntries; }
     *
     * public ArrayList<Entry> buildSrcEntries() { return srcEntries; }
     */
}
