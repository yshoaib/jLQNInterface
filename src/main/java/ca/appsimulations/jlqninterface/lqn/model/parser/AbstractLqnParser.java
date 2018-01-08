package ca.appsimulations.jlqninterface.lqn.model.parser;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.utilities.FileHandler;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
public abstract class AbstractLqnParser extends DefaultHandler {
    // LQX CData Parameters
    protected static final int defaultSpacing = 15;
    protected Processor curProcessor = null;
    protected Task curTask = null;
    protected Entry curEntry = null;
    protected ActivityDefBase curActivity = null;
    protected TaskActivity curTaskActivity = null;
    protected LqnModel lqnModel;
    protected boolean isTaskActivities = false;
    protected boolean isEntryPhaseActivities = false;

    public AbstractLqnParser(LqnModel lqnModel) {
        this.lqnModel = lqnModel;
    }

    public static String getVariableInitializationCData() {
        StringBuilder cData = new StringBuilder();
        cData.append("\n" + "	DefaultSpacing = " + AbstractLqnParser.defaultSpacing + "; \n" +
                     "	satArray = array_create_map(); \n" + "	BStrength = array_create_map();\n"
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
        cData.append(
                "	////////////////////////////////////////////For tasks " +
                "/////////////////////////////////////////////\n")
                .append("	for(idx = 0; idx < tasks.size; idx = idx + 1)  \n").append("	{ \n").append(
                "	     taskName = tasks[idx]; \n")
                .append("	     entryName = taskData[taskName][1]; \n").append("	     tObject = task(taskName); " +
                                                                                 "\n").append(
                "	     eObject = entry(entryName);\n")
                .append("	     pObject = phase(eObject, 1);\n").append("	     tHostDemand = taskData[taskName][2];" +
                                                                          "\n").append(
                "	     tMultiplicity =taskData[taskName][3];\n")
                .append("	     resource = taskName;\n").append("	     hostDemand = tHostDemand;\n").append(
                "	     if(eObject.has_phase_1)\n").append("	     {\n")
                .append("	         responseTime = eObject.phase1_service_time;\n").append("	     }\n").append(
                "	     else\n").append("	     {\n")
                .append("	         responseTime = pObject.service_time;\n").append("	     }\n").append(
                "	     mult = tMultiplicity;\n")
                .append("	     throughput = tObject.throughput;\n").append("	     util = tObject.utilization;\n")
                .append(
                "\n").append("	     //Calculations\n")
                .append("	     sat = util/mult;\n").append("	     satArray[str(taskName)] = sat;\n").append("\n")
                .append("	     printData[str(taskName)] = [str(taskName), str(hostDemand), str(responseTime), str" +
                        "(mult), str(throughput), str(util), str(sat)];\n").append(
                "	}\n");

        cData.append("	//Calculate BStrength\n").append("	foreach ( index , taskName in tasks ) {\n").append(
                "	     max = 0;\n").append("	     if(Below[taskName].size == 0)\n")
                .append("	     {\n").append("	     	BStrength[str(taskName)] = satArray[taskName];\n").append(
                "	     }\n").append("	     else\n").append("	     {\n")
                .append("	     	foreach ( index , below in Below[taskName] ) {\n").append(
                "	     	   if(max < satArray[below])\n").append("		   {\n")
                .append("		       max = satArray[below];\n").append("		   }\n").append("		}\n").append(
                "		BStrength[str(taskName)] = satArray[taskName]/max;\n").append("\n")
                .append("	     }\n").append("	}\n");

        return cData.toString();
    }

    public static String getProcessorsBStrengthCData() {
        StringBuilder cData = new StringBuilder();

        cData.append(
                "	////////////////////////////////////////////For procs " +
                "/////////////////////////////////////////////     \n");
        cData.append("	for(idx = 0; idx < processors.size; idx = idx + 1)\n" + "	{\n" +
                     "	     procName = processors[idx];\n" + "	     pObject = processor(procName);\n"
                     + "	     pMultiplicity = procData[procName][1];\n" + "\n" + "	     mult = pMultiplicity;\n" +
                     "	     util = pObject.utilization;\n" + "\n"
                     + "	     //Calculations\n" + "	     sat = util/mult;\n" +
                     "	     satArray[str(procName)] = sat;\n" + "\n"
                     +
                     "	     printData[str(procName)] = [str(procName), \"\", \"\", str(mult), \"\", str(util), str" +
                     "(sat)];\n" +
                     "	}\n");

        return cData.toString();
    }

    public static String getBStrengthPrintAllCData() {

        StringBuilder cData = new StringBuilder();

        cData.append(
                "	////////////////////////////////////////////Print All " +
                "///////////////////////////////////////////// \n" +
                "	println(\"\"); \n"
                +
                "	println_spaced(15, \"Resource\",\"ServiceTime\",\"ResponseTime\", \"Multiplicity\",\"Throughput\"," +
                " \"Utilization\", \"Saturation\", \"BStrength\"); \n"
                +
                "	println" +
                "(\"-----------------------------------------------------------------------------------------------------------------------\"); \n" +
                "\n"
                + "	foreach( index,taskName in tasks){ \n" + "	   hostDemand = printData[taskName][1]; \n" +
                "	   responseTime = printData[taskName][2]; \n"
                + "	   mult = printData[taskName][3]; \n" + "	   throughput = printData[taskName][4]; \n" +
                "	   util = printData[taskName][5]; \n"
                + "	   sat = printData[taskName][6]; \n" + "	   bStrength = BStrength[taskName]; \n"
                +
                "	   println_spaced(DefaultSpacing, taskName,  hostDemand, responseTime,mult, throughput, util, sat," +
                " bStrength); \n" +
                "	} \n" + "\n"
                + "	foreach( index,procName in processors){ \n" + "	   hostDemand = printData[procName][1]; \n" +
                "	   responseTime = printData[procName][2]; \n"
                + "	   mult = printData[procName][3]; \n" + "	   throughput = printData[procName][4]; \n" +
                "	   util = printData[procName][5]; \n"
                + "	   sat = printData[procName][6]; \n" + "	   bStrength = -1; \n"
                +
                "	   println_spaced(DefaultSpacing, procName,  hostDemand, responseTime,mult, throughput, util, sat," +
                " bStrength); \n" +
                "	}\n");

        return cData.toString();

    }

    public LqnModel getLqnModel() {
        return lqnModel;
    }

    public void setLqnModel(LqnModel lqnModel) {
        this.lqnModel = lqnModel;
    }

    public void parseFile(String filePath) throws FileNotFoundException {

        if (!FileHandler.fileExists(filePath)) {
            throw new java.io.FileNotFoundException("Filename: " + filePath + " doesn't exist");
        }
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);

            SAXParser saxParser = spf.newSAXParser();

            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.parse(filePath);
        }
        catch (SAXException se) {
            log.debug("[SAX Exception]: " + se.getMessage());
            se.printStackTrace();
        }
        catch (ParserConfigurationException pce) {
            log.debug("[PCE Exception]: " + pce.getMessage());
            pce.printStackTrace();
        }
        catch (IOException ie) {
            log.debug("[IO Exception]: " + ie.getMessage());
            ie.printStackTrace();
        }
        catch (Exception e) {
            log.debug("[Exception]: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void printProcessors() {
        int size = lqnModel.processorsSize();
        for (int i = 0; i < size; i++) {
            log.info(lqnModel.processorAtIndex(i).toString());
        }
    }

    public String getLQXCData(boolean addCData) {
        StringBuilder cData = new StringBuilder();
        if (addCData) {
            cData.append(getVariableInitializationCData());
            cData.append(lqnModel.buildProcessorsCDataString() + "\n\n");
            cData.append(lqnModel.buildTasksCDataString() + "\n\n");
            cData.append(lqnModel.buildBelowCDataString() + "\n\n");
            cData.append(AbstractLqnParser.getLQNSolveCData());
            cData.append(AbstractLqnParser.getTasksBStrengthCData());
            cData.append(AbstractLqnParser.getProcessorsBStrengthCData());
            cData.append(AbstractLqnParser.getBStrengthPrintAllCData());
        }
        return cData.toString();
    }
}
