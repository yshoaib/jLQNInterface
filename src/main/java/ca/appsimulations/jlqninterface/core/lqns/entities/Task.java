package ca.appsimulations.jlqninterface.core.lqns.entities;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import java.util.*;

import ca.appsimulations.jlqninterface.core.lqns.model.LqnModel;
import ca.appsimulations.jlqninterface.utilities.Utility;

public class Task extends TaskType {
	private Processor processor;
	private ArrayList<Entry> entries = new ArrayList<Entry>();
	private ArrayList<Task> destTasks = new ArrayList<Task>();
	private ArrayList<Task> srcTasks = new ArrayList<Task>();
	private ArrayList<Task> below = new ArrayList<Task>();
	private boolean isDuplicate = false;
	private Task duplicatedFrom = null;
	private ArrayList<Task> duplicates;
	private int duplication = 1;
	// private LqnModel workspace;
	// private Result result;
	private static int nextId = 1;

	private boolean isReplicable = true;

	public Task(LqnModel lqnModel) {
		this(lqnModel, "", null);
	}

	public Task(LqnModel lqnModel, String name, Processor processor) {
		super(lqnModel, name, false);
		this.id = nextId++;
		this.setProcessor(processor);
		lqnModel.addTask(this);
		processor.addTask(this);

		this.result = new Result();
	}

	@Override
	public String toString() {
		return this.name;
		/*
		 * String str = ""; str = str + "Task " + this.id + " " + this.name +
		 * "\n";
		 * 
		 * for (int i = 0; i < entries.size(); i++) { str = str + "\t\t" +
		 * entries.get(i); }
		 * 
		 * return str;
		 */
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void addEntry(Entry e) {
		entries.add(e);
	}

	public int getEntrySize() {
		return entries.size();
	}

	public Entry getEntryAtIndex(int index) {
		return entries.get(index);
	}

	public void addDestTask(Task t) {
		if (!destTasks.contains(t)) {
			this.destTasks.add(t);
		}
		if (!t.srcTasks.contains(this)) {
			t.srcTasks.add(this);
		}
	}

	public void clearDestSrcTasks() {
		this.destTasks.clear();
		this.srcTasks.clear();
		this.below.clear();
	}

	public void addToBelow(Task t) {
		if (!this.below.contains(t)) {
			this.below.add(t);
		}
	}

	private void addAllToBelow(ArrayList<Task> a) {
		if (a.size() <= 0) {
			return;
		}
		for (int m = 0; m < a.size(); m++) {
			this.addToBelow(a.get(m));
		}
	}

	public void addAllTasksBelowtoBelow(Task t) {
		addAllToBelow(t.below);
	}

	public void clearBelow() {
		below.clear();
	}

	public int getBelowSize() {
		return below.size();
	}

	public int getSrcTasksSize() {
		return this.srcTasks.size();
	}

	public int getDestTasksSize() {
		return this.destTasks.size();
	}

	public Task getDestTaskAtIndex(int index) {
		return this.destTasks.get(index);
	}

	public Task getSrcTaskAtIndex(int index) {
		return this.srcTasks.get(index);
	}

	public Task getBelowAtIndex(int index) {
		return this.below.get(index);
	}

	public ArrayList<Task> getBelow() {
		return below;
	}

	public String getBelowQutotationStrArray() {
		return Utility.listToQuotationStrArray(below);
	}

	public ArrayList<Entry> getEntries() {
		return entries;
	}

	public Result getResult() {
		return result;
	}

	private Task duplicateDepth(Processor parentProc) {
		Task dupParent = this.getDuplicatedParentElseSelf();
		String newName = dupParent.name + "_rt" + dupParent.duplication++;
		Task t = new Task(dupParent.lqnModel, newName, parentProc);
		t.isDuplicate = true;
		t.duplicatedFrom = dupParent;
		t.priority = dupParent.priority;
		t.multiplicity = dupParent.multiplicity;
		t.replication = dupParent.replication;
		t.duplication = dupParent.duplication;
		t.think_time = dupParent.think_time;
		t.subclass = dupParent.subclass;
		t.sat = dupParent.sat;
		t.scheduling = dupParent.scheduling;
		t.queue_length = dupParent.queue_length;
		t.activity_graph = dupParent.activity_graph;
		if (dupParent.duplicates == null) {
			dupParent.duplicates = new ArrayList<Task>();
		}
		dupParent.duplicates.add(t);

		for (Entry e : dupParent.entries) {
			e.duplicateDepth(t);
		}

		return t;
	}

	public ArrayList<Task> getDuplicates() {
		return duplicatedFrom.duplicates;
	}

	private Task getDuplicatedParentElseSelf() {
		Task dupParent = this.duplicatedFrom;

		if (dupParent == null) {
			dupParent = this;
		}
		return dupParent;

	}

	private Task duplicateDepthAndParent() {
		Processor p = this.processor.duplicate();
		return this.duplicateDepth(p);
	}

	public String getInformation() {
		StringBuilder strB = new StringBuilder();

		strB.append("\t Task: " + id + " " + name + "\n");
		strB.append("\t Processor: " + this.processor + "\n");
		strB.append("\t multiplicty: " + this.multiplicity + "\n");
		strB.append("\t replication: " + this.replication + "\n");
		strB.append("\t duplication: " + this.duplication + "\n");
		strB.append("\t think_time: " + this.think_time + "\n");
		strB.append("\t sub_class: " + this.subclass + "\n");
		strB.append("\t sat: " + this.sat + "\n");
		strB.append("\t scheduling: " + this.scheduling + "\n");
		strB.append("\t queue_length: " + this.queue_length + "\n");
		strB.append("\t activity_graph: " + this.activity_graph + "\n");
		strB.append("\t Results" + "\n");
		strB.append("\t Throughput: " + result.getThroughput() + "\n");
		strB.append("\t Utilization: " + result.getUtilization() + "\n");
		strB.append("\t Phase1-Utilization: " + result.getPhase1_utilization() + "\n");
		strB.append("\t Proc-Utilization: " + result.getProc_utilization() + "\n");

		strB.append("\t Entries: ");
		for (int i = 0; i < entries.size(); i++) {
			strB.append(entries.get(i) + ", ");
		}
		strB.append("\n");

		return strB.toString();
	}

	public boolean isDuplicate() {
		return isDuplicate;
	}

	public Task getDuplicatedFrom() {
		return duplicatedFrom;
	}

	public boolean setupDuplication(int duplicationCount, boolean duplicateParentProc, Processor parentProc) {
		if (!isReplicable) {
			return false;
		}
		if (!duplicateParentProc && (parentProc == null)) {
			return false;
		}

		//int updatedDupCount = this.nextDupId + duplicationCount;
		int numberDuplications = this.getDuplicationCount();
		int updatedDupCount = this.getDuplicationCount() + duplicationCount;

		for (Entry e : entries) {
			ArrayList<Entry> srcEntries = lqnModel.getSrcEntries(e.getName());
			for (Entry src : srcEntries) {
				ActivityPhases a = (ActivityPhases) src.getActivityAtPhase(1);
				SynchCall srcSynch = a.getSynchCallByStrDestEntry(e.getName());
				float oldCallsMean = srcSynch.getCallsMean();
				float updatedCallsMean = (oldCallsMean * numberDuplications) / (updatedDupCount);
				srcSynch.setCallsMean(updatedCallsMean);

				for (SynchCall srcInDupList : srcSynch.getDuplicationList()) {
					String strMyDest = srcSynch.getStrDestEntry();
					String strOtherDest = srcInDupList.getStrDestEntry();
					if (!strMyDest.equals(strOtherDest)) {
						srcInDupList.setCallsMean(updatedCallsMean);
					}
				}
			}
		}

		for (int i = 0; i < duplicationCount; i++) {
			Task dup;
			if (duplicateParentProc) {
				dup = this.duplicateDepthAndParent();
			} else {
				dup = this.duplicateDepth(parentProc);
			}
		}
		return true;
	}

	public int getDuplicationCount() {
		return this.getDuplicatedParentElseSelf().duplication;
	}

	public boolean isReplicable() {
		return isReplicable;
	}

	public void setReplicable(boolean isReplicable) {
		this.isReplicable = isReplicable;
	}
}
