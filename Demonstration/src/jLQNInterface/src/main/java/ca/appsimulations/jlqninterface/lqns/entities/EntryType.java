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

public abstract class EntryType extends Entity {

	protected String name;
	protected EntryAcType entryType;
	protected float open_arrival_rate;
	protected int priority;
	protected EntrySemaphoreType semaphore;

	protected EntryPhaseActivities entryPhaseActivities;

	public EntryType(Model workspace, String name, EntryAcType entryType) {
		this.workspace = workspace;
		this.name = name;
		this.entryType = entryType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getOpen_arrival_rate() {
		return open_arrival_rate;
	}

	public void setOpen_arrival_rate(float openArrivalRate) {
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

	public EntryPhaseActivities generateEntryPhaseActivities() {
		if (entryPhaseActivities == null) {
			entryPhaseActivities = new EntryPhaseActivities(this.workspace);
		}
		return entryPhaseActivities;
	}

	/*
	 * public void setEntryActivities(EntryPhaseActivities e) {
	 * entryPhaseActivities = e; }
	 */

	/*
	 * public ArrayList<ActivityDefBase> getActivities() { //return activities;
	 * }
	 */

	/*
	 * public ArrayList<ActivityPhases> getActivities() {
	 * if(entryPhaseActivities == null) { return null; } return
	 * entryPhaseActivities.getActivityPhases(); }
	 */

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
