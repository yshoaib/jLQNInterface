package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public class ActivityPhases extends ActivityPhasesType {

    protected int nextRepId = 1;
    private boolean isDuplicate = false;
    private String duplicateFrom = "";

    public ActivityPhases(LqnModel lqnModel, String name, Entry entry, int phase) {
        super(lqnModel, name, 0.0, phase, entry);

        lqnModel.addActivity(this);
        entry.addActivity(this, phase);
        result = new Result();
    }

    public Result getResult() {
        return result;
    }

    public ActivityPhases duplicateDepth(Entry parentEntry) {
        String newName = this.name + "_ra" + nextRepId++;
        ActivityPhases ap = new ActivityPhases(this.lqnModel, newName, parentEntry, this.phase);
        ap.isDuplicate = true;
        ap.duplicateFrom = this.name;
        ap.host_demand_mean = this.host_demand_mean;
        ap.host_demand_cvsq = this.host_demand_cvsq;
        ap.thinkTime = this.thinkTime;
        ap.max_service_time = this.max_service_time;
        ap.call_order = this.call_order;

        for (SynchCall s : synchCalls) {
            SynchCall dupS = s.duplicate();
            ap.addSynchCall(dupS);
        }

        return ap;
    }

    public String getInformation() {
        StringBuilder strB = new StringBuilder();

        strB.append("\t\t\t Activity: " + name + "\n");
        strB.append("\t\t\t Proc-waiting: " + result.getProc_waiting() + "\n");
        strB.append("\t\t\t Host-demand-mean: " + this.host_demand_mean + "\n");
        strB.append("\t\t\t Service-time: " + result.getService_time() + "\n");
        strB.append("\t\t\t Service-time-variance: " + result.getService_time_variance() + "\n");
        strB.append("\t\t\t Utilization: " + result.getUtilization() + "\n");
        strB.append("\n");

        return strB.toString();
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    public String getDuplicateFrom() {
        return duplicateFrom;
    }

    public void setDuplicateFrom(String duplicateFrom) {
        this.duplicateFrom = duplicateFrom;
    }

    public void linkSynchCalls() {
        for (SynchCall s : synchCalls) {
            Entry dest = lqnModel.entryByName(s.getStrDestEntry());
            s.setDestEntry(dest);
        }
    }

}
