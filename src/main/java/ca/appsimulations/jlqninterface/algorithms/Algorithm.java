package ca.appsimulations.jlqninterface.algorithms;
import ca.appsimulations.jlqninterface.core.Model;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class Algorithm {
	protected Model workspace;
	protected double satThreshold;
	protected int maxVMReplicas;
	protected int maxProcsPerVM;
	protected int spareVMs;
	protected double responseTimeObjective;
	protected boolean bottleneckMaxBStrengthTaskOnly = true;
	protected String inputFilePath;
	protected String outputFilePath;
	protected String xmlOutputFilePath;

	public Algorithm(Model workspace) {
		this.workspace = workspace;
		this.satThreshold = workspace.getSatThreshold();
		this.maxVMReplicas = workspace.getMaxVMReplicas();
		this.responseTimeObjective = workspace.getResponseTimeObjective();
		this.bottleneckMaxBStrengthTaskOnly = workspace.isBottleneckBStrengthTaskOnly();
		this.maxProcsPerVM = workspace.getMaxProcsPerVM();
		this.spareVMs = workspace.getSpareVMs();
		this.inputFilePath = workspace.getInputFilePath();
		this.outputFilePath = workspace.getAutoInputFilePath();
		this.xmlOutputFilePath = workspace.getLqnXmlOutputFilePath();

	}

	public abstract void setup();

	public abstract void run();
}
