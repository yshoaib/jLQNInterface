package ca.appsimulations.jlqninterface.core.lqns.entities;
import ca.appsimulations.jlqninterface.core.lqns.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class ActivityMakingCallType extends MakingCallType {
	protected float callsMean;

	public ActivityMakingCallType(LqnModel lqnModel, Entry dstEntry, float callsMean) {
		super(lqnModel, dstEntry);
		this.callsMean = callsMean;
	}

	public ActivityMakingCallType(LqnModel lqnModel, String strDstEntry, float callsMean) {
		super(lqnModel, strDstEntry);
		this.callsMean = callsMean;
	}

	public float getCallsMean() {
		return callsMean;
	}

	public void setCallsMean(float callsMean) {
		this.callsMean = callsMean;
	}
}
