package ca.appsimulations.jlqninterface.core;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import ca.appsimulations.jlqninterface.utilities.Utility;

public class Configuration {
	private static Configuration instance = null;
	private String inputFilePath;
	private String outputFilePath;
	private double responseTimeObjective;
	private int maxVMReplicas = 10;
	private int maxProcsPerVM = 8;
	private int spareVMs = 20;
	private double satThreshold;
	private String lqnXmlOutputFilePath;
	private boolean bottleneckMaxBStrengthTaskOnly;

	private String configFile;

	public Configuration(String configFile) {
		this.configFile = configFile;
		LoadConfigurationData();
	}

	private void LoadConfigurationData() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(configFile));
		} catch (IOException e) {
			Utility.debug("[IOException]: " + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inputFilePath = prop.getProperty("inputFilePath");
		outputFilePath = prop.getProperty("outputFilePath");
		lqnXmlOutputFilePath = prop.getProperty("lqnXmlOutputFilePath");
		responseTimeObjective = Double.parseDouble(prop.getProperty("responseTimeObjective"));
		maxVMReplicas = Integer.parseInt(prop.getProperty("maxVMReplicas"));
		maxProcsPerVM = Integer.parseInt(prop.getProperty("maxProcsPerVM"));
		spareVMs = Integer.parseInt(prop.getProperty("spareVMs"));
		satThreshold = Double.parseDouble(prop.getProperty("satThreshold"));
		bottleneckMaxBStrengthTaskOnly = Boolean.parseBoolean(prop.getProperty("bottleneckMaxBStrengthTaskOnly"));
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public String getConfigFile() {
		return configFile;
	}

	public String getLqnXmlOutputFilePath() {
		return lqnXmlOutputFilePath;
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

	public boolean isBottleneckMaxBStrengthTaskOnly() {
		return bottleneckMaxBStrengthTaskOnly;
	}

	public int getMaxProcsPerVM() {
		return maxProcsPerVM;
	}

	public int getSpareVMs() {
		return spareVMs;
	}

}
