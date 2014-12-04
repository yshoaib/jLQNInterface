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

public abstract class ActivityPhasesType extends ActivityDefBase {
	protected int phase;
	protected Entry entry;
	protected ArrayList<SynchCall> synchCalls = new ArrayList<SynchCall>();

	public ActivityPhasesType(Model workspace, String name, float host_demand_mean, int phase, Entry entry) {
		super(workspace, name, host_demand_mean);

		this.phase = phase;
		this.entry = entry;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public void addSynchCall(SynchCall s) {
		this.synchCalls.add(s);
		this.entry.addSynchDestStr(s.getStrDestEntry());
	}

	public SynchCall getSynchCallByStrDestEntry(String dest) {
		for (SynchCall s : synchCalls) {
			if (s.strDestEntry.equals(dest)) {
				return s;
			}
		}
		return null;
	}

	public ArrayList<SynchCall> getSynchCalls() {
		return synchCalls;
	}
}
