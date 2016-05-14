package ca.appsimulations.jlqninterface.lqns.entities;
import ca.appsimulations.jlqninterface.core.Model;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */
public abstract class TaskActivityGraph extends ActivityGraphBase {

	protected Entry replyEntry; // fix this TODO
	protected Entry replyActivity; // fix this TODO

	public TaskActivityGraph(Model workspace, Entry replyEntry, Entry replyActivity) {
		super(workspace);
		//TODO
	}

	public Entry getReplyEntry() {
		return replyEntry;
	}

	public void setReplyEntry(Entry replyEntry) {
		this.replyEntry = replyEntry;
	}

	public Entry getReplyActivity() {
		return replyActivity;
	}

	public void setReplyActivity(Entry replyActivity) {
		this.replyActivity = replyActivity;
	}
}
