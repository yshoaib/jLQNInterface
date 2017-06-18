package ca.appsimulations.jlqninterface.bottleneck;
import ca.appsimulations.jlqninterface.algorithm.Algorithm;
import ca.appsimulations.jlqninterface.core.lqns.model.LqnModel;
import ca.appsimulations.jlqninterface.core.lqns.entities.*;
import ca.appsimulations.jlqninterface.core.lqns.model.handler.LqnModifier;
import ca.appsimulations.jlqninterface.core.lqns.model.handler.LqnSolver;
import ca.appsimulations.jlqninterface.core.lqns.model.parser.LqnXmlModelInputParser;
import ca.appsimulations.jlqninterface.core.lqns.model.parser.LqnXmlResultParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */
@Component
public class Algorithm1 extends Algorithm {

	private LqnXmlModelInputParser lqnInputParser;
	private LqnXmlResultParser lqnResultParser;
	private LqnModifier lqnmod;
	private BottleneckIdentifier botIdentifier;
	private ArrayList<String> messages;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Autowired
	public Algorithm1(LqnModel lqnModel) {
		super(lqnModel);
		this.lqnInputParser = new LqnXmlModelInputParser(lqnModel);
		this.lqnResultParser = new LqnXmlResultParser(lqnModel);
		this.lqnmod = new LqnModifier(lqnModel);
		this.botIdentifier = new BottleneckIdentifier(lqnModel);
		this.messages = new ArrayList<String>();
	}



	@Override
	public void setup() {
		logger.info("===============================================================================================================");
		logger.info("Setting up algorithm...");

		lqnModel.resetAll();
		// LqnModel workspace = new LqnModel("./application.properties");

		logger.debug("Parsing input file " + inputFilePath);
		try {
			lqnInputParser.ParseFile(inputFilePath);
		} catch (FileNotFoundException fnfe) {
			logger.debug("[FileNotFoundException]: " + fnfe.getMessage());
			fnfe.printStackTrace();
		}

		logger.debug("Linking entries...");
		lqnModel.LinkEntries();

		// lqnParser.printProcessors();
		logger.debug("Building DestTree...");
		lqnModel.buildDestTree();
		try {
			lqnModel.organizeTasksBasedBelowSize();
		} catch (Exception e) {
			logger.debug("[Exception]: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		logger.debug("");
		lqnModel.printDestTree();
		logger.debug("");

		logger.info("Initial model details:");
		botIdentifier.printBStrengthTable();
		logger.info("");

		logger.info("DONE Initializing.");
		logger.info("");
	}

	//return false if problem solving
	private boolean findBottlenecks(ArrayList<Entity> bStrengthTable, ArrayList<Entity> unchangeable, boolean solveModel) {
		logger.info("Finding bottleneck...");
		logger.info("");
		bStrengthTable.clear();

		if (solveModel) {
			boolean isSolved = solveAndParseModel();

			if (!isSolved) {
				return false;
			}
		}

		bStrengthTable.addAll(botIdentifier.generateBStrengthTable(satThreshold, unchangeable, this.bottleneckMaxBStrengthTaskOnly));

		logger.info("FindingBottleneck...DONE");
		logger.info("");

		return true;
	}

	private boolean solveAndParseModel() {
		logger.info("Solving model...");
		logger.info("");

		logger.debug("Linking Entries...");
		lqnModel.LinkEntries();
		logger.debug("Building Dest Tree...");
		lqnModel.buildDestTree();
		try {
			lqnModel.organizeTasksBasedBelowSize();
		} catch (Exception e) {
			logger.debug("[Exception]: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		lqnModel.printDestTree();
		logger.debug("");

		logger.debug("Parsing and updating XML model");
		logger.debug("input file: " + inputFilePath);
		logger.debug("output model to: " + outputFilePath);
		logger.debug("");

		//update from in-memory representation of the model, output the updated model to outputFilePath
		lqnmod.parseAndUpdateXML(lqnInputParser.getLQXCData(false), inputFilePath, outputFilePath);

		boolean result = LqnSolver.solveLqns(outputFilePath, outputFilePath + ".result", lqnResultParser, xmlOutputFilePath);
		if (result == false) {
			logger.info("Problem solving.");
			return false;
		}
		

		logger.info("Solving Model...DONE.");
		logger.info("");
		return true;
	}

	//return response time of first ref type task
	public float findMaxResourcePerformance(ArrayList<Entity> bSet) {
		logger.info("Finding max resource performance...");
		boolean flag = false;
		HashMap<Entity, Integer> hMap = new HashMap<Entity, Integer>();
		int tmpMaxVMReplicas = this.maxVMReplicas;
		int tmpMaxProcsPerVM = this.maxProcsPerVM;

		while (!flag && (tmpMaxProcsPerVM >= 1 || tmpMaxVMReplicas >= 1)) {
			logger.info("-----------------------------------------------------------------------------");
			logger.info("Solving to find maxResourcePerformance: maxVMReplicas: " + tmpMaxVMReplicas + " maxProcsPerVM: " + tmpMaxProcsPerVM);
			logger.info("-----------------------------------------------------------------------------");

			for (Processor p : lqnModel.getProcessors()) {

				if (tmpMaxProcsPerVM >= 1 || tmpMaxVMReplicas >= 1) {
					if (p.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() && p.getScheduling() != ProcessorSchedulingType.INF) {
						p.setMultiplicity(Math.max(tmpMaxVMReplicas, tmpMaxProcsPerVM));
					}
				}

				if (tmpMaxVMReplicas >= 1) {
					for (Task t : p.getTasks()) {
						if (t.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() && t.getScheduling() != TaskSchedulingType.INF
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
			return LqnConstants.UNDEF.getConstantValue();
		}

		logger.info("==============================================================");
		logger.info("maxReplicasPerVM tried: " + tmpMaxVMReplicas);
		logger.info("maxProcessorsPerVM tried: " + tmpMaxProcsPerVM);
		logger.info("Max resource performance");
		float responseTime = this.getFirstRefTaskResponseTime();
		logger.info("==============================================================");

		logger.info("Finding max resource performance...DONE");

		return responseTime;
	}

	/*	public float findMaxResourcePerformance() {
			logger.info("Finding max resource performance...");
			logger.info("");

			for (Processor p : workspace.getProcessors()) {
				if (p.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() && p.getScheduling() != ProcessorSchedulingType.INF) {
					p.setMultiplicity(Math.max(this.maxReplicationPerTask, this.maxProcessorsPerVM));
				}
				for (Task t : p.getTasks()) {

					if (t.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() && t.getScheduling() != TaskSchedulingType.INF && t.getScheduling() != TaskSchedulingType.REF) {
						//System.out.println("[Algorithm1] Replicating: " + t);
						t.setMultiplicity((this.maxReplicationPerTask + 1) * t.getMultiplicity());

					}
				}
			}
			ArrayList<Entity> bSet = new ArrayList<Entity>();
			boolean flag = findBottlenecks(bSet, true);

			if (!flag) {
				return LqnConstants.UNDEF.getConstantValue();
			}

			logger.info("Max resource performance");
			float responseTime = this.getFirstRefTaskResponseTime();

			logger.info("Finding max resource performance...DONE");

			return responseTime;
		}
	*/
	private float getFirstRefTaskResponseTime() {
		ArrayList<Task> refTasks = lqnModel.findRefTasks();
		float responseTime = this.getFirstRefTaskResponseTime(refTasks, true);
		return responseTime;
	}

	private float getFirstRefTaskResponseTime(ArrayList<Task> refTasks, boolean printInfo) {
		Task t = refTasks.get(0);
		Entry e = refTasks.get(0).getEntries().get(0);
		ActivityPhases ap = (ActivityPhases) refTasks.get(0).getEntries().get(0).getActivityAtPhase(1);
		float responseTime = ap.getResult().getService_time();
		if (printInfo) {
			logger.info("Task " + t + " entry: " + e + " activity " + ap + " ph1 response time: " + responseTime);
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
		logger.info("Finding initial configuration that solves model...");
		logger.info("");

		while (!endLoop) {
			loopCount++;
			//unable to solve model in first try
			//therefore go in a loop until model is solvable.
			if (loopCount == 1) {
				if (initialTry.equals("") || initialTry == null) {
					continue;
				}

				Task t = lqnModel.getTaskByName(initialTry);
				if (t == null) {
					continue;
				}
				initialTask = t;
				boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);
				if (!wasDuplicated) {
					logger.debug("Task " + t + " was not replicated");
					continue;
				}
				changeCount++;
				addedVMs++;
				logger.info("=======================================");
				strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
				logger.info(strChange);
				logger.info("=======================================");
				messages.add(strChange);
			} else {
				ArrayList<Task> ent = (ArrayList<Task>) lqnModel.getTasks().clone();
				for (Task t : ent) {
					if (t.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() && t.getScheduling() != TaskSchedulingType.INF
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
							logger.debug("Task " + t + " was not replicated");
							continue;
						}
						changeCount++;
						addedVMs++;
						logger.info("=======================================");
						strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
						logger.info(strChange);
						logger.info("=======================================");
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
					logger.debug("Task " + t + " was not replicated");
					continue;
				}
				changeCount++;
				addedVMs++;
				logger.info("=======================================");
				strChange = "[InitialSolvableConf Change count: " + changeCount + "] " + "TASK: " + t + " m-replication = " + t.getDuplicationCount() + " in next loop";
				logger.info(strChange);
				logger.info("=======================================");
				changes.add(strChange);
			} else {
			ArrayList<Processor> ent = (ArrayList<Processor>) workspace.getProcessors().clone();
			for (Processor p : ent) {
				if (p.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() && p.getScheduling() != ProcessorSchedulingType.INF) {
					boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

					if (!wasMultiplicated) {
						logger.debug("Processor " + p + " was not duplicated");
						continue;
					}

					changeCount++;
					//addedVMs++;
					logger.info("=======================================");
					strChange = "[InitialSolvableConf Change count: " + changeCount + "] " + "PROCESSOR: " + p + " multiplicity = " + p.getMultiplicity() + " in next loop";
					logger.info(strChange);
					logger.info("=======================================");
					changes.add(strChange);
				}
			}
			*/

			endLoop = findBottlenecks(bSet, unchangeable, true);

			logger.info("Bottlenecks: " + bSet.toString());
			logger.info("Unchangeable: " + unchangeable.toString());

			if (!endLoop && ((bSet == null) || (bSet.isEmpty()))) {
				if (loopCount > 4) {
					endLoop = true;
				}
				continue;
			} else if ((bSet == null) || (bSet.isEmpty())) {
				logger.info("No more bottlenecks - DONE");
				endLoop = true;
			}
		}
		logger.info("Finding initial configuration that solves model...DONE");
		logger.info("");
		return changeCount;
	}

	private int setupInitialSolvableConf(ArrayList<Entity> bSet, ArrayList<Entity> unchangeable, String initialTry) {
		boolean endLoop = false;
		String strChange;
		int changeCount = 0;
		int addedVMs = 0;
		int loopCount = 0;
		Task initialTask = lqnModel.getTaskByName(initialTry);
		logger.info("Finding initial configuration that solves model...");
		logger.info("");

		logger.info("Bottlenecks: " + bSet.toString());
		logger.info("Unchangeable: " + unchangeable.toString());

		if (initialTask != null) {
			if (!bSet.contains(initialTask)) {
				bSet.add(0, initialTask);
			}
		}

		ArrayList<Entity> bSetClone = (ArrayList<Entity>) bSet.clone();
		bSet.clear();
		for (Entity b : bSetClone) {
			if (b instanceof Task) {
				Task t = lqnModel.getTaskByName(((Task) b).getName());

				if (bSet.contains(t.getProcessor())) {
					//since processor of this task is also a bottleneck, first add the processor
					logger.info("Skipping task " + t + " duplication as parent processor is also bottleneck");
					continue;
				}

				boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);

				if (!wasDuplicated) {
					logger.debug("Task " + t + " was not replicated");
					continue;
				}

				changeCount++;
				addedVMs++;
				logger.info("=======================================");
				strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
				logger.info(strChange);
				logger.info("=======================================");
				messages.add(strChange);
			} else if (b instanceof Processor) {
				Processor p = lqnModel.getProcessorByName(((Processor) b).getName());

				boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

				if (!wasMultiplicated) {
					logger.debug("Processor " + p + " was not duplicated");
					continue;
				}

				changeCount++;
				logger.info("=======================================");
				strChange = "InitialSolvableConf Change" + changeCount + ": " + "PROC: " + p + " multiplicity = " + p.getMultiplicity() + " in next loop";
				logger.info(strChange);
				logger.info("=======================================");
				messages.add(strChange);
			}

			endLoop = findBottlenecks(bSet, unchangeable, true);

			logger.info("Bottlenecks: " + bSet.toString());
			logger.info("Unchangeable: " + unchangeable.toString());

			if (endLoop) {
				logger.info("No more bottlenecks - DONE");
				break;
			} else if (!endLoop && ((bSet == null) || (bSet.isEmpty()))) {
				if (loopCount > 4) {
					endLoop = true;
					break;
				}
			} else if ((bSet == null) || (bSet.isEmpty())) {
				logger.info("No more bottlenecks - DONE");
				endLoop = true;
				break;
			}
		}

		//bSet.clear();

		while (!endLoop) {
			loopCount++;
			//unable to solve model in first try
			//therefore go in a loop until model is solvable.
			ArrayList<Task> ent = (ArrayList<Task>) lqnModel.getTasks().clone();
			for (Task t : ent) {
				if (t.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() && t.getScheduling() != TaskSchedulingType.INF && t.getScheduling() != TaskSchedulingType.REF) {
					boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);
					if (!wasDuplicated) {
						logger.debug("Task " + t + " was not replicated");
						continue;
					}
					changeCount++;
					addedVMs++;
					logger.info("=======================================");
					strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
					logger.info(strChange);
					logger.info("=======================================");
					messages.add(strChange);
				}
			}

			/*
			ArrayList<Processor> ent = (ArrayList<Processor>) workspace.getProcessors().clone();
			for (Processor p : ent) {
				if (p.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() && p.getScheduling() != ProcessorSchedulingType.INF) {
					boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

					if (!wasMultiplicated) {
						logger.debug("Processor " + p + " was not duplicated");
						continue;
					}

					changeCount++;
					//addedVMs++;
					logger.info("=======================================");
					strChange = "[InitialSolvableConf Change count: " + changeCount + "] " + "PROCESSOR: " + p + " multiplicity = " + p.getMultiplicity() + " in next loop";
					logger.info(strChange);
					logger.info("=======================================");
					changes.add(strChange);
				}
			}
			*/

			endLoop = findBottlenecks(bSet, unchangeable, true);

			logger.info("Bottlenecks: " + bSet.toString());
			logger.info("Unchangeable: " + unchangeable.toString());

			if (endLoop) {
				logger.info("No more bottlenecks - DONE");
				break;
			} else if (!endLoop && ((bSet == null) || (bSet.isEmpty()))) {
				if (loopCount > 4) {
					endLoop = true;
				}
				continue;
			} else if ((bSet == null) || (bSet.isEmpty())) {
				logger.info("No more bottlenecks - DONE");
				endLoop = true;
			}
		}
		logger.info("Finding initial configuration that solves model...DONE");
		logger.info("");
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
			logger.info(t.getName() + " has reached maxReplicas: ");
			return false;
		} else if (addedVMs >= this.spareVMs) {
			if (unchangeable != null && (!unchangeable.contains(t))) {
				unchangeable.add(t);
			}
			logger.info("No more VMs available.");
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

			logger.info(p.getName() + " " + "multiplicity: " + p.getMultiplicity() + " has reached maxReplicas: " + maxProcsPerVM);
			return false;
		}
		p.setMultiplicity(p.getMultiplicity() + multiplicityAdded);

		return true;
	}

	@PostConstruct
	@Override
    public void run() {
		String strMsg;
		int addedVMs = 0;
		int noChangesCount = 0;
		ArrayList<Entity> bSet = new ArrayList<Entity>();
		ArrayList<Entity> bSetMaxRes = new ArrayList<Entity>();

		setup();

		float maxResResponse = findMaxResourcePerformance(bSet);
		bSetMaxRes = (ArrayList<Entity>) bSet.clone();

		if (maxResResponse == LqnConstants.UNDEF.getConstantValue()) {
			botIdentifier.printBStrengthTable();
			logger.info("Unable to find model solution for maximum resources. DONE.");
			return;
		} else if (maxResResponse > responseTimeObjective) {
			botIdentifier.printBStrengthTable();
			logger.info("Max Resource ResponseTime: " + maxResResponse);
			logger.info("ResponseTime Objective: " + responseTimeObjective);
			logger.info("Unable to find model solution: Max Resource ResponseTime > responseTimeObjective. DONE");
			return;
		}

		//get maxBStrength task name based on model run for findMaxResourcePerformance.
		String initialTry = null;
		if (botIdentifier.getMaxBStrengthTask() == null) {
			logger.info("No maximum bottleneck Task");
		} else {
			initialTry = botIdentifier.getMaxBStrengthTask().getName();
			setup();
		}

		int loopCount = 1;
		int changeCount = 0;
		ArrayList<Task> refTasks = lqnModel.findRefTasks();
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
					logger.info("Unable to find model solution");
					break;
				}
			} else if (flag == false) {
				botIdentifier.printBStrengthTable();
				logger.info("Unable to find model solution");
				break;
			}

			float responseTime = this.getFirstRefTaskResponseTime(refTasks, false);
			if (this.responseTimeObjective >= responseTime) {
				logger.info("Met response Time Objectives - DONE");
				logger.info(String.format("Objective: %.3f, ResponseTime: %.3f", responseTimeObjective, responseTime));
				break;
			}

			logger.info("Bottlenecks: " + bSet.toString());
			logger.info("Unchangeable: " + unchangeable.toString());

			if ((bSet == null) || (bSet.isEmpty())) {
				logger.info("No more bottlenecks - DONE");
				break;
			}

			curChangesCount = 0;
			for (Entity b : bSet) {
				if (b instanceof Task) {
					Task t = (Task) b;

					if (bSet.contains(t.getProcessor())) {
						//since processor of this task is also a bottleneck, first add the processor
						logger.info("Skipping task " + t + " duplication as parent processor is also bottleneck");
						continue;
					}

					boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);

					if (!wasDuplicated) {
						strMsg = "Loop" + loopCount + " Limit reached: TASK: " + t + " VM not replicated in next loop";
						messages.add(strMsg);
						logger.info(strMsg);
						continue;
					}

					changeCount++;
					addedVMs++;
					logger.info("=======================================");
					strMsg = "Loop" + loopCount + " Change" + changeCount + ": " + "TASK: " + t + " VM = " + t.getDuplicationCount() + " in next loop";
					logger.info(strMsg);
					logger.info("=======================================");
					messages.add(strMsg);
					curChangesCount++;

				} else if (b instanceof Processor) {
					Processor p = (Processor) b;

					boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

					if (!wasMultiplicated) {
						strMsg = "Loop" + loopCount + " Limit reached: PROC: " + p + " not duplicated in next loop";
						messages.add(strMsg);
						logger.info(strMsg);
						continue;
					}

					changeCount++;
					logger.info("=======================================");
					strMsg = "Loop" + loopCount + " Change" + changeCount + ": " + "PROC: " + p + " multiplicity = " + p.getMultiplicity() + " in next loop";
					logger.info(strMsg);
					logger.info("=======================================");
					messages.add(strMsg);
					curChangesCount++;
				}
			}
			logger.info("Loop " + loopCount + " DONE------------------------------------------------------------------------------");
			logger.info("Running...");
			logger.info("");

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
				logger.info("No changes for previous 5 loops. DONE");
				break;
			}
			loopCount++;
		}

		logger.info("Summary of changes");
		logger.info("----------------------");
		for (int i = 0; i < messages.size(); i++) {
			logger.info(messages.get(i));
		}

	}
}
