package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

import java.util.ArrayList;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public abstract class TaskType extends Entity {

    protected String name;
    protected int multiplicity = 1;
    protected int replication = 1;
    protected TaskSchedulingType scheduling = TaskSchedulingType.FIFO;
    protected TaskSubClass subclass;
    protected double think_time = 0;
    protected int priority = 0;
    protected int queue_length = 0;
    protected boolean activity_graph;
    protected int intially = multiplicity;
    protected ArrayList<TaskActivity> taskActivities = new ArrayList<TaskActivity>();

    public TaskType(LqnModel lqnModel, String name, boolean activity_graph) {
        this.lqnModel = lqnModel;
        this.name = name;
        this.activity_graph = activity_graph;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    public String getMutiplicityString() {
        if (multiplicity == LqnConstants.INFINITY.getConstantValue()) {
            return LqnConstants.INFINITY.toString();
        }
        else {
            return Integer.toString(multiplicity);
        }
    }

    public int getReplication() {
        return replication;
    }

    public void setReplication(int replication) {
        this.replication = replication;
    }

    public TaskSchedulingType getScheduling() {
        return scheduling;
    }

    public void setScheduling(TaskSchedulingType scheduling) {
        this.scheduling = scheduling;
    }

    public double getThink_time() {
        return think_time;
    }

    public void setThink_time(double thinkTime) {
        think_time = thinkTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getQueue_length() {
        return queue_length;
    }

    public void setQueue_length(int queueLength) {
        queue_length = queueLength;
    }

    public boolean isActivity_graph() {
        return activity_graph;
    }

    public void setActivity_graph(boolean activityGraph) {
        activity_graph = activityGraph;
    }

    public int getIntially() {
        return intially;
    }

    public void setIntially(int intially) {
        this.intially = intially;
    }

    public TaskSubClass getSubclass() {
        return subclass;
    }

    public void setSubclass(TaskSubClass subclass) {
        this.subclass = subclass;
    }

    public ArrayList<TaskActivity> getTaskActivities() {
        return taskActivities;
    }

    public TaskActivity buildAndAddTaskActivity() {
        // TODO need to find taskActivitiesByName ...or similar functionality
        TaskActivity taskActivity = new TaskActivity(this.lqnModel);
        addTaskActivity(taskActivity);
        return taskActivity;
    }

    public void addTaskActivity(TaskActivity taskActivity) {
        taskActivities.add(taskActivity);
    }

    public void addActivityToTaskActivity(ActivityDef a, TaskActivity tA) {
        tA.getActivities().add(a);
    }

    public ActivityDef getActivityAtIndex(int i) {
        // TODO
        if (taskActivities.isEmpty()) {
            return null;
        }
        else {
            return taskActivities.get(0).getActivities().get(i);
        }
    }

    public ActivityDef getActivityByName(String name) {
        if (taskActivities.isEmpty()) {
            return null;
        }

        for (TaskActivity tA : taskActivities) {
            ArrayList<ActivityDef> activities = tA.getActivities();
            int size = activities.size();
            for (int i = 0; i < size; i++) {
                ActivityDef a = activities.get(i);
                if (a.getName().equals(name)) {
                    return a;
                }
            }
        }

        return null;
    }

    public ActivityDef getActivityByName(String name, TaskActivity tA) {
        if (tA == null) {
            return null;
        }
        ArrayList<ActivityDef> activities = tA.getActivities();
        int size = activities.size();
        for (int i = 0; i < size; i++) {
            ActivityDef a = activities.get(i);
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public boolean isRefTask() {
        return scheduling.equals(TaskSchedulingType.REF);
    }
}
