package ca.appsimulations.jlqninterface.bottleneck;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class BottleneckIdentifier {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private LqnModel lqnModel;

    public BottleneckIdentifier(LqnModel lqnModel) {
        this.lqnModel = lqnModel;
    }

    private static boolean addToBSet(ArrayList<Entity> bSet, Entity e, ArrayList<Entity> unchangeable) {
        boolean isAdded = false;
        if (bSet == null) {
            return false;
        }

        if (!bSet.contains(e)) {
            if ((unchangeable == null) || ((unchangeable != null) && !unchangeable.contains(e))) {
                bSet.add(e);
                isAdded = true;
            }
        }
        return isAdded;
    }

    public ArrayList<Entity> buildBStrengthTable(double satThreshold,
                                                 ArrayList<Entity> unchangeable,
                                                 boolean maxBStrengthTaskOnly) {
        // find sat for Processors
        ArrayList<Processor> processors = lqnModel.processors();
        ArrayList<Task> tasks = lqnModel.tasks();
        ArrayList<Entity> bSet = new ArrayList<Entity>();
        Processor maxBProc = null;
        double maxSatOfProcessors = 0;
        Task maxBTask = null;
        double maxBStrengthValue = 0;
        double factor = 1000000.0;
        int pCount = 0, tCount = 0; //number of processors and tasks that are bottleneck
        boolean isAdded = false;

        //Calculate saturation of each processor
        for (Processor p : processors) {
            double util = p.getResult().getUtilization();
            int mult = p.getMultiplicity();
            double sat = Math.round(util / mult * factor) / factor;
            if (LqnConstants.getValue(mult) == LqnConstants.INFINITY) {
                sat = 0;
            }

            p.setSat(sat);

            if ((p.getScheduling() != ProcessorSchedulingType.INF) && (maxSatOfProcessors < p.getSat())) {
                if (unchangeable != null && !unchangeable.contains(p)) {
                    maxSatOfProcessors = p.getSat();
                    maxBProc = p;
                }
            }
        }

        //Calculate saturation for each tasks
        for (Task t : tasks) {
            double util = t.getResult().getUtilization();
            int mult = t.getMultiplicity();
            double sat = Math.round(util / mult * factor) / factor;
            if (LqnConstants.getValue(mult) == LqnConstants.INFINITY) {
                sat = 0;
            }

            t.setSat(sat);
        }

        // Find BStrength for each task
        for (Task t : tasks) {
            double maxBelowSat = 0;
            if (t.getBelow().size() > 0) {
                for (int i = 0; i < t.getBelow().size(); i++) {
                    Task below = t.getBelow().get(i);
                    if (maxBelowSat < below.getSat()) {
                        maxBelowSat = below.getSat();
                    }
                }

                double bStr = Math.round(t.getSat() / maxBelowSat * factor) / factor;
                t.setBStrength(bStr);
            }
            else {
                t.setBStrength(t.getSat());
            }

            if ((t.getScheduling() != TaskSchedulingType.REF) && (t.getScheduling() != TaskSchedulingType.INF) &&
                (t.getSat() >= satThreshold)) {

                if (!maxBStrengthTaskOnly) {
                    if (t.isDuplicate()) {
                        isAdded = addToBSet(bSet, t.getDuplicatedFrom(), unchangeable);
                        if (isAdded) {
                            tCount++;
                        }
                        //if (!bSet.contains(t.getDuplicatedFrom())) {
                        //bSet.add(t.getDuplicatedFrom());
                        //}
                    }
                    else {
                        //bSet.add(t);
                        isAdded = addToBSet(bSet, t, unchangeable);
                        if (isAdded) {
                            tCount++;
                        }
                    }

                }
                else if (maxBStrengthTaskOnly && (maxBStrengthValue < t.getBStrength())) {
                    if (unchangeable != null && !unchangeable.contains(t)) {
                        maxBStrengthValue = t.getBStrength();
                        maxBTask = t;
                    }
                }
            }

        }

        //If maxBStrengthTaskOnly is true then add only the maxBProc to bottlenecks
        //else add all proc that have saturation > satThreshold to bottlenecks
        if (maxBStrengthTaskOnly) {
            //If a maxBProc has saturation > satThreshold then add it to bottlenecks
            if ((maxBProc != null) && (maxBProc.getSat() >= satThreshold)) {
                isAdded = addToBSet(bSet, maxBProc, unchangeable);
                if (isAdded) {
                    pCount++;
                }
            }
        }
        else {
            //If a processor has saturation > satThreshold then add it to bottlenecks
            for (Processor p : processors) {
                if ((p.getScheduling() != ProcessorSchedulingType.INF) && (p.getSat() >= satThreshold)) {
                    //bSet.add(p);
                    isAdded = addToBSet(bSet, p, unchangeable);
                    if (isAdded) {
                        pCount++;
                    }
                }
            }
        }

        //If bStrength=True (i.e. maxBStrengthTaskOnly is true) and maxBTask!=null
        //and no processor is a bottleneck then add maxBTask to bottleneck
        if (maxBStrengthTaskOnly && (maxBTask != null) && (pCount == 0)) {
            //bSet.add(maxBTask);
            isAdded = addToBSet(bSet, maxBTask, unchangeable);
            if (isAdded) {
                tCount++;
            }
        }
        logger.debug("MAX BStrength " + maxBStrengthValue);

        printBStrengthTable();
        return bSet;

    }

    public void printBStrengthTable() {
        StringBuilder strB = new StringBuilder();
        String formatString = "%12s %12s %12s %12s %12s %12s %5s %5s %12s %12s %12s %12s\n";
        String formatStringTask = "%12s %12s %12.4f %12.4f %12.4f %12s %5d %5d %12.4f %12.4f %12.4f %12.4f\n";
        String formatStringProc = "%12s %12s %12s %12s %12s %12s %5d %5d %12.4f %12.4f %12.4f %12s\n";

        strB.append("BStrength Table:\n");
        strB.append(
                "--------------------------------------------------------------------------------------------------------------------------------------------------\n");
        strB.append(String.format(formatString,
                                  "Resource",
                                  "Entry",
                                  "T-ThinkTime",
                                  "ServiceTime",
                                  "ResponseTime",
                                  "Multiplicity",
                                  "Rep",
                                  "Dup",
                                  "Throughput",
                                  "Utilization",
                                  "Saturation",
                                  "BStrength"));
        strB.append(
                "--------------------------------------------------------------------------------------------------------------------------------------------------\n");
        strB.append("Tasks:\n");
        for (Task t : lqnModel.tasks()) {
            for (Entry e : t.getEntries()) {
                ActivityPhases ap = (ActivityPhases) e.getActivityAtPhase(1);
                double rTime = ap.getResult().getService_time();
                double tThinTime = ap.getThinkTime();
                double sTime = ap.getHost_demand_mean();
                double through = t.getResult().getThroughput();
                double util = t.getResult().getUtilization();
                String mult = t.getMutiplicityString();
                int rep = t.getReplication();
                int dup = t.getDuplicationCount();
                double sat = t.getSat();
                double bStrength = t.getBStrength();
                strB.append(String.format(formatStringTask,
                                          t.getName(),
                                          e.getName(),
                                          tThinTime,
                                          sTime,
                                          rTime,
                                          mult,
                                          rep,
                                          dup,
                                          through,
                                          util,
                                          sat,
                                          bStrength));
            }
        }
        strB.append("Processors:\n");
        for (Processor p : lqnModel.processors()) {
            String mult = p.getMutiplicityString();
            double through = p.getResult().getThroughput();
            double util = p.getResult().getUtilization();
            double sat = p.getSat();
            int rep = p.getReplication();
            int dup = p.getDuplicationCount();
            strB.append(String.format(formatStringProc,
                                      p.getName(),
                                      "-",
                                      "-",
                                      "-",
                                      "-",
                                      mult,
                                      rep,
                                      dup,
                                      through,
                                      util,
                                      sat,
                                      "-"));
        }

        logger.info(strB.toString());
    }

    public Task getMaxBStrengthTask() {
        Task maxTask = null;
        double maxBStrength = 0;

        for (Task t : lqnModel.tasks()) {
            if ((t.getScheduling() != TaskSchedulingType.REF) && (t.getScheduling() != TaskSchedulingType.INF)) {
                if (t.getBStrength() > maxBStrength) {
                    maxBStrength = t.getBStrength();
                    maxTask = t;
                }
            }
        }

        return maxTask;
    }

}
