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

public class SynchCall extends ActivityMakingCallType {
	private ArrayList<SynchCall> duplicationList = new ArrayList<SynchCall>();

	public SynchCall(Model workspace, Entry dstEntry, float callsMean) {
		super(workspace, dstEntry, callsMean);
	}

	public SynchCall(Model workspace, String strDstEntry, float callsMean) {
		super(workspace, strDstEntry, callsMean);
	}

	public SynchCall duplicate() {
		SynchCall s = new SynchCall(this.workspace, this.strDestEntry, this.callsMean);
		s.fanin = this.fanin;
		s.fanout = this.fanout;
		this.addToDuplicationList(s);
		return s;
	}

	private void addToDuplicationList(SynchCall s) {
		s.duplicationList = this.duplicationList;
		if (!this.duplicationList.contains(this)) {
			this.duplicationList.add(this);
		}
		this.duplicationList.add(s);
	}

	public ArrayList<SynchCall> getDuplicationList() {
		return this.duplicationList;
	}

	@Override
	public Result getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return this.getStrDestEntry() + " " + callsMean;
	}

}
