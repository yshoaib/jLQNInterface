package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */
@Slf4j
public abstract class PhaseActivities extends Entity {
    protected final int MAX_PHASES = 3;
    protected final ArrayList<ActivityPhases> activityPhases = new ArrayList<ActivityPhases>(MAX_PHASES);

    public PhaseActivities(LqnModel lqnModel) {
        this.lqnModel = lqnModel;
    }

    /*
     * public ArrayList<ActivityPhases> getActivityPhases() { return
     * activityPhases; }
     */

    public void addActivityPhase(ActivityPhases a, int phase) {
        // TODO
        if (phase > MAX_PHASES) {
            log.debug("[PROBLEM]: phase exceed MAX_PHASES (" + MAX_PHASES + ")");
            return;
        }
        activityPhases.add(phase - 1, a);
    }

    public ActivityPhases getActivityAtPhase(int phase) {
        if ((phase - 1) >= activityPhases.size()) {
            return null;
        }
        return activityPhases.get(phase - 1);
    }

    public int getActivitiesSize() {
        return activityPhases.size();
    }

}
