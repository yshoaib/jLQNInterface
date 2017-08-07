package ca.appsimulations.jlqninterface.configuration;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class ConfigurationService {

	@Value("${inputFilePath}")
	private String inputFilePath;

	@Value("${autoInputFileName}")
	private String autoInputFilePath;

	@Value("${lqnXmlOutputFileName}")
	private String lqnXmlOutputFilePath;

	@Value("${responseTimeObjective}")
	private double responseTimeObjective;

	@Value("${maxVMReplicas}")
	private int maxVMReplicas = 10;

	@Value("${maxProcsPerVM}")
	private int maxProcsPerVM = 8;

	@Value("${spareVMs}")
	private int spareVMs = 20;

	@Value("${satThreshold}")
	private double satThreshold;

	@Value("${bottleneckMaxBStrengthTaskOnly}")
	private boolean bottleneckMaxBStrengthTaskOnly;


	@PostConstruct
	public void initialize() throws IOException
	{
		ClassPathResource inputFileResource = new ClassPathResource(inputFilePath);
		if(!inputFileResource.exists()){
			throw new FileNotFoundException(inputFileResource.getPath() + " does not exist");
		}
		this.inputFilePath = inputFileResource.getFile().getAbsolutePath();

		this.autoInputFilePath= inputFilePath.substring(0, inputFilePath.lastIndexOf("\\")) + "\\" + autoInputFilePath;
		ClassPathResource autoInputFileResource = new ClassPathResource(autoInputFilePath);
		if(autoInputFileResource.exists())
		{
			autoInputFileResource.getFile().delete();
		}

		this.lqnXmlOutputFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf("\\")) + "\\" + lqnXmlOutputFilePath;
		ClassPathResource lqnXmlOutputFileResource = new ClassPathResource(lqnXmlOutputFilePath);
		if(lqnXmlOutputFileResource.exists())
		{
			lqnXmlOutputFileResource.getFile().delete();
		}
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public String getAutoInputFilePath() {
		return autoInputFilePath;
	}

	public double getResponseTimeObjective() {
		return responseTimeObjective;
	}

	public int getMaxVMReplicas() {
		return maxVMReplicas;
	}

	public int getMaxProcsPerVM() {
		return maxProcsPerVM;
	}

	public int getSpareVMs() {
		return spareVMs;
	}

	public double getSatThreshold() {
		return satThreshold;
	}

	public String getLqnXmlOutputFilePath() {
		return lqnXmlOutputFilePath;
	}

	public boolean isBottleneckMaxBStrengthTaskOnly() {
		return bottleneckMaxBStrengthTaskOnly;
	}
}
