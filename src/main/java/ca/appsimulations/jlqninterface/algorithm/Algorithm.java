package ca.appsimulations.jlqninterface.algorithm;
import ca.appsimulations.jlqninterface.core.lqns.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class Algorithm {
	protected LqnModel lqnModel;
	protected double satThreshold;
	protected int maxVMReplicas;
	protected int maxProcsPerVM;
	protected int spareVMs;
	protected double responseTimeObjective;
	protected boolean bottleneckMaxBStrengthTaskOnly = true;
	protected String inputFilePath;
	protected String outputFilePath;
	protected String xmlOutputFilePath;

	public Algorithm(LqnModel lqnModel) {
		this.lqnModel = lqnModel;
		this.satThreshold = lqnModel.getSatThreshold();
		this.maxVMReplicas = lqnModel.getMaxVMReplicas();
		this.responseTimeObjective = lqnModel.getResponseTimeObjective();
		this.bottleneckMaxBStrengthTaskOnly = lqnModel.isBottleneckBStrengthTaskOnly();
		this.maxProcsPerVM = lqnModel.getMaxProcsPerVM();
		this.spareVMs = lqnModel.getSpareVMs();
		this.inputFilePath = lqnModel.getInputFilePath();
		this.outputFilePath = lqnModel.getAutoInputFilePath();
		this.xmlOutputFilePath = lqnModel.getLqnXmlOutputFilePath();

	}

	public abstract void setup();

	public abstract void run();
}
