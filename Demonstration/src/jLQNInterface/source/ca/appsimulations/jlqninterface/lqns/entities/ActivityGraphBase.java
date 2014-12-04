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
public abstract class ActivityGraphBase extends Entity {
	ArrayList<ActivityDef> activities = new ArrayList<ActivityDef>();

	public ActivityGraphBase(Model workspace) {
		this.workspace = workspace;
	}

	public ArrayList<ActivityDef> getActivities() {
		return activities;
	}
}
