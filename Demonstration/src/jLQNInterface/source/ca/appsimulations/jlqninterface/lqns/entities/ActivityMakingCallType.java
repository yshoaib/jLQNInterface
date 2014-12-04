package ca.appsimulations.jlqninterface.lqns.entities;
import ca.appsimulations.jlqninterface.core.Model;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class ActivityMakingCallType extends MakingCallType {
	protected float callsMean;

	public ActivityMakingCallType(Model workspace, Entry dstEntry, float callsMean) {
		super(workspace, dstEntry);
		this.callsMean = callsMean;
	}

	public ActivityMakingCallType(Model workspace, String strDstEntry, float callsMean) {
		super(workspace, strDstEntry);
		this.callsMean = callsMean;
	}

	public float getCallsMean() {
		return callsMean;
	}

	public void setCallsMean(float callsMean) {
		this.callsMean = callsMean;
	}
}
