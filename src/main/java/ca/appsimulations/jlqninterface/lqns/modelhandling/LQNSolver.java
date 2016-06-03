package ca.appsimulations.jlqninterface.lqns.modelhandling;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.OperatingSystemMXBean;

import ca.appsimulations.jlqninterface.lqns.entities.ResultTypes;
import ca.appsimulations.jlqninterface.utilities.Utility;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LQNSolver {
	private static final Logger logger = LoggerFactory.getLogger(LQNSolver.class);

	public static boolean solveLqns(String inputPath, String outputPath, LQNXmlResultParser lqnResultParser, String xmlOutputFilePath) {
		StringBuilder strCmd = new StringBuilder();
		Boolean isError = false;

		// strCmd.append("lqns ");
		// strCmd.append(inputPath + " > " + outputPath);
		// System.out.println(strCmd.toString());
		logger.debug("----Running Solver----");
		String[] cmd = { "bash", "-c", "lqns " + inputPath };


		try {
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();

			String read;

			BufferedReader inputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((read = inputReader.readLine()) != null) {
				logger.debug("[Input Reader]: " + read);
				if (read.indexOf("error:") != -1 || read.indexOf("advisory:") != -1) {
					logger.debug("[error] above");
					isError = true;
				}
			}
			inputReader.close();
			while ((read = errorReader.readLine()) != null) {
				isError = true;
				logger.debug("[Error Reader]: " + read);
			}
			errorReader.close();

			if ((p.exitValue() != 0) || isError) {
				return false;
			}
			
			logger.debug("Parsing output file: " + xmlOutputFilePath);

			try {
				lqnResultParser.ParseFile(xmlOutputFilePath);
			} catch (FileNotFoundException fnfe) {
				logger.debug("[FileNotFoundException]: " + fnfe.getMessage());
				fnfe.printStackTrace();
				return false;
			}
			
			return lqnResultParser.workspace.getResult().getResultValid().equals(ResultTypes.YES);
			
		} catch (IOException io) {
			logger.debug("[IOException]: " + io.getMessage());
		} catch (InterruptedException intE) {
			logger.debug("[InterruptedException]: " + intE.getMessage());
		}
		return false;
	}

	public static boolean solve2(String inputPath, String outputPath) {
		StringBuilder strCmd = new StringBuilder();
		Boolean isError = false;

		// strCmd.append("lqns ");
		// strCmd.append(inputPath + " > " + outputPath);
		// System.out.println(strCmd.toString());
		logger.debug("----Running Solver----");

		//convert XML to simple lqn format file
		String modifiedInputPath = inputPath.substring(0, inputPath.lastIndexOf(".lqnx")) + ".lqn";
		String strCmd0 = "lqn2lqn " + inputPath + " > " + modifiedInputPath;
		String[] cmd0 = { "bash", "-c", strCmd0 };
		Process p;
		try {
			logger.debug("Running cmd: " + strCmd0);
			p = Runtime.getRuntime().exec(cmd0);
			p.waitFor();

			String read;

			BufferedReader inputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((read = inputReader.readLine()) != null) {
				logger.debug("[Input Reader]: " + read);
				if (read.indexOf("error:") != -1 || read.indexOf("advisory:") != -1) {
					logger.debug("[error] above");
					isError = true;
				}
			}
			inputReader.close();
			while ((read = errorReader.readLine()) != null) {
				isError = true;
				logger.debug("[Error Reader]: " + read);
			}
			errorReader.close();
		} catch (InterruptedException e) {
			logger.debug("[InterruptedException]: " + e.getMessage());
		} catch (IOException e) {
			logger.debug("[IOException]: " + e.getMessage());
		}

		String[] cmd = { "bash", "-c", "lqns -x " + modifiedInputPath };
		try {
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();

			String read;

			BufferedReader inputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((read = inputReader.readLine()) != null) {
				logger.debug("[Input Reader]: " + read);
				if (read.indexOf("error:") != -1 || read.indexOf("advisory:") != -1) {
					logger.debug("[error] above");
					isError = true;
				}
			}
			inputReader.close();
			while ((read = errorReader.readLine()) != null) {
				isError = true;
				logger.debug("[Error Reader]: " + read);
			}
			errorReader.close();

			if ((p.exitValue() != 0) || isError) {
				return false;
			} else {
				return true;
			}
		} catch (IOException io) {
			logger.debug("[IOException]: " + io.getMessage());
		} catch (InterruptedException intE) {
			logger.debug("[InterruptedException]: " + intE.getMessage());
		}
		return false;
	}
}
