package ca.appsimulations.jlqninterface.lqn.entities;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public class Result {
    private ResultTypes isResultValid = ResultTypes.NO;

    private double proc_utilization; // Processor utilization for a task, entry,
    // or
    // activity.
    private double proc_waiting; // Waiting time at a processor for an activity.
    private double phase1_proc_waiting; // Waiting time at a processor for phase
    // 1 of an
    // entry.
    private double phase2_proc_waiting; // Waiting time at a processor for phase
    // 2 of an
    // entry.
    private double phase3_proc_waiting; // Waiting time at a processor for phase
    // 3 of an
    // entry.

    private double open_wait_time; // Waiting time for open arrivals.

    private double service_time = Float.MAX_VALUE; // Activity service time.

    private double loss_probability; // Probability of dropping an asynchronous
    // message.

    private double phase1_service_time; // Service time for phase 1 of an entry.
    private double phase2_service_time; // Service time for phase 2 of an entry.
    private double phase3_service_time; // Service time for phase 3 of an entry.

    private double service_time_variance; // Variance for an activity.
    private double phaseX_service_time_variance; // Variance for phase X of an
    // entry.

    private double phase1_utilization; // Utilization for phase 1 of an entry.
    private double phase2_utilization; // Utilization for phase 2 of an entry.
    private double phase3_utilization; // Utilization for phase 3 of an entry.

    private double prob_exceed_max_service_time; //
    private double squared_coeff_variation; // Squared coefficient of variation
    // over all
    // phases of an entry
    private double throughput_bound; // Throughput bound for an entry.
    private double throughput; // Throughput for a task, entry or activity.
    private double utilization; // Utilization for a task, entry, activity.
    private double waiting; // Rendezvous delay
    private double waiting_variance; // Variance of delay for a rendezvous

    public double getProc_utilization() {
        return proc_utilization;
    }

    public void setProc_utilization(double procUtilization) {
        proc_utilization = procUtilization;
    }

    public double getProc_waiting() {
        return proc_waiting;
    }

    public void setProc_waiting(double procWaiting) {
        proc_waiting = procWaiting;
    }

    public double getPhase1_proc_waiting() {
        return phase1_proc_waiting;
    }

    public void setPhase1_proc_waiting(double phase1ProcWaiting) {
        phase1_proc_waiting = phase1ProcWaiting;
    }

    public double getPhase2_proc_waiting() {
        return phase2_proc_waiting;
    }

    public void setPhase2_proc_waiting(double phase2ProcWaiting) {
        phase2_proc_waiting = phase2ProcWaiting;
    }

    public double getPhase3_proc_waiting() {
        return phase3_proc_waiting;
    }

    public void setPhase3_proc_waiting(double phase3ProcWaiting) {
        phase3_proc_waiting = phase3ProcWaiting;
    }

    public double getOpen_wait_time() {
        return open_wait_time;
    }

    public void setOpen_wait_time(double openWaitTime) {
        open_wait_time = openWaitTime;
    }

    public double getService_time() {
        return service_time;
    }

    public void setService_time(double serviceTime) {
        service_time = serviceTime;
    }

    public double getLoss_probability() {
        return loss_probability;
    }

    public void setLoss_probability(double lossProbability) {
        loss_probability = lossProbability;
    }

    public double getPhase1_service_time() {
        return phase1_service_time;
    }

    public void setPhase1_service_time(double phase1ServiceTime) {
        phase1_service_time = phase1ServiceTime;
    }

    public double getPhase2_service_time() {
        return phase2_service_time;
    }

    public void setPhase2_service_time(double phase2ServiceTime) {
        phase2_service_time = phase2ServiceTime;
    }

    public double getPhase3_service_time() {
        return phase3_service_time;
    }

    public void setPhase3_service_time(double phase3ServiceTime) {
        phase3_service_time = phase3ServiceTime;
    }

    public double getService_time_variance() {
        return service_time_variance;
    }

    public void setService_time_variance(double serviceTimeVariance) {
        service_time_variance = serviceTimeVariance;
    }

    public double getPhaseX_service_time_variance() {
        return phaseX_service_time_variance;
    }

    public void setPhaseX_service_time_variance(double phaseXServiceTimeVariance) {
        phaseX_service_time_variance = phaseXServiceTimeVariance;
    }

    public double getPhase1_utilization() {
        return phase1_utilization;
    }

    public void setPhase1_utilization(double phase1Utilization) {
        phase1_utilization = phase1Utilization;
    }

    public double getPhase2_utilization() {
        return phase2_utilization;
    }

    public void setPhase2_utilization(double phase2Utilization) {
        phase2_utilization = phase2Utilization;
    }

    public double getPhase3_utilization() {
        return phase3_utilization;
    }

    public void setPhase3_utilization(double phase3Utilization) {
        phase3_utilization = phase3Utilization;
    }

    public double getProb_exceed_max_service_time() {
        return prob_exceed_max_service_time;
    }

    public void setProb_exceed_max_service_time(double probExceedMaxServiceTime) {
        prob_exceed_max_service_time = probExceedMaxServiceTime;
    }

    public double getSquared_coeff_variation() {
        return squared_coeff_variation;
    }

    public void setSquared_coeff_variation(double squaredCoeffVariation) {
        squared_coeff_variation = squaredCoeffVariation;
    }

    public double getThroughput_bound() {
        return throughput_bound;
    }

    public void setThroughput_bound(double throughputBound) {
        throughput_bound = throughputBound;
    }

    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public double getUtilization() {
        return utilization;
    }

    public void setUtilization(double utilization) {
        this.utilization = utilization;
    }

    public double getWaiting() {
        return waiting;
    }

    public void setWaiting(double waiting) {
        this.waiting = waiting;
    }

    public double getWaiting_variance() {
        return waiting_variance;
    }

    public void setWaiting_variance(double waitingVariance) {
        waiting_variance = waitingVariance;
    }

    public ResultTypes getResultValid() {
        return isResultValid;
    }

    public void setResultValid(String valid) {
        isResultValid = ResultTypes.getValue(valid);
    }

}
