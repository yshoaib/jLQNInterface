package ca.appsimulations.jlqninterface.lqns.entities;
import ca.appsimulations.jlqninterface.core.Model;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class MakingCallType extends Entity {
	protected Entry destEntry;
	protected String strDestEntry;
	protected int fanin = 1;
	protected int fanout = 1;

	public MakingCallType(Model workspace, Entry dstEntry) {
		this.destEntry = dstEntry;
		if (destEntry != null) {
			this.strDestEntry = dstEntry.getName();
		}
	}

	public MakingCallType(Model workspace, String strDestEntry) {
		this.strDestEntry = strDestEntry;
	}

	public Entry getDestEntry() {
		return destEntry;
	}

	public void setDestEntry(Entry destEntry) {
		this.destEntry = destEntry;
		this.strDestEntry = destEntry.getName();
	}

	public int getFanin() {
		return fanin;
	}

	public void setFanin(int fanin) {
		this.fanin = fanin;
	}

	public int getFanout() {
		return fanout;
	}

	public void setFanout(int fanout) {
		this.fanout = fanout;
	}

	public String getStrDestEntry() {
		return strDestEntry;
	}

	public void setStrDestEntry(String strDestEntry) {
		this.strDestEntry = strDestEntry;
	}
}
