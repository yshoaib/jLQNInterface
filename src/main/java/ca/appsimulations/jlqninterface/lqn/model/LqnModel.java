package ca.appsimulations.jlqninterface.lqn.model;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import ca.appsimulations.jlqninterface.configuration.ConfigurationService;
import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.utilities.Utility;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@NoArgsConstructor
public class LqnModel {
    private ArrayList<Task> tasks;
    private ArrayList<Task> refTasks;
    private ArrayList<Entry> entries;
    private ArrayList<Processor> processors;
    private ArrayList<ActivityDefBase> activities;
    private String inputFilePath;
    private String autoInputFilePath;
    private String lqnXmlOutputFilePath;
    private double responseTimeObjective;
    private int maxVMReplicas;
    private double satThreshold;
    private boolean bottleneckBStrengthTaskOnly;
    private int maxProcsPerVM;
    private int spareVMs;
    private Result result;
    private int maxBelow = 0;

    @Autowired
    public LqnModel(ConfigurationService configurationService) {
        tasks = new ArrayList<Task>();
        refTasks = new ArrayList<Task>();
        entries = new ArrayList<Entry>();
        processors = new ArrayList<Processor>();
        activities = new ArrayList<ActivityDefBase>();
        inputFilePath = configurationService.getInputFilePath();
        autoInputFilePath = configurationService.getAutoInputFilePath();
        lqnXmlOutputFilePath = configurationService.getLqnXmlOutputFilePath();
        responseTimeObjective = configurationService.getResponseTimeObjective();
        maxVMReplicas = configurationService.getMaxVMReplicas();
        satThreshold = configurationService.getSatThreshold();
        bottleneckBStrengthTaskOnly = configurationService.isBottleneckMaxBStrengthTaskOnly();
        maxProcsPerVM = configurationService.getMaxProcsPerVM();
        spareVMs = configurationService.getSpareVMs();
        result = new Result();
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    public void addProcessor(Processor p) {
        processors.add(p);
    }

    public void addEntry(Entry e) {
        entries.add(e);
    }

    public void addActivity(ActivityDefBase a) {
        activities.add(a);
    }

    public int getProcessorsSize() {
        return processors.size();
    }

    public int getTasksSize() {
        return tasks.size();
    }

    public Processor getProcessorAtIndex(int index) {
        return processors.get(index);
    }

    public Task getTaskAtIndex(int index) {
        return tasks.get(index);
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public ArrayList<Task> findRefTasks() {
        ArrayList<Task> refTasks = new ArrayList<Task>();
        for (Task t : tasks) {
            if (t.getScheduling() == TaskSchedulingType.REF) {
                refTasks.add(t);
            }
        }
        return refTasks;
    }

    // Call Entry.LinkEntries() after dest array is properly set.
    // After this method, Task.buildDestTree() may be called.
    public void LinkEntries() {
        int entriesSize = entries.size();
        for (int k = 0; k < entriesSize; k++) {
            Entry e = entries.get(k);
            e.clearDestSrcEntries();
        }

        for (int k = 0; k < entries.size(); k++) {
            Entry e = entries.get(k);
            int size = e.getSyncDestStrSize();
            for (int i = 0; i < size; i++) {
                Entry tmp = getEntryByName(e.getSyncDestStr(i));
                if (tmp != null) {
                    e.addSyncDest(tmp);
                }
            }

            e.linkSynchCalls();
        }
    }

    public ArrayList<Entry> getSrcEntries(String entryName) {
        //find srcEntries without calling LinkEntries
        ArrayList<Entry> srcEntries = new ArrayList<Entry>();
        int entriesSize = this.entries.size();
        for (int k = 0; k < entriesSize; k++) {
            Entry src = this.entries.get(k);
            int size = src.getSyncDestStrSize();
            for (int i = 0; i < size; i++) {
                if (src.getSyncDestStr(i).equals(entryName)) {
                    srcEntries.add(src);
                }
            }
        }

        return srcEntries;
    }

    public String getProcessorsToQuotationStrArray() {
        return Utility.listToQuotationStrArray(processors);
    }

    public String getProcessorsCDataString() {
        StringBuilder strB = new StringBuilder();
        strB.append("	processors = array_create(); \n" + "	processors = " + Utility.listToQuotationStrArray(processors) + ";\n" + "	procData = array_create();\n");

        for (int i = 0; i < processors.size(); i++) {
            Processor p = processors.get(i);
            strB.append("	procData[\"" + p + "\"] = [\"" + p + "\", " + p.getMutiplicityString() + "] ;\n");
        }

        return strB.toString();
    }

    // Call buildDestTree() method after calling LinkEntries()
    public void buildDestTree() {
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            t.clearBelow();
        }

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            if (t.getSrcTasksSize() == 0) {
                buildDestTreeRec(t);
            }
        }

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            int curBelowSize = t.getBelowSize();

            if (maxBelow < curBelowSize) {
                maxBelow = curBelowSize;
            }
        }
    }

    private void buildDestTreeRec(Task parent) {
        int size = parent.getDestTasksSize();
        for (int i = 0; i < size; i++) {
            Task child = parent.getDestTaskAtIndex(i);
            buildDestTreeRec(child);
            parent.addToBelow(child);
            parent.addAllTasksBelowtoBelow(child);
        }
    }

    public void printDestTree() {
        StringBuilder strB = new StringBuilder();
        strB.append("Dest Tree\n");
        strB.append("-----------\n");
        for (int k = 0; k < tasks.size(); k++) {
            Task t = tasks.get(k);
            strB.append(String.format("%-25s", "Below(" + t.getName() + "):"));

            int size = t.getBelowSize();
            for (int i = 0; i < size; i++) {
                strB.append(String.format("%-25s", t.getBelowAtIndex(i).getName()));
            }
            strB.append("(size: " + size + ")\t");
            strB.append("\n");
        }

        log.debug(strB.toString());

    }

    public String getTasksToQuotationStrArray() {
        return Utility.listToQuotationStrArray(tasks);
    }

    public String getTasksCDataString() {
        StringBuilder strB = new StringBuilder();
        strB.append("	tasks = array_create(); \n").append("	tasks = " + Utility.listToQuotationStrArray(tasks) + ";\n");

        strB.append("	taskData = array_create();\n");

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            strB.append("	taskData[\"" + t + "\"] = [\"" + t + "\", ");
            if (t.getEntrySize() != 0) {
                Entry e = t.getEntryAtIndex(0);
                strB.append("\"" + e.getName() + "\", ");
                if (e.getEntryType() == EntryAcType.PH1PH2) {
                    if (e.getActivityAtPhase(1) != null) {
                        strB.append(e.getActivityAtPhase(1).getHost_demand_mean() + ", ");
                    } else {
                        strB.append("0.0 , ");
                    }
                } else if (e.getEntryType() == EntryAcType.NONE) {
                    // TODO
                    strB.append("-1" + ", ");
                }
            }
            strB.append(t.getMutiplicityString() + "] ;\n");

        }

        return strB.toString();
    }

    public ArrayList<Task> getTaskWithBelowSize(int size) {
        ArrayList<Task> list = new ArrayList<Task>();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            if (t.getBelowSize() == size) {
                list.add(t);
            }
        }
        return list;
    }

    public void organizeTasksBasedBelowSize() throws Exception {

        ArrayList<Task> newArray = new ArrayList<Task>();
        for (int j = maxBelow; j >= 0; j--) {
            ArrayList<Task> list = getTaskWithBelowSize(j);
            for (int i = 0; i < list.size(); i++) {
                Task t = list.get(i);
                newArray.add(t);
            }
        }

        if (tasks.size() != newArray.size()) {
            throw new Exception("tasks size != newArray size");
        }
        tasks.clear();
        tasks = newArray;
    }

    public void resetAll() {
        activities.clear();
        entries.clear();
        tasks.clear();
        refTasks.clear();
        processors.clear();
    }

    public String getBelowCDataString() {
        StringBuilder strB = new StringBuilder();
        strB.append("	Below = array_create(); \n");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            strB.append("	Below[\"" + t.getName() + "\"] = ");
            strB.append(t.getBelowQutotationStrArray());
            strB.append(";");
            strB.append("\n");
        }
        return strB.toString();
    }

    public ArrayList<Processor> getProcessors() {
        return processors;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public ArrayList<ActivityDefBase> getActivities() {
        return activities;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public String getAutoInputFilePath() {
        return autoInputFilePath;
    }

    public String getLqnXmlOutputFilePath() {
        return lqnXmlOutputFilePath;
    }

    public Processor getProcessorByName(String name) {
        return getProcessorByName(name, false);
    }

    public Processor getProcessorByName(String name, boolean createNew) {
        int size = processors.size();
        for (int i = 0; i < size; i++) {
            Processor p = processors.get(i);
            if (p.getName().equals(name)) {
                return p;
            }
        }

        if (createNew) {
            return (new Processor(this, name));
        }
        return null;
    }

    public Task getTaskByName(String name) {
        ArrayList<Task> ts = tasks;
        int size = ts.size();
        for (int i = 0; i < size; i++) {
            Task t = ts.get(i);
            if (t.getName().equals(name)) {
                return t;
            }
        }

        return null;
    }

    public Task getTaskByName(String name, Processor p, boolean createNew) {
        Task t = findTaskInProcessor(name, p);
        if (t != null) return t;

        if (createNew) {
            return (new Task(this, name, p));
        }

        return null;
    }

    private Task findTaskInProcessor(String name, Processor p) {
        if(p == null){
            return  null;
        }
        ArrayList<Task> ts = p.getTasks();
        int size = ts.size();
        for (int i = 0; i < size; i++) {
            Task t = ts.get(i);
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public Entry getEntryByName(String name) {
        int size = entries.size();
        for (int i = 0; i < size; i++) {
            Entry e = entries.get(i);
            if (e.getName().equals(name)) {
                return e;
            }
        }

        return null;
    }

    public Entry getEntryByName(String name, Task t, boolean createNew) {
        ArrayList<Entry> theEntries = t.getEntries();
        int size = theEntries.size();
        for (int i = 0; i < size; i++) {
            Entry e = theEntries.get(i);
            if (e.getName().equals(name)) {
                return e;
            }
        }

        if (createNew) {
            return (new Entry(this, name, t));
        }

        return null;
    }

    public ActivityPhases getActivityPHByName(String name, Entry e, int phase, boolean createNew) {
        ActivityDefBase a = e.getActivityByName(name);
        if ((a != null) && (a.getName().equals(name))) {
            return (ActivityPhases) a;
        } else if (createNew) {
            return (new ActivityPhases(this, name, e, phase));
        }

        return null;
    }

    public ActivityDef getActivityDefByName(String name, Task t, TaskActivities tA, Entry e, boolean createNew) {
        ActivityDef a = t.getActivityByName(name, tA);
        if ((a != null) && (a.getName().equals(name))) {
            return a;
        } else if (createNew) {
            return (new ActivityDef(this, name, t, tA, e));
        }

        return null;
    }

    public double getResponseTimeObjective() {
        return responseTimeObjective;
    }

    public int getMaxVMReplicas() {
        return maxVMReplicas;
    }

    public double getSatThreshold() {
        return satThreshold;
    }

    public boolean isBottleneckBStrengthTaskOnly() {
        return bottleneckBStrengthTaskOnly;
    }

    public int getMaxProcsPerVM() {
        return maxProcsPerVM;
    }

    public int getSpareVMs() {
        return spareVMs;
    }

    public List<Task> getNonRefTasks(){
        return tasks.stream().filter(task -> task.isRefTask() == false).collect(Collectors.toList());
    }

}
