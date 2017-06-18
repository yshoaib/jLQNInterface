package ca.appsimulations.jlqninterface.lqn.entities;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class ActivityDefBase extends Entity {

	protected String name;
	protected float host_demand_mean;
	protected float host_demand_cvsq = 1.0f;
	protected float think_time = 0.0f;
	protected float max_service_time = 0.0f;
	protected CallOrderType call_order = CallOrderType.STOCHASTIC;

	public ActivityDefBase(LqnModel lqnModel, String name, float host_demand_mean) {
		this.lqnModel = lqnModel;
		this.name = name;
		this.host_demand_mean = host_demand_mean;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getHost_demand_mean() {
		return host_demand_mean;
	}

	public void setHost_demand_mean(float hostDemandMean) {
		host_demand_mean = hostDemandMean;
	}

	public float getHost_demand_cvsq() {
		return host_demand_cvsq;
	}

	public void setHost_demand_cvsq(float hostDemandCvsq) {
		host_demand_cvsq = hostDemandCvsq;
	}

	public float getThink_time() {
		return think_time;
	}

	public void setThink_time(float thinkTime) {
		think_time = thinkTime;
	}

	public float getMax_service_time() {
		return max_service_time;
	}

	public void setMax_service_time(float maxServiceTime) {
		max_service_time = maxServiceTime;
	}

	public CallOrderType getCall_order() {
		return call_order;
	}

	public void setCall_order(CallOrderType callOrder) {
		call_order = callOrder;
	}

	@Override
	public String toString() {
		return name;
	}
}
