package ca.appsimulations.jlqninterface.core.lqns.entities;
import ca.appsimulations.jlqninterface.core.lqns.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public class ActivityDef extends ActivityDefType {

	public ActivityDef(LqnModel lqnModel) {
		this(lqnModel, "", null, null, null);
	}

	public ActivityDef(LqnModel lqnModel, String name, Task t, TaskActivities tA, Entry entry) {

		super(lqnModel, name, 0.0f, entry);

		lqnModel.addActivity(this);

		t.addActivityToTaskActivity(this, tA);

		result = new Result();
	}

	public Result getResult() {
		return result;
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

	public SynchCall getSynchCallByStrDestEntry(String dest) {
		for (SynchCall s : synchCalls) {
			if (s.strDestEntry.equals(dest)) {
				return s;
			}
		}
		return null;
	}

}
