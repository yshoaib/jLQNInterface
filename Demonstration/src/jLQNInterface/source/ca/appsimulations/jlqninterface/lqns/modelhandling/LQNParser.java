package ca.appsimulations.jlqninterface.lqns.modelhandling;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import org.xml.sax.helpers.*;
import org.xml.sax.*;

import ca.appsimulations.jlqninterface.core.Model;
import ca.appsimulations.jlqninterface.lqns.entities.ActivityDef;
import ca.appsimulations.jlqninterface.lqns.entities.ActivityDefBase;
import ca.appsimulations.jlqninterface.lqns.entities.ActivityPhases;
import ca.appsimulations.jlqninterface.lqns.entities.Entry;
import ca.appsimulations.jlqninterface.lqns.entities.EntryAcType;
import ca.appsimulations.jlqninterface.lqns.entities.LQNConstants;
import ca.appsimulations.jlqninterface.lqns.entities.LQNDefaults;
import ca.appsimulations.jlqninterface.lqns.entities.Processor;
import ca.appsimulations.jlqninterface.lqns.entities.ProcessorSchedulingType;
import ca.appsimulations.jlqninterface.lqns.entities.SynchCall;
import ca.appsimulations.jlqninterface.lqns.entities.Task;
import ca.appsimulations.jlqninterface.lqns.entities.TaskActivities;
import ca.appsimulations.jlqninterface.lqns.entities.TaskSchedulingType;
import ca.appsimulations.jlqninterface.utilities.FileHandler;
import ca.appsimulations.jlqninterface.utilities.Utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public abstract class LQNParser extends DefaultHandler {
	protected Processor curProcessor = null;
	protected Task curTask = null;
	protected Entry curEntry = null;
	protected ActivityDefBase curActivity = null;
	protected TaskActivities curTaskActivities = null;
	protected Model workspace;
	protected boolean isTaskActivities = false;
	protected boolean isEntryPhaseActivities = false;

	// LQX CData Parameters
	protected static final int defaultSpacing = 15;

	public LQNParser(Model workspace) {
		this.workspace = workspace;
	}

	public void ParseFile(String filename) throws FileNotFoundException {

		if (!FileHandler.doesFileExist(filename)) {
			throw new java.io.FileNotFoundException("Filename: " + filename + " doesn't exist");
		}
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);

			SAXParser saxParser = spf.newSAXParser();

			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);
			xmlReader.parse(filename);
		} catch (SAXException se) {
			Utility.debug("[SAX Exception]: " + se.getMessage());
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			Utility.debug("[PCE Exception]: " + pce.getMessage());
			pce.printStackTrace();
		} catch (IOException ie) {
			Utility.debug("[IO Exception]: " + ie.getMessage());
			ie.printStackTrace();
		} catch (Exception e) {
			Utility.debug("[Exception]: " + e.getMessage());
			e.printStackTrace();
		}
	}



	public void printProcessors() {
		int size = workspace.getProcessorsSize();
		for (int i = 0; i < size; i++) {
			Utility.print(workspace.getProcessorAtIndex(i).toString());
		}
	}

	public static String getVariableInitializationCData() {
		StringBuilder cData = new StringBuilder();
		cData.append("\n" + "	DefaultSpacing = " + LQNParser.defaultSpacing + "; \n" + "	satArray = array_create_map(); \n" + "	BStrength = array_create_map();\n"
				+ "	taskData = array_create(); \n" + "	printData = array_create(); \n" + "\n");

		return cData.toString();
	}

	public static String getLQNSolveCData() {
		StringBuilder cData = new StringBuilder();
		cData.append("	solve();\n");

		return cData.toString();
	}

	public static String getTasksBStrengthCData() {
		StringBuilder cData = new StringBuilder();
		cData.append("	////////////////////////////////////////////For tasks /////////////////////////////////////////////\n")
				.append("	for(idx = 0; idx < tasks.size; idx = idx + 1)  \n").append("	{ \n").append("	     taskName = tasks[idx]; \n")
				.append("	     entryName = taskData[taskName][1]; \n").append("	     tObject = task(taskName); \n").append("	     eObject = entry(entryName);\n")
				.append("	     pObject = phase(eObject, 1);\n").append("	     tHostDemand = taskData[taskName][2];\n").append("	     tMultiplicity =taskData[taskName][3];\n")
				.append("	     resource = taskName;\n").append("	     hostDemand = tHostDemand;\n").append("	     if(eObject.has_phase_1)\n").append("	     {\n")
				.append("	         responseTime = eObject.phase1_service_time;\n").append("	     }\n").append("	     else\n").append("	     {\n")
				.append("	         responseTime = pObject.service_time;\n").append("	     }\n").append("	     mult = tMultiplicity;\n")
				.append("	     throughput = tObject.throughput;\n").append("	     util = tObject.utilization;\n").append("\n").append("	     //Calculations\n")
				.append("	     sat = util/mult;\n").append("	     satArray[str(taskName)] = sat;\n").append("\n")
				.append("	     printData[str(taskName)] = [str(taskName), str(hostDemand), str(responseTime), str(mult), str(throughput), str(util), str(sat)];\n").append("	}\n");

		cData.append("	//Calculate BStrength\n").append("	foreach ( index , taskName in tasks ) {\n").append("	     max = 0;\n").append("	     if(Below[taskName].size == 0)\n")
				.append("	     {\n").append("	     	BStrength[str(taskName)] = satArray[taskName];\n").append("	     }\n").append("	     else\n").append("	     {\n")
				.append("	     	foreach ( index , below in Below[taskName] ) {\n").append("	     	   if(max < satArray[below])\n").append("		   {\n")
				.append("		       max = satArray[below];\n").append("		   }\n").append("		}\n").append("		BStrength[str(taskName)] = satArray[taskName]/max;\n").append("\n")
				.append("	     }\n").append("	}\n");

		return cData.toString();
	}

	public static String getProcessorsBStrengthCData() {
		StringBuilder cData = new StringBuilder();

		cData.append("	////////////////////////////////////////////For procs /////////////////////////////////////////////     \n");
		cData.append("	for(idx = 0; idx < processors.size; idx = idx + 1)\n" + "	{\n" + "	     procName = processors[idx];\n" + "	     pObject = processor(procName);\n"
				+ "	     pMultiplicity = procData[procName][1];\n" + "\n" + "	     mult = pMultiplicity;\n" + "	     util = pObject.utilization;\n" + "\n"
				+ "	     //Calculations\n" + "	     sat = util/mult;\n" + "	     satArray[str(procName)] = sat;\n" + "\n"
				+ "	     printData[str(procName)] = [str(procName), \"\", \"\", str(mult), \"\", str(util), str(sat)];\n" + "	}\n");

		return cData.toString();
	}

	public static String getBStrengthPrintAllCData() {

		StringBuilder cData = new StringBuilder();

		cData.append("	////////////////////////////////////////////Print All ///////////////////////////////////////////// \n" + "	println(\"\"); \n"
				+ "	println_spaced(15, \"Resource\",\"ServiceTime\",\"ResponseTime\", \"Multiplicity\",\"Throughput\", \"Utilization\", \"Saturation\", \"BStrength\"); \n"
				+ "	println(\"-----------------------------------------------------------------------------------------------------------------------\"); \n" + "\n"
				+ "	foreach( index,taskName in tasks){ \n" + "	   hostDemand = printData[taskName][1]; \n" + "	   responseTime = printData[taskName][2]; \n"
				+ "	   mult = printData[taskName][3]; \n" + "	   throughput = printData[taskName][4]; \n" + "	   util = printData[taskName][5]; \n"
				+ "	   sat = printData[taskName][6]; \n" + "	   bStrength = BStrength[taskName]; \n"
				+ "	   println_spaced(DefaultSpacing, taskName,  hostDemand, responseTime,mult, throughput, util, sat, bStrength); \n" + "	} \n" + "\n"
				+ "	foreach( index,procName in processors){ \n" + "	   hostDemand = printData[procName][1]; \n" + "	   responseTime = printData[procName][2]; \n"
				+ "	   mult = printData[procName][3]; \n" + "	   throughput = printData[procName][4]; \n" + "	   util = printData[procName][5]; \n"
				+ "	   sat = printData[procName][6]; \n" + "	   bStrength = -1; \n"
				+ "	   println_spaced(DefaultSpacing, procName,  hostDemand, responseTime,mult, throughput, util, sat, bStrength); \n" + "	}\n");

		return cData.toString();

	}

	public String getLQXCData() {
		StringBuilder cData = new StringBuilder();
		cData.append(getVariableInitializationCData());
		cData.append(workspace.getProcessorsCDataString() + "\n\n");
		cData.append(workspace.getTasksCDataString() + "\n\n");
		cData.append(workspace.getBelowCDataString() + "\n\n");
		cData.append(LQNParser.getLQNSolveCData());
		cData.append(LQNParser.getTasksBStrengthCData());
		cData.append(LQNParser.getProcessorsBStrengthCData());
		cData.append(LQNParser.getBStrengthPrintAllCData());

		return cData.toString();
	}
}
