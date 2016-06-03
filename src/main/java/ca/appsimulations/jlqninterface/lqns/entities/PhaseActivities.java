package ca.appsimulations.jlqninterface.lqns.entities;
import java.util.ArrayList;

import ca.appsimulations.jlqninterface.core.Model;
import ca.appsimulations.jlqninterface.utilities.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */
public abstract class PhaseActivities extends Entity {
	protected final int MAX_PHASES = 3;
	protected final ArrayList<ActivityPhases> activityPhases = new ArrayList<ActivityPhases>(MAX_PHASES);
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public PhaseActivities(Model workspace) {
		this.workspace = workspace;
	}

	/*
	 * public ArrayList<ActivityPhases> getActivityPhases() { return
	 * activityPhases; }
	 */

	public void addActivityPhase(ActivityPhases a, int phase) {
		// TODO
		if (phase > MAX_PHASES) {
			logger.debug("[PROBLEM]: phase exceed MAX_PHASES (" + MAX_PHASES + ")");
			return;
		}
		activityPhases.add(phase - 1, a);
	}

	public ActivityPhases getActivityAtPhase(int phase) {
		if ((phase - 1) >= activityPhases.size()) {
			return null;
		}
		return activityPhases.get(phase - 1);
	}

	public int getActivitiesSize() {
		return activityPhases.size();
	}

}
