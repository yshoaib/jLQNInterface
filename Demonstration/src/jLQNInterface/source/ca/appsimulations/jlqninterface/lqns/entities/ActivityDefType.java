package ca.appsimulations.jlqninterface.lqns.entities;
import java.util.ArrayList;

import ca.appsimulations.jlqninterface.core.Model;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class ActivityDefType extends ActivityDefBase {
	protected Entry bound_to_entry;
	protected String strBound_to_entry;
	protected ArrayList<SynchCall> synchCalls = new ArrayList<SynchCall>();

	public ActivityDefType(Model workspace, String name, float host_demand_mean, Entry entry) {
		super(workspace, name, host_demand_mean);
		// TODO Auto-generated constructor stub

		this.bound_to_entry = entry;

		if (entry != null) {
			this.strBound_to_entry = entry.getName();
		} else {
			this.strBound_to_entry = "";
		}

	}

	public Entry getEntry() {
		return bound_to_entry;
	}

	public void setEntry(Entry entry) {
		this.bound_to_entry = entry;
	}

	public String getBound_to_entry() {
		return strBound_to_entry;
	}

	public void setBound_to_entry(String bound_to_entry) {
		this.strBound_to_entry = bound_to_entry;
	}

	public void addSynchCall(SynchCall s) {
		this.synchCalls.add(s);
		this.bound_to_entry.addSynchDestStr(s.strDestEntry);
	}

}
