package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public abstract class ActivityDefBase extends Entity {

    protected String name;
    protected double host_demand_mean;
    protected double host_demand_cvsq = 1.0f;
    protected double think_time = 0.0f;
    protected double max_service_time = 0.0f;
    protected CallOrderType call_order = CallOrderType.STOCHASTIC;

    public ActivityDefBase(LqnModel lqnModel, String name, double host_demand_mean) {
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

    public double getHost_demand_mean() {
        return host_demand_mean;
    }

    public void setHost_demand_mean(double hostDemandMean) {
        host_demand_mean = hostDemandMean;
    }

    public double getHost_demand_cvsq() {
        return host_demand_cvsq;
    }

    public void setHost_demand_cvsq(double hostDemandCvsq) {
        host_demand_cvsq = hostDemandCvsq;
    }

    public double getThink_time() {
        return think_time;
    }

    public void setThink_time(double thinkTime) {
        think_time = thinkTime;
    }

    public double getMax_service_time() {
        return max_service_time;
    }

    public void setMax_service_time(double maxServiceTime) {
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
