package ca.appsimulations.jlqninterface.lqn.entities;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public class Result {
	private ResultTypes isResultValid = ResultTypes.NO;
	
	private float proc_utilization; // Processor utilization for a task, entry,
	// or
	// activity.
	private float proc_waiting; // Waiting time at a processor for an activity.
	private float phase1_proc_waiting; // Waiting time at a processor for phase
	// 1 of an
	// entry.
	private float phase2_proc_waiting; // Waiting time at a processor for phase
	// 2 of an
	// entry.
	private float phase3_proc_waiting; // Waiting time at a processor for phase
	// 3 of an
	// entry.

	private float open_wait_time; // Waiting time for open arrivals.

	private float service_time = Float.MAX_VALUE; // Activity service time.

	private float loss_probability; // Probability of dropping an asynchronous
	// message.

	private float phase1_service_time; // Service time for phase 1 of an entry.
	private float phase2_service_time; // Service time for phase 2 of an entry.
	private float phase3_service_time; // Service time for phase 3 of an entry.

	private float service_time_variance; // Variance for an activity.
	private float phaseX_service_time_variance; // Variance for phase X of an
	// entry.

	private float phase1_utilization; // Utilization for phase 1 of an entry.
	private float phase2_utilization; // Utilization for phase 2 of an entry.
	private float phase3_utilization; // Utilization for phase 3 of an entry.

	private float prob_exceed_max_service_time; //
	private float squared_coeff_variation; // Squared coefficient of variation
	// over all
	// phases of an entry
	private float throughput_bound; // Throughput bound for an entry.
	private float throughput; // Throughput for a task, entry or activity.
	private float utilization; // Utilization for a task, entry, activity.
	private float waiting; // Rendezvous delay
	private float waiting_variance; // Variance of delay for a rendezvous

	public float getProc_utilization() {
		return proc_utilization;
	}

	public void setProc_utilization(float procUtilization) {
		proc_utilization = procUtilization;
	}

	public float getProc_waiting() {
		return proc_waiting;
	}

	public void setProc_waiting(float procWaiting) {
		proc_waiting = procWaiting;
	}

	public float getPhase1_proc_waiting() {
		return phase1_proc_waiting;
	}

	public void setPhase1_proc_waiting(float phase1ProcWaiting) {
		phase1_proc_waiting = phase1ProcWaiting;
	}

	public float getPhase2_proc_waiting() {
		return phase2_proc_waiting;
	}

	public void setPhase2_proc_waiting(float phase2ProcWaiting) {
		phase2_proc_waiting = phase2ProcWaiting;
	}

	public float getPhase3_proc_waiting() {
		return phase3_proc_waiting;
	}

	public void setPhase3_proc_waiting(float phase3ProcWaiting) {
		phase3_proc_waiting = phase3ProcWaiting;
	}

	public float getOpen_wait_time() {
		return open_wait_time;
	}

	public void setOpen_wait_time(float openWaitTime) {
		open_wait_time = openWaitTime;
	}

	public float getService_time() {
		return service_time;
	}

	public void setService_time(float serviceTime) {
		service_time = serviceTime;
	}

	public float getLoss_probability() {
		return loss_probability;
	}

	public void setLoss_probability(float lossProbability) {
		loss_probability = lossProbability;
	}

	public float getPhase1_service_time() {
		return phase1_service_time;
	}

	public void setPhase1_service_time(float phase1ServiceTime) {
		phase1_service_time = phase1ServiceTime;
	}

	public float getPhase2_service_time() {
		return phase2_service_time;
	}

	public void setPhase2_service_time(float phase2ServiceTime) {
		phase2_service_time = phase2ServiceTime;
	}

	public float getPhase3_service_time() {
		return phase3_service_time;
	}

	public void setPhase3_service_time(float phase3ServiceTime) {
		phase3_service_time = phase3ServiceTime;
	}

	public float getService_time_variance() {
		return service_time_variance;
	}

	public void setService_time_variance(float serviceTimeVariance) {
		service_time_variance = serviceTimeVariance;
	}

	public float getPhaseX_service_time_variance() {
		return phaseX_service_time_variance;
	}

	public void setPhaseX_service_time_variance(float phaseXServiceTimeVariance) {
		phaseX_service_time_variance = phaseXServiceTimeVariance;
	}

	public float getPhase1_utilization() {
		return phase1_utilization;
	}

	public void setPhase1_utilization(float phase1Utilization) {
		phase1_utilization = phase1Utilization;
	}

	public float getPhase2_utilization() {
		return phase2_utilization;
	}

	public void setPhase2_utilization(float phase2Utilization) {
		phase2_utilization = phase2Utilization;
	}

	public float getPhase3_utilization() {
		return phase3_utilization;
	}

	public void setPhase3_utilization(float phase3Utilization) {
		phase3_utilization = phase3Utilization;
	}

	public float getProb_exceed_max_service_time() {
		return prob_exceed_max_service_time;
	}

	public void setProb_exceed_max_service_time(float probExceedMaxServiceTime) {
		prob_exceed_max_service_time = probExceedMaxServiceTime;
	}

	public float getSquared_coeff_variation() {
		return squared_coeff_variation;
	}

	public void setSquared_coeff_variation(float squaredCoeffVariation) {
		squared_coeff_variation = squaredCoeffVariation;
	}

	public float getThroughput_bound() {
		return throughput_bound;
	}

	public void setThroughput_bound(float throughputBound) {
		throughput_bound = throughputBound;
	}

	public float getThroughput() {
		return throughput;
	}

	public void setThroughput(float throughput) {
		this.throughput = throughput;
	}

	public float getUtilization() {
		return utilization;
	}

	public void setUtilization(float utilization) {
		this.utilization = utilization;
	}

	public float getWaiting() {
		return waiting;
	}

	public void setWaiting(float waiting) {
		this.waiting = waiting;
	}

	public float getWaiting_variance() {
		return waiting_variance;
	}

	public void setWaiting_variance(float waitingVariance) {
		waiting_variance = waitingVariance;
	}
	
	public void setResultValid(String valid)
	{
		isResultValid = ResultTypes.getValue(valid);
	}
	
	public ResultTypes getResultValid()
	{
		return isResultValid;
	}

}
