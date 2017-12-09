package ca.appsimulations.jlqninterface.lqn.model;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.utilities.Utility;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Data
@NoArgsConstructor
@Accessors(fluent = true, chain = true)
public class LqnModel {
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<Task> refTasks = new ArrayList<>();
    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<Processor> processors = new ArrayList<>();
    private ArrayList<ActivityDefBase> activities = new ArrayList<>();
    private Result result = new Result();
    private int maxBelow = 0;

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

    public int processorsSize() {
        return processors.size();
    }

    public int tasksSize() {
        return tasks.size();
    }

    public Processor processorAtIndex(int index) {
        return processors.get(index);
    }

    public Task taskAtIndex(int index) {
        return tasks.get(index);
    }


    public ArrayList<Task> buildRefTasksFromExistingTasks() {
        for (Task t : tasks) {
            if (t.getScheduling() == TaskSchedulingType.REF) {
                refTasks.add(t);
            }
        }
        return refTasks;
    }

    // Call Entry.linkEntries() after dest array is properly set.
    // After this method, Task.buildDestTree() may be called.
    public void linkEntries() {
        int entriesSize = entries.size();
        for (int k = 0; k < entriesSize; k++) {
            Entry e = entries.get(k);
            e.clearDestSrcEntries();
        }

        for (int k = 0; k < entries.size(); k++) {
            Entry e = entries.get(k);
            int size = e.getSyncDestStrSize();
            for (int i = 0; i < size; i++) {
                Entry tmp = entryByName(e.getSyncDestStr(i));
                if (tmp != null) {
                    e.addSyncDest(tmp);
                }
            }

            e.linkSynchCalls();
        }
    }

    public ArrayList<Entry> buildSrcEntries(String entryName) {
        //find srcEntries without calling linkEntries
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

    public String buildProcessorsCDataString() {
        StringBuilder strB = new StringBuilder();
        strB.append("	processors = array_create(); \n" + "	processors = " +
                    Utility.listToQuotationStrArray(processors) + ";\n" + "	procData = array_create();\n");

        for (int i = 0; i < processors.size(); i++) {
            Processor p = processors.get(i);
            strB.append("	procData[\"" + p + "\"] = [\"" + p + "\", " + p.getMutiplicityString() + "] ;\n");
        }

        return strB.toString();
    }

    // Call buildDestTree() method after calling linkEntries()
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

    public String buildTasksCDataString() {
        StringBuilder strB = new StringBuilder();
        strB.append("	tasks = array_create(); \n").append(
                "	tasks = " + Utility.listToQuotationStrArray(tasks) + ";\n");

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
                    }
                    else {
                        strB.append("0.0 , ");
                    }
                }
                else if (e.getEntryType() == EntryAcType.NONE) {
                    // TODO
                    strB.append("-1" + ", ");
                }
            }
            strB.append(t.getMutiplicityString() + "] ;\n");

        }

        return strB.toString();
    }

    public ArrayList<Task> taskWithBelowSize(int size) {
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
            ArrayList<Task> list = taskWithBelowSize(j);
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

    public String buildBelowCDataString() {
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


    public Processor processorByName(String name) {
        return processorByName(name, false);
    }

    public Processor processorByName(String name, boolean createNew) {
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

    public Task taskByName(String name) {
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

    public Task taskByName(String name, Processor p, boolean createNew) {
        Task t = findTaskInProcessor(name, p);
        if (t != null) {
            return t;
        }

        if (createNew) {
            return (new Task(this, name, p));
        }

        return null;
    }

    private Task findTaskInProcessor(String name, Processor p) {
        if (p == null) {
            return null;
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

    public Entry entryByName(String name) {
        int size = entries.size();
        for (int i = 0; i < size; i++) {
            Entry e = entries.get(i);
            if (e.getName().equals(name)) {
                return e;
            }
        }

        return null;
    }

    public Entry entryByName(String name, Task t, boolean createNew) {
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

    public ActivityPhases activityPHByName(String name, Entry e, int phase, boolean createNew) {
        ActivityDefBase a = e.getActivityByName(name);
        if ((a != null) && (a.getName().equals(name))) {
            return (ActivityPhases) a;
        }
        else if (createNew) {
            return (new ActivityPhases(this, name, e, phase));
        }

        return null;
    }

    public ActivityDef activityDefByName(String name, Task t, TaskActivity tA, Entry e, boolean createNew) {
        ActivityDef a = t.getActivityByName(name, tA);
        if ((a != null) && (a.getName().equals(name))) {
            return a;
        }
        else if (createNew) {
            return (new ActivityDef(this, name, t, tA, e));
        }

        return null;
    }

    public List<Task> nonRefTasks() {
        return tasks.stream().filter(task -> task.isRefTask() == false).collect(Collectors.toList());
    }

}
