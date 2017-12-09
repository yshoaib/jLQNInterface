package ca.appsimulations.jlqninterface.bottleneck;

import ca.appsimulations.jlqninterface.algorithm.Algorithm;
import ca.appsimulations.jlqninterface.configuration.ConfigurationService;
import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.lqn.model.handler.LqnModifier;
import ca.appsimulations.jlqninterface.lqn.model.handler.LqnSolver;
import ca.appsimulations.jlqninterface.lqn.model.parser.LqnInputParser;
import ca.appsimulations.jlqninterface.lqn.model.parser.LqnResultParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */
@Slf4j
@Component
public class BottleneckAlgorithm extends Algorithm {

    private LqnInputParser lqnInputParser;
    private LqnResultParser lqnResultParser;
    private LqnModifier lqnModifier;
    private BottleneckIdentifier bottleneckIdentifier;
    private ArrayList<String> messages;

    @Autowired
    public BottleneckAlgorithm(ConfigurationService configurationService, LqnModel lqnModel) {
        super(configurationService, lqnModel);
        this.lqnInputParser = new LqnInputParser(lqnModel, true);
        this.lqnResultParser = new LqnResultParser(lqnModel);
        this.lqnModifier = new LqnModifier(lqnModel);
        this.bottleneckIdentifier = new BottleneckIdentifier(lqnModel);
        this.messages = new ArrayList<String>();
    }


    @Override
    public void setup() {
        log.info(
                "===============================================================================================================");
        log.info("Setting up algorithm...");

        lqnModel.resetAll();
        // LqnModel workspace = new LqnModel("./application.properties");

        log.debug("Parsing input file " + this.configurationService.inputFilePath());
        try {
            lqnInputParser.parseFile(this.configurationService.inputFilePath());
        }
        catch (FileNotFoundException fnfe) {
            log.debug("[FileNotFoundException]: " + fnfe.getMessage());
            fnfe.printStackTrace();
        }

        lqnModel.linkEntries();

        // lqnParser.printProcessors();
        log.debug("Linking entries...");
        log.debug("Building DestTree...");
        lqnModel.buildDestTree();
        try {
            lqnModel.organizeTasksBasedBelowSize();
        }
        catch (Exception e) {
            log.debug("[Exception]: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        log.debug("");
        lqnModel.printDestTree();
        log.debug("");

        log.info("Initial model details:");
        bottleneckIdentifier.printBStrengthTable();
        log.info("");

        log.info("DONE Initializing.");
        log.info("");
    }

    //return false if problem solving
    private boolean findBottlenecks(ArrayList<Entity> bStrengthTable,
                                    ArrayList<Entity> unchangeable,
                                    boolean solveModel) {
        log.info("Finding bottleneck...");
        log.info("");
        bStrengthTable.clear();

        if (solveModel) {
            boolean isSolved = solveAndParseModel();

            if (!isSolved) {
                return false;
            }
        }

        bStrengthTable.addAll(bottleneckIdentifier.buildBStrengthTable(this.configurationService.satThreshold(),
                                                                       unchangeable,
                                                                       this.configurationService
																				  .bottleneckMaxBStrengthTaskOnly()));

        log.info("FindingBottleneck...DONE");
        log.info("");

        return true;
    }

    private boolean solveAndParseModel() {
        log.info("Solving model...");
        log.info("");

        log.debug("Linking Entries...");
        lqnModel.linkEntries();
        log.debug("Building Dest Tree...");
        lqnModel.buildDestTree();
        try {
            lqnModel.organizeTasksBasedBelowSize();
        }
        catch (Exception e) {
            log.debug("[Exception]: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        lqnModel.printDestTree();
        log.debug("");

        log.debug("Parsing and updating XML model");
        log.debug("input file: " + configurationService.inputFilePath());
        log.debug("output model to: " + configurationService.autoInputFilePath());
        log.debug("");

        //update from in-memory representation of the model, output the updated model to outputFilePath
        lqnModifier.parseAndUpdateXML(lqnInputParser.getLQXCData(false), configurationService.inputFilePath(),
                                      configurationService.autoInputFilePath());

        boolean result = LqnSolver.solveLqns(configurationService.autoInputFilePath(), lqnResultParser,
                                             configurationService.lqnXmlOutputFilePath());
        if (result == false) {
            log.info("Problem solving.");
            return false;
        }


        log.info("Solving Model...DONE.");
        log.info("");
        return true;
    }

    //return response time of first ref type task
    public double findMaxResourcePerformance(ArrayList<Entity> bSet) {
        log.info("Finding max resource performance...");
        boolean flag = false;
        HashMap<Entity, Integer> hMap = new HashMap<Entity, Integer>();
        int tmpMaxVMReplicas = this.configurationService.maxVMReplicas();
        int tmpMaxProcsPerVM = this.configurationService.maxProcsPerVM();

        while (!flag && (tmpMaxProcsPerVM >= 1 || tmpMaxVMReplicas >= 1)) {
            log.info("-----------------------------------------------------------------------------");
            log.info("Solving to find maxResourcePerformance: maxVMReplicas: " + tmpMaxVMReplicas + " maxProcsPerVM: " +
                     tmpMaxProcsPerVM);
            log.info("-----------------------------------------------------------------------------");

            for (Processor p : lqnModel.processors()) {

                if (tmpMaxProcsPerVM >= 1 || tmpMaxVMReplicas >= 1) {
                    if (p.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() &&
                        p.getScheduling() != ProcessorSchedulingType.INF) {
                        p.setMultiplicity(Math.max(tmpMaxVMReplicas, tmpMaxProcsPerVM));
                    }
                }

                if (tmpMaxVMReplicas >= 1) {
                    for (Task t : p.getTasks()) {
                        if (t.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() &&
                            t.getScheduling() != TaskSchedulingType.INF
                            && t.getScheduling() != TaskSchedulingType.REF) {

                            int mult = t.getMultiplicity();
                            if (!hMap.containsKey(t)) {
                                hMap.put(t, t.getMultiplicity());
                            }
                            else {
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

        log.info("==============================================================");
        log.info("maxReplicasPerVM tried: " + tmpMaxVMReplicas);
        log.info("maxProcessorsPerVM tried: " + tmpMaxProcsPerVM);
        log.info("Max resource performance");
        double responseTime = this.getFirstRefTaskResponseTime();
        log.info("==============================================================");

        log.info("Finding max resource performance...DONE");

        return responseTime;
    }

    private double getFirstRefTaskResponseTime() {
        ArrayList<Task> refTasks = lqnModel.buildRefTasksFromExistingTasks();
        double responseTime = this.getFirstRefTaskResponseTime(refTasks, true);
        return responseTime;
    }

    private double getFirstRefTaskResponseTime(ArrayList<Task> refTasks, boolean printInfo) {
        Task t = refTasks.get(0);
        Entry e = refTasks.get(0).getEntries().get(0);
        ActivityPhases ap = (ActivityPhases) refTasks.get(0).getEntries().get(0).getActivityAtPhase(1);
        double responseTime = ap.getResult().getService_time();
        if (printInfo) {
            log.info("Task " + t + " entry: " + e + " activity " + ap + " ph1 response time: " + responseTime);
        }

        return responseTime;
    }

    private int setupInitialSolvableConf_OLD(ArrayList<Entity> bSet,
                                             ArrayList<Entity> unchangeable,
                                             String initialTry) {
        boolean endLoop = false;
        String strChange;
        int changeCount = 0;
        int addedVMs = 0;
        int loopCount = 0;
        Task initialTask = null;
        log.info("Finding initial configuration that solves model...");
        log.info("");

        while (!endLoop) {
            loopCount++;
            //unable to solve model in first try
            //therefore go in a loop until model is solvable.
            if (loopCount == 1) {
                if (initialTry.equals("") || initialTry == null) {
                    continue;
                }

                Task t = lqnModel.taskByName(initialTry);
                if (t == null) {
                    continue;
                }
                initialTask = t;
                boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);
                if (!wasDuplicated) {
                    log.debug("Task " + t + " was not replicated");
                    continue;
                }
                changeCount++;
                addedVMs++;
                log.info("=======================================");
                strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " +
                            t.getDuplicationCount() + " in next loop";
                log.info(strChange);
                log.info("=======================================");
                messages.add(strChange);
            }
            else {
                ArrayList<Task> ent = (ArrayList<Task>) lqnModel.tasks().clone();
                for (Task t : ent) {
                    if (t.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() &&
                        t.getScheduling() != TaskSchedulingType.INF
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
                            log.debug("Task " + t + " was not replicated");
                            continue;
                        }
                        changeCount++;
                        addedVMs++;
                        log.info("=======================================");
                        strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " +
                                    t.getDuplicationCount() + " in next loop";
                        log.info(strChange);
                        log.info("=======================================");
                        messages.add(strChange);
                    }
                }
            }

            endLoop = findBottlenecks(bSet, unchangeable, true);

            log.info("Bottlenecks: " + bSet.toString());
            log.info("Unchangeable: " + unchangeable.toString());

            if (!endLoop && ((bSet == null) || (bSet.isEmpty()))) {
                if (loopCount > 4) {
                    endLoop = true;
                }
                continue;
            }
            else if ((bSet == null) || (bSet.isEmpty())) {
                log.info("No more bottlenecks - DONE");
                endLoop = true;
            }
        }
        log.info("Finding initial configuration that solves model...DONE");
        log.info("");
        return changeCount;
    }

    private int setupInitialSolvableConf(ArrayList<Entity> bSet, ArrayList<Entity> unchangeable, String initialTry) {
        boolean endLoop = false;
        String strChange;
        int changeCount = 0;
        int addedVMs = 0;
        int loopCount = 0;
        Task initialTask = lqnModel.taskByName(initialTry);
        log.info("Finding initial configuration that solves model...");
        log.info("");

        log.info("Bottlenecks: " + bSet.toString());
        log.info("Unchangeable: " + unchangeable.toString());

        if (initialTask != null) {
            if (!bSet.contains(initialTask)) {
                bSet.add(0, initialTask);
            }
        }

        ArrayList<Entity> bSetClone = (ArrayList<Entity>) bSet.clone();
        bSet.clear();
        for (Entity b : bSetClone) {
            if (b instanceof Task) {
                Task t = lqnModel.taskByName(((Task) b).getName());

                if (bSet.contains(t.getProcessor())) {
                    //since processor of this task is also a bottleneck, first add the processor
                    log.info("Skipping task " + t + " duplication as parent processor is also bottleneck");
                    continue;
                }

                boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);

                if (!wasDuplicated) {
                    log.debug("Task " + t + " was not replicated");
                    continue;
                }

                changeCount++;
                addedVMs++;
                log.info("=======================================");
                strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " +
                            t.getDuplicationCount() + " in next loop";
                log.info(strChange);
                log.info("=======================================");
                messages.add(strChange);
            }
            else if (b instanceof Processor) {
                Processor p = lqnModel.processorByName(((Processor) b).getName());

                boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

                if (!wasMultiplicated) {
                    log.debug("Processor " + p + " was not duplicated");
                    continue;
                }

                changeCount++;
                log.info("=======================================");
                strChange = "InitialSolvableConf Change" + changeCount + ": " + "PROC: " + p + " multiplicity = " +
                            p.getMultiplicity() + " in next loop";
                log.info(strChange);
                log.info("=======================================");
                messages.add(strChange);
            }

            endLoop = findBottlenecks(bSet, unchangeable, true);

            log.info("Bottlenecks: " + bSet.toString());
            log.info("Unchangeable: " + unchangeable.toString());

            if (endLoop) {
                log.info("No more bottlenecks - DONE");
                break;
            }
            else if (!endLoop && ((bSet == null) || (bSet.isEmpty()))) {
                if (loopCount > 4) {
                    endLoop = true;
                    break;
                }
            }
            else if ((bSet == null) || (bSet.isEmpty())) {
                log.info("No more bottlenecks - DONE");
                endLoop = true;
                break;
            }
        }

        //bSet.clear();

        while (!endLoop) {
            loopCount++;
            //unable to solve model in first try
            //therefore go in a loop until model is solvable.
            ArrayList<Task> ent = (ArrayList<Task>) lqnModel.tasks().clone();
            for (Task t : ent) {
                if (t.getMultiplicity() != LqnConstants.INFINITY.getConstantValue() &&
                    t.getScheduling() != TaskSchedulingType.INF && t.getScheduling() != TaskSchedulingType.REF) {
                    boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);
                    if (!wasDuplicated) {
                        log.debug("Task " + t + " was not replicated");
                        continue;
                    }
                    changeCount++;
                    addedVMs++;
                    log.info("=======================================");
                    strChange = "InitialSolvableConf Change" + changeCount + ": " + "TASK: " + t + " VM = " +
                                t.getDuplicationCount() + " in next loop";
                    log.info(strChange);
                    log.info("=======================================");
                    messages.add(strChange);
                }
            }

            endLoop = findBottlenecks(bSet, unchangeable, true);

            log.info("Bottlenecks: " + bSet.toString());
            log.info("Unchangeable: " + unchangeable.toString());

            if (endLoop) {
                log.info("No more bottlenecks - DONE");
                break;
            }
            else if (!endLoop && ((bSet == null) || (bSet.isEmpty()))) {
                if (loopCount > 4) {
                    endLoop = true;
                }
                continue;
            }
            else if ((bSet == null) || (bSet.isEmpty())) {
                log.info("No more bottlenecks - DONE");
                endLoop = true;
            }
        }
        log.info("Finding initial configuration that solves model...DONE");
        log.info("");
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

        if (t.getDuplicationCount() >= configurationService.maxVMReplicas()) {
            if (unchangeable != null && (!unchangeable.contains(t))) {
                unchangeable.add(t);
            }
            log.info(t.getName() + " has reached maxReplicas: ");
            return false;
        }
        else if (addedVMs >= configurationService.spareVMs()) {
            if (unchangeable != null && (!unchangeable.contains(t))) {
                unchangeable.add(t);
            }
            log.info("No more VMs available.");
            return false;
        }

        wasDuplicated = t.getProcessor().duplicateSelfAndTasksDepth();

        return wasDuplicated;
    }

    private boolean processProcessorMultiplication(Processor p, int multiplicityAdded, ArrayList<Entity> unchangeable) {
        if (p.getMultiplicity() >= configurationService.maxProcsPerVM()) {
            if (!unchangeable.contains(p)) {
                unchangeable.add(p);
            }

            log.info(p.getName() + " " + "multiplicity: " + p.getMultiplicity() + " has reached maxReplicas: " +
                     configurationService.maxProcsPerVM());
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

        double maxResResponse = findMaxResourcePerformance(bSet);
        bSetMaxRes = (ArrayList<Entity>) bSet.clone();

        if (maxResResponse == LqnConstants.UNDEF.getConstantValue()) {
            bottleneckIdentifier.printBStrengthTable();
            log.info("Unable to find model solution for maximum resources. DONE.");
            return;
        }
        else if (maxResResponse > configurationService.responseTimeObjective()) {
            bottleneckIdentifier.printBStrengthTable();
            log.info("Max Resource ResponseTime: " + maxResResponse);
            log.info("ResponseTime Objective: " + configurationService.responseTimeObjective());
            log.info("Unable to find model solution: Max Resource ResponseTime > responseTimeObjective. DONE");
            return;
        }

        //get maxBStrength task name based on model run for findMaxResourcePerformance.
        String initialTry = null;
        if (bottleneckIdentifier.getMaxBStrengthTask() == null) {
            log.info("No maximum bottleneck Task");
        }
        else {
            initialTry = bottleneckIdentifier.getMaxBStrengthTask().getName();
            setup();
        }

        int loopCount = 1;
        int changeCount = 0;
        ArrayList<Task> refTasks = lqnModel.buildRefTasksFromExistingTasks();
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
                    log.info("Unable to find model solution");
                    break;
                }
            }
            else if (flag == false) {
                bottleneckIdentifier.printBStrengthTable();
                log.info("Unable to find model solution");
                break;
            }

            double responseTime = this.getFirstRefTaskResponseTime(refTasks, false);
            if (configurationService.responseTimeObjective() >= responseTime) {
                log.info("Met response Time Objectives - DONE");
                log.info(String.format("Objective: %.3f, ResponseTime: %.3f",
                                       configurationService.responseTimeObjective(),
                                       responseTime));
                break;
            }

            log.info("Bottlenecks: " + bSet.toString());
            log.info("Unchangeable: " + unchangeable.toString());

            if ((bSet == null) || (bSet.isEmpty())) {
                log.info("No more bottlenecks - DONE");
                break;
            }

            curChangesCount = 0;
            for (Entity b : bSet) {
                if (b instanceof Task) {
                    Task t = (Task) b;

                    if (bSet.contains(t.getProcessor())) {
                        //since processor of this task is also a bottleneck, first add the processor
                        log.info("Skipping task " + t + " duplication as parent processor is also bottleneck");
                        continue;
                    }

                    boolean wasDuplicated = this.processTaskDuplication(t, 1, unchangeable, addedVMs);

                    if (!wasDuplicated) {
                        strMsg = "Loop" + loopCount + " Limit reached: TASK: " + t + " VM not replicated in next loop";
                        messages.add(strMsg);
                        log.info(strMsg);
                        continue;
                    }

                    changeCount++;
                    addedVMs++;
                    log.info("=======================================");
                    strMsg = "Loop" + loopCount + " Change" + changeCount + ": " + "TASK: " + t + " VM = " +
                             t.getDuplicationCount() + " in next loop";
                    log.info(strMsg);
                    log.info("=======================================");
                    messages.add(strMsg);
                    curChangesCount++;

                }
                else if (b instanceof Processor) {
                    Processor p = (Processor) b;

                    boolean wasMultiplicated = processProcessorMultiplication(p, 1, unchangeable);

                    if (!wasMultiplicated) {
                        strMsg = "Loop" + loopCount + " Limit reached: PROC: " + p + " not duplicated in next loop";
                        messages.add(strMsg);
                        log.info(strMsg);
                        continue;
                    }

                    changeCount++;
                    log.info("=======================================");
                    strMsg = "Loop" + loopCount + " Change" + changeCount + ": " + "PROC: " + p + " multiplicity = " +
                             p.getMultiplicity() + " in next loop";
                    log.info(strMsg);
                    log.info("=======================================");
                    messages.add(strMsg);
                    curChangesCount++;
                }
            }
            log.info("Loop " + loopCount +
                     " DONE------------------------------------------------------------------------------");
            log.info("Running...");
            log.info("");

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (curChangesCount == 0) {
                noChangesCount++;
            }
            else {
                noChangesCount = 0;
            }

            if (noChangesCount >= 5) {
                log.info("No changes for previous 5 loops. DONE");
                break;
            }
            loopCount++;
        }

        log.info("Summary of changes");
        log.info("----------------------");
        for (int i = 0; i < messages.size(); i++) {
            log.info(messages.get(i));
        }

    }
}
