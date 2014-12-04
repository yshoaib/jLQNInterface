package ca.appsimulations.jlqninterface.algorithms;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.io.*;

import ca.appsimulations.jlqninterface.core.Model;
import ca.appsimulations.jlqninterface.lqns.algorithms.BottleneckIdentifier;
import ca.appsimulations.jlqninterface.lqns.entities.ActivityPhases;
import ca.appsimulations.jlqninterface.lqns.entities.Entity;
import ca.appsimulations.jlqninterface.lqns.entities.Entry;
import ca.appsimulations.jlqninterface.lqns.entities.LQNConstants;
import ca.appsimulations.jlqninterface.lqns.entities.Processor;
import ca.appsimulations.jlqninterface.lqns.entities.ProcessorSchedulingType;
import ca.appsimulations.jlqninterface.lqns.entities.Task;
import ca.appsimulations.jlqninterface.lqns.entities.TaskSchedulingType;
import ca.appsimulations.jlqninterface.lqns.modelhandling.*;

import ca.appsimulations.jlqninterface.utilities.Utility;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */
public class Algorithm1 extends Algorithm {

	private LQNXmlModelInputParser lqnInputParser;
	private LQNXmlResultParser lqnResultParser;
	private LQNModifier lqnmod;
	private BottleneckIdentifier botIdentifier;
	private ArrayList<String> messages;

	public Algorithm1(Model workspace) {
		super(workspace);
		this.lqnInputParser = new LQNXmlModelInputParser(workspace);
		this.lqnResultParser = new LQNXmlResultParser(workspace);
		this.lqnmod = new LQNModifier(workspace);
		this.botIdentifier = new BottleneckIdentifier(workspace);
		this.messages = new ArrayList<String>();
	}

	@Override
	public void initialize() {
		Utility.print("===============================================================================================================");
		Utility.print("Initializing...");

		workspace.resetAll();
		// Workspace workspace = new Workspace("./app.properties");

		Utility.debug("Parsing input file " + inputFilePath);
		try {
			lqnInputParser.ParseFile(inputFilePath);
		} catch (FileNotFoundException fnfe) {
			Utility.debug("[FileNotFoundException]: " + fnfe.getMessage());
			fnfe.printStackTrace();
		}

		Utility.debug("Linking entries...");
		workspace.LinkEntries();

		// lqnParser.printProcessors();
		Utility.debug("Building DestTree...");
		workspace.buildDestTree();
		try {
			workspace.organizeTasksBasedBelowSize();
		} catch (Exception e) {
			Utility.debug("[Exception]: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		Utility.debug("");
		workspace.printDestTree();
		Utility.debug("");

		Utility.print("Initial model details:");
		botIdentifier.printBStrengthTable();
		Utility.print("");

		Utility.print("DONE Initializing.");
		Utility.print("");
	}

	//return false if problem solving
	private boolean findBottlenecks(ArrayList<Entity> bStrengthTable, ArrayList<Entity> unchangeable, boolean solveModel) {
		Utility.print("Finding bottleneck...");
		Utility.print("");
		bStrengthTable.clear();

		if (solveModel) {
			boolean isSolved = solveAndParseModel();

			if (!isSolved) {
				return false;
			}
		}

		bStrengthTable.addAll(botIdentifier.generateBStrengthTable(satThreshold, unchangeable, this.bottleneckMaxBStrengthTaskOnly));

		Utility.print("FindingBottleneck...DONE");
		Utility.print("");

		return true;
	}

	private boolean solveAndParseModel() {
		Utility.print("Solving model...");
		Utility.print("");

		Utility.debug("Linking Entries...");
		workspace.LinkEntries();
		Utility.debug("Building Dest Tree...");
		workspace.buildDestTree();
		try {
			workspace.organizeTasksBasedBelowSize();
		} catch (Exception e) {
			Utility.debug("[Exception]: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		workspace.printDestTree();
		Utility.debug("");

		Utility.debug("Parsing and updating XML model");
		Utility.debug("input file: " + inputFilePath);
		Utility.debug("output model to: " + outputFilePath);
		Utility.debug("");

		//update from in-memory representation of the model, output the updated model to outputFilePath
		lqnmod.parseAndUpdateXML(lqnInputParser.getLQXCData(), inputFilePath, outputFilePath);

		boolean result = LQNSolver.solveLqns(outputFilePath, outputFilePath + ".result", lqnResultParser, xmlOutputFilePath);
		if (result == false) {
			Utility.print("Problem solving.");
			return false;
		}
		

		Utility.print("Solving Model...DONE.");
		Utility.print("");
		return true;
	}

	//return response time of first ref type task
	public float findMaxResourcePerformance(ArrayList<Entity> bSet) {
		Utility.print("Finding max resource performance...");
		boolean flag = false;
		HashMap<Entity, Integer> hMap = new HashMap<Entity, Integer>();
		int tmpMaxVMReplicas = this.maxVMReplicas;
		int tmpMaxProcsPerVM = this.maxProcsPerVM;

		while (!flag && (tmpMaxProcsPerVM >= 1 || tmpMaxVMReplicas >= 1)) {
			Utility.print("-----------------------------------------------------------------------------");
			Utility.print("Solving to find maxResourcePerformance: maxVMReplicas: " + tmpMaxVMReplicas + " maxProcsPerVM: " + tmpMaxProcsPerVM);
			Utility.print("-----------------------------------------------------------------------------");

			for (Processor p : workspace.getProcessors()) {

				if (tmpMaxProcsPerVM >= 1 || tmpMaxVMReplicas >= 1) {
					if (p.getMultiplicity() != LQNConstants.INFINITY.getConstantValue() && p.getScheduling() != ProcessorSchedulingType.INF) {
						p.setMultiplicity(Math.max(tmpMaxVMReplicas, tmpMaxProcsPerVM));
					}
				}

				if (tmpMaxVMReplicas >= 1) {
					for (Task t : p.getTasks()) {
						if (t.getMultiplicity() != LQNConstants.INFINITY.getConstantValue() && t.getScheduling() != TaskSchedulingType.INF
								&& t.getScheduling() != TaskSchedulingType.REF) {

							int mult = t.getMultiplicity();
							if (!hMap.containsKey(t)) {
								hMap.put(t, t.getMultiplicity());
							} else {
								mult = hMap.get(t);
							}
							//System.out.println("[Algorithm1] Replicating: " + t);
							t.setMultiplicity((tmpMaxVMReplicas) * mult);

						}
					}
				}
			}
			bSet.clear();
			flag = findBottlenecks(bSet, null, true);

			if (!flag) {
				flag = true;

				if (tmpMaxVMReplicas > 1) {
					tmpMaxVMReplicas--;
					flag = false;
				}
				if (tmpMaxProcsPerVM > 1) {
					tmpMaxProcsPerVM--;
					flag = false;
				}
			}
		}

		if (!flag) {
			return LQNConstants.UNDEF.getConstantValue();
		}

		Utility.print("==============================================================");
		Utility.print("maxReplicasPerVM tried: " + tmpMaxVMReplicas);
		Utility.print("maxProcessorsPerVM tried: " + tmpMaxProcsPerVM);
		Utility.print("Max resource performance");
		float responseTime = this.getFirstRefTaskResponseTime();
		Utility.print("==============================================================");

		Utility.print("Finding max resource performance...DONE");

		return responseTime;
	}

	/*	public float findMaxResourcePerformance() {
			Utility.print("Finding max resource performance...");
			Utility.print("");

			for (Processor p : workspace.getProcessors()) {
				if (p.getMultiplicity() != LQNConstants.INFINITY.getConstantValue() && p.getScheduling() != ProcessorSchedulingType.INF) {
					p.setMultiplicity(Math.max(this.maxReplicationPerTask, this.maxProcessorsPerVM));
				}
				for (Task t : p.getTasks()) {

					if (t.getMultiplicity() != LQNConstants.INFINITY.getConstantValue() && t.getScheduling() != TaskSchedulingType.INF && t.getScheduling() != TaskSchedulingType.REF) {
						//System.out.println("[Algorithm1] Replicating: " + t);
						t.setMultiplicity((this.maxReplicationPerTask + 1) * t.getMultiplicity());

					}
				}
			}
			ArrayList<Entity> bSet = new ArrayList<Entity>();
			boolean flag = findBottlenecks(bSet, true);

			if (!flag) {
				return LQNConstants.UNDEF.getConstantValue();
			}

			Utility.print("Max resource performance");
			float responseTime = this.getFirstRefTaskResponseTime();

			Utility.print("Finding max resource performance...DONE");

			return responseTime;
		}
	*/
	private float getFirstRefTaskResponseTime() {
		ArrayList<Task> refTasks = workspace.findRefTasks();
		float responseTime = this.getFirstRefTaskResponseTime(refTasks, true);
		return responseTime;
	}

	private float getFirstRefTaskResponseTime(ArrayList<Task> refTasks, boolean printInfo) {
		Task t = refTasks.get(0);
		Entry e = refTasks.get(0).getEntries().get(0);
		ActivityPhases ap = (ActivityPhases) refTasks.get(0).getEntries().get(0).getActivityAtPhase(1);
		float responseTime = ap.getResult().getService_time();
		if (printInfo) {
			Utility.print("Task " + t + " entry: " + e + " activity " + ap + " ph1 response time: " + responseTime);
		}

		return responseTime;
	}

	private int setupInitialSolvableConf_OLD(ArrayList<Entity> bSet, ArrayList<Entity> unchangeable, String initialTry) {
		boolean endLoop = false;
		String strChange;
		int changeCount = 0;
		int addedVMs = 0;
		int loopCount = 0;
		Task initialTask = null;
		Utility.print("Finding initial configuration that solves model...");
		Utility.print("");

		while (!endLoop) {
			loopCount++;
			//unable to solve model in first try
			//therefore go in a loop until model is solvable.
			if (loopCount == 1) {
				if (initialTry.equals("") || initialTry == null) {
					continue;
				}

				Task t = workspace.getTaskByName(initialTry);
				if (t == null) {
					continue;
				}
				initialTask = t;
				boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);
				if (!wasDuplicated) {
					Utility.debug("Task " + t + " was not replicated");
					continue;
				}
				changeCount++;
				addedVMs++;
				Utility.print("=======================================");
				strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
				Utility.print(strChange);
				Utility.print("=======================================");
				messages.add(strChange);
			} else {
				ArrayList<Task> ent = (ArrayList<Task>) workspace.getTasks().clone();
				for (Task t : ent) {
					if (t.getMultiplicity() != LQNConstants.INFINITY.getConstantValue() && t.getScheduling() != TaskSchedulingType.INF
							&& t.getScheduling() != TaskSchedulingType.REF) {

						if (loopCount == 2) {
							if (initialTask != null) {
								if (t == initialTask) {
									continue;
								}
							}
						}
						boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);
						if (!wasDuplicated) {
							Utility.debug("Task " + t + " was not replicated");
							continue;
						}
						changeCount++;
						addedVMs++;
						Utility.print("=======================================");
						strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
						Utility.print(strChange);
						Utility.print("=======================================");
						messages.add(strChange);
					}
				}
			}

			/*			if (loopCount == 1) {
				if (initialTry.equals("") || initialTry == null) {
					continue;
				}

				Task t = workspace.getTaskByName(initialTry);
				if (t == null) {
					continue;
				}
				initialTask = t;
				boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);
				if (!wasDuplicated) {
					Utility.debug("Task " + t + " was not replicated");
					continue;
				}
				changeCount++;
				addedVMs++;
				Utility.print("=======================================");
				strChange = "[InitialSolvableConf Change count: " + changeCount + "] " + "TASK: " + t + " m-replication = " + t.getDuplicationCount() + " in next loop";
				Utility.print(strChange);
				Utility.print("=======================================");
				changes.add(strChange);
			} else {
			ArrayList<Processor> ent = (ArrayList<Processor>) workspace.getProcessors().clone();
			for (Processor p : ent) {
				if (p.getMultiplicity() != LQNConstants.INFINITY.getConstantValue() && p.getScheduling() != ProcessorSchedulingType.INF) {
					boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

					if (!wasMultiplicated) {
						Utility.debug("Processor " + p + " was not duplicated");
						continue;
					}

					changeCount++;
					//addedVMs++;
					Utility.print("=======================================");
					strChange = "[InitialSolvableConf Change count: " + changeCount + "] " + "PROCESSOR: " + p + " multiplicity = " + p.getMultiplicity() + " in next loop";
					Utility.print(strChange);
					Utility.print("=======================================");
					changes.add(strChange);
				}
			}
			*/

			endLoop = findBottlenecks(bSet, unchangeable, true);

			Utility.print("Bottlenecks: " + bSet.toString());
			Utility.print("Unchangeable: " + unchangeable.toString());

			if (!endLoop && ((bSet == null) || (bSet.isEmpty()))) {
				if (loopCount > 4) {
					endLoop = true;
				}
				continue;
			} else if ((bSet == null) || (bSet.isEmpty())) {
				Utility.print("No more bottlenecks - DONE");
				endLoop = true;
			}
		}
		Utility.print("Finding initial configuration that solves model...DONE");
		Utility.print("");
		return changeCount;
	}

	private int setupInitialSolvableConf(ArrayList<Entity> bSet, ArrayList<Entity> unchangeable, String initialTry) {
		boolean endLoop = false;
		String strChange;
		int changeCount = 0;
		int addedVMs = 0;
		int loopCount = 0;
		Task initialTask = workspace.getTaskByName(initialTry);
		Utility.print("Finding initial configuration that solves model...");
		Utility.print("");

		Utility.print("Bottlenecks: " + bSet.toString());
		Utility.print("Unchangeable: " + unchangeable.toString());

		if (initialTask != null) {
			if (!bSet.contains(initialTask)) {
				bSet.add(0, initialTask);
			}
		}

		ArrayList<Entity> bSetClone = (ArrayList<Entity>) bSet.clone();
		bSet.clear();
		for (Entity b : bSetClone) {
			if (b instanceof Task) {
				Task t = workspace.getTaskByName(((Task) b).getName());

				if (bSet.contains(t.getProcessor())) {
					//since processor of this task is also a bottleneck, first add the processor
					Utility.print("Skipping task " + t + " duplication as parent processor is also bottleneck");
					continue;
				}

				boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);

				if (!wasDuplicated) {
					Utility.debug("Task " + t + " was not replicated");
					continue;
				}

				changeCount++;
				addedVMs++;
				Utility.print("=======================================");
				strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
				Utility.print(strChange);
				Utility.print("=======================================");
				messages.add(strChange);
			} else if (b instanceof Processor) {
				Processor p = workspace.getProcessorByName(((Processor) b).getName());

				boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

				if (!wasMultiplicated) {
					Utility.debug("Processor " + p + " was not duplicated");
					continue;
				}

				changeCount++;
				Utility.print("=======================================");
				strChange = "InitialSolvableConf Change" + changeCount + ": " + "PROC: " + p + " multiplicity = " + p.getMultiplicity() + " in next loop";
				Utility.print(strChange);
				Utility.print("=======================================");
				messages.add(strChange);
			}

			endLoop = findBottlenecks(bSet, unchangeable, true);

			Utility.print("Bottlenecks: " + bSet.toString());
			Utility.print("Unchangeable: " + unchangeable.toString());

			if (endLoop) {
				Utility.print("No more bottlenecks - DONE");
				break;
			} else if (!endLoop && ((bSet == null) || (bSet.isEmpty()))) {
				if (loopCount > 4) {
					endLoop = true;
					break;
				}
			} else if ((bSet == null) || (bSet.isEmpty())) {
				Utility.print("No more bottlenecks - DONE");
				endLoop = true;
				break;
			}
		}

		//bSet.clear();

		while (!endLoop) {
			loopCount++;
			//unable to solve model in first try
			//therefore go in a loop until model is solvable.
			ArrayList<Task> ent = (ArrayList<Task>) workspace.getTasks().clone();
			for (Task t : ent) {
				if (t.getMultiplicity() != LQNConstants.INFINITY.getConstantValue() && t.getScheduling() != TaskSchedulingType.INF && t.getScheduling() != TaskSchedulingType.REF) {
					boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);
					if (!wasDuplicated) {
						Utility.debug("Task " + t + " was not replicated");
						continue;
					}
					changeCount++;
					addedVMs++;
					Utility.print("=======================================");
					strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
					Utility.print(strChange);
					Utility.print("=======================================");
					messages.add(strChange);
				}
			}

			/*
			ArrayList<Processor> ent = (ArrayList<Processor>) workspace.getProcessors().clone();
			for (Processor p : ent) {
				if (p.getMultiplicity() != LQNConstants.INFINITY.getConstantValue() && p.getScheduling() != ProcessorSchedulingType.INF) {
					boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

					if (!wasMultiplicated) {
						Utility.debug("Processor " + p + " was not duplicated");
						continue;
					}

					changeCount++;
					//addedVMs++;
					Utility.print("=======================================");
					strChange = "[InitialSolvableConf Change count: " + changeCount + "] " + "PROCESSOR: " + p + " multiplicity = " + p.getMultiplicity() + " in next loop";
					Utility.print(strChange);
					Utility.print("=======================================");
					changes.add(strChange);
				}
			}
			*/

			endLoop = findBottlenecks(bSet, unchangeable, true);

			Utility.print("Bottlenecks: " + bSet.toString());
			Utility.print("Unchangeable: " + unchangeable.toString());

			if (endLoop) {
				Utility.print("No more bottlenecks - DONE");
				break;
			} else if (!endLoop && ((bSet == null) || (bSet.isEmpty()))) {
				if (loopCount > 4) {
					endLoop = true;
				}
				continue;
			} else if ((bSet == null) || (bSet.isEmpty())) {
				Utility.print("No more bottlenecks - DONE");
				endLoop = true;
			}
		}
		Utility.print("Finding initial configuration that solves model...DONE");
		Utility.print("");
		return changeCount;
	}

	/**
	 * @param t
	 * @param duplicationAdded
	 * @param unchangeable
	 * @param addedVMs
	 * @return if process was duplicated
	 */
	private boolean processTaskDuplication(Task t, int duplicationAdded, ArrayList<Entity> unchangeable, int addedVMs) {
		boolean wasDuplicated = true;

		if (t.getDuplicationCount() >= this.maxVMReplicas) {
			if (unchangeable != null && (!unchangeable.contains(t))) {
				unchangeable.add(t);
			}
			Utility.print(t.getName() + " has reached maxReplicas: ");
			return false;
		} else if (addedVMs >= this.spareVMs) {
			if (unchangeable != null && (!unchangeable.contains(t))) {
				unchangeable.add(t);
			}
			Utility.print("No more VMs available.");
			return false;
		}

		//wasDuplicated = t.setupDuplication(duplicationAdded);
		wasDuplicated = t.getProcessor().duplicateSelfAndTasksDepth();

		return wasDuplicated;
	}

	private boolean processProcessorMultiplication(Processor p, int multiplicityAdded, ArrayList<Entity> unchangeable) {
		if (p.getMultiplicity() >= this.maxProcsPerVM) {
			if (!unchangeable.contains(p)) {
				unchangeable.add(p);
			}

			Utility.print(p.getName() + " " + "multiplicity: " + p.getMultiplicity() + " has reached maxReplicas: " + maxProcsPerVM);
			return false;
		}
		p.setMultiplicity(p.getMultiplicity() + multiplicityAdded);

		return true;
	}

	@Override
	public void run() {
		String strMsg;
		int addedVMs = 0;
		int noChangesCount = 0;
		ArrayList<Entity> bSet = new ArrayList<Entity>();
		ArrayList<Entity> bSetMaxRes = new ArrayList<Entity>();

		initialize();

		float maxResResponse = findMaxResourcePerformance(bSet);
		bSetMaxRes = (ArrayList<Entity>) bSet.clone();

		if (maxResResponse == LQNConstants.UNDEF.getConstantValue()) {
			botIdentifier.printBStrengthTable();
			Utility.print("Unable to find model solution for maximum resources. DONE.");
			return;
		} else if (maxResResponse > responseTimeObjective) {
			botIdentifier.printBStrengthTable();
			Utility.print("Max Resource ResponseTime: " + maxResResponse);
			Utility.print("ResponseTime Objective: " + responseTimeObjective);
			Utility.print("Unable to find model solution: Max Resource ResponseTime > responseTimeObjective. DONE");
			return;
		}

		//get maxBStrength task name based on model run for findMaxResourcePerformance.
		String initialTry = null;
		if (botIdentifier.getMaxBStrengthTask() == null) {
			Utility.printAndDebug("No maximum bottleneck Task");
		} else {
			initialTry = botIdentifier.getMaxBStrengthTask().getName();
			initialize();
		}

		int loopCount = 1;
		int changeCount = 0;
		ArrayList<Task> refTasks = workspace.findRefTasks();
		ArrayList<Entity> unchangeable = new ArrayList<Entity>();
		int curChangesCount = 0;
		while (true) {
			bSet.clear();
			boolean flag = findBottlenecks(bSet, unchangeable, true);

			//if unable to solve model in the first try
			if (flag == false && (loopCount == 1)) {
				int initialChanges = this.setupInitialSolvableConf(bSetMaxRes, unchangeable, initialTry);
				findBottlenecks(bSet, unchangeable, true);
				changeCount = changeCount + initialChanges;
				if (initialChanges == 0) {
					Utility.print("Unable to find model solution");
					break;
				}
			} else if (flag == false) {
				botIdentifier.printBStrengthTable();
				Utility.print("Unable to find model solution");
				break;
			}

			float responseTime = this.getFirstRefTaskResponseTime(refTasks, false);
			if (this.responseTimeObjective >= responseTime) {
				Utility.print("Met response Time Objectives - DONE");
				Utility.print(String.format("Objective: %.3f, ResponseTime: %.3f", responseTimeObjective, responseTime));
				break;
			}

			Utility.print("Bottlenecks: " + bSet.toString());
			Utility.print("Unchangeable: " + unchangeable.toString());

			if ((bSet == null) || (bSet.isEmpty())) {
				Utility.print("No more bottlenecks - DONE");
				break;
			}

			curChangesCount = 0;
			for (Entity b : bSet) {
				if (b instanceof Task) {
					Task t = (Task) b;

					if (bSet.contains(t.getProcessor())) {
						//since processor of this task is also a bottleneck, first add the processor
						Utility.print("Skipping task " + t + " duplication as parent processor is also bottleneck");
						continue;
					}

					boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);

					if (!wasDuplicated) {
						strMsg = "Loop" + loopCount + " Limit reached: TASK: " + t + " VM not replicated in next loop";
						messages.add(strMsg);
						Utility.printAndDebug(strMsg);
						continue;
					}

					changeCount++;
					addedVMs++;
					Utility.print("=======================================");
					strMsg = "Loop" + loopCount + " Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
					Utility.print(strMsg);
					Utility.print("=======================================");
					messages.add(strMsg);
					curChangesCount++;

				} else if (b instanceof Processor) {
					Processor p = (Processor) b;

					boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

					if (!wasMultiplicated) {
						strMsg = "Loop" + loopCount + " Limit reached: PROC: " + p + " not duplicated in next loop";
						messages.add(strMsg);
						Utility.printAndDebug(strMsg);
						continue;
					}

					changeCount++;
					Utility.print("=======================================");
					strMsg = "Loop" + loopCount + " Change" + changeCount + ": " + "PROC: " + p + " multiplicity = " + p.getMultiplicity() + " in next loop";
					Utility.print(strMsg);
					Utility.print("=======================================");
					messages.add(strMsg);
					curChangesCount++;
				}
			}
			Utility.print("Loop " + loopCount + " DONE------------------------------------------------------------------------------");
			Utility.print("Running...");
			Utility.print("");

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (curChangesCount == 0) {
				noChangesCount++;
			} else {
				noChangesCount = 0;
			}

			if (noChangesCount >= 5) {
				Utility.print("No changes for previous 5 loops. DONE");
				break;
			}
			loopCount++;
		}

		Utility.print("Summary of changes");
		Utility.print("----------------------");
		for (int i = 0; i < messages.size(); i++) {
			Utility.print(messages.get(i));
		}

	}
}
