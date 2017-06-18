package ca.appsimulations.jlqninterface.lqn.entities;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import java.util.*;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

public class Processor extends ProcessorType {
	private static int nextId = 1;
	private ArrayList<Task> tasks;
	private boolean isDuplicate = false;
	private Processor duplicatedFrom;
	private int duplication = 1;

	// private LqnModel workspace;
	// private Result result;

	public Processor(LqnModel lqnModel) {
		this(lqnModel, "");
	}

	public Processor(LqnModel lqnModel, String name) {
		super(lqnModel, name);
		this.id = nextId++;
		tasks = new ArrayList<Task>();
		lqnModel.addProcessor(this);
		result = new Result();
	}

	@Override
	public String toString() {
		return this.name;
		/*
		 * String str = ""; str = str + "Processor " + this.id + " " + this.name
		 * + "\n";
		 * 
		 * for (int i = 0; i < tasks.size(); i++) { str = str + "\t" +
		 * tasks.get(i); }
		 * 
		 * return str;
		 */
	}

	public void addTask(Task t) {
		tasks.add(t);
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getInformation() {
		StringBuilder strB = new StringBuilder();

		strB.append("Processor: " + id + " " + name + "\n");
		strB.append("Utilization: " + result.getUtilization() + "\n");
		strB.append("Tasks: ");
		for (int i = 0; i < tasks.size(); i++) {
			strB.append(tasks.get(i) + ", ");
		}
		strB.append("\n");

		return strB.toString();
	}

	public boolean isDuplicate() {
		return isDuplicate;
	}

	public void setDuplicate(boolean isDuplicate) {
		this.isDuplicate = isDuplicate;
	}

	public Processor getDuplicatedFrom() {
		return duplicatedFrom;
	}

	/**
	 * Duplicates only the processor and not the child tasks.
	 * @return
	 */
	public Processor duplicate() {
		Processor dupParent = this.getDuplicatedParentElseSelf();
		String newName = dupParent.name + "_rp" + dupParent.duplication++;
		Processor p = new Processor(dupParent.lqnModel, newName);
		p.isDuplicate = true;
		p.duplicatedFrom = dupParent;
		p.multiplicity = dupParent.multiplicity;
		p.replication = dupParent.replication;
		p.duplication = dupParent.duplication;
		p.scheduling = dupParent.scheduling;
		p.speedFactor = dupParent.speedFactor;
		p.quantum = dupParent.quantum;

		return p;
	}

	/**
	 * Duplicates processor and child tasks.
	 * @return
	 */
	public boolean duplicateSelfAndTasksDepth() {
		boolean isSuccess = true;
		Processor dupParent = this.getDuplicatedParentElseSelf();
		String newName = dupParent.name + "_rp" + dupParent.duplication++;
		Processor p = new Processor(dupParent.lqnModel, newName);
		p.isDuplicate = true;
		p.duplicatedFrom = dupParent;
		p.multiplicity = dupParent.multiplicity;
		p.replication = dupParent.replication;
		p.duplication = dupParent.duplication;
		p.scheduling = dupParent.scheduling;
		p.speedFactor = dupParent.speedFactor;
		p.quantum = dupParent.quantum;

		ArrayList<Task> childTasks = this.tasks;
		boolean isTaskDuplicated;
		for (Task t : childTasks) {
			isTaskDuplicated = t.setupDuplication(1, false, p);
			isSuccess = isSuccess && isTaskDuplicated;
		}
		return isSuccess;
	}

	private Processor getDuplicatedParentElseSelf() {
		Processor dupParent = this.duplicatedFrom;

		if (dupParent == null) {
			dupParent = this;
		}
		return dupParent;
	}

	public int getDuplicationCount() {
		return this.getDuplicatedParentElseSelf().duplication;
	}

}
