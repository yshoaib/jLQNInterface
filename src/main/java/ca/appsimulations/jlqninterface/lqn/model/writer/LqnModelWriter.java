package ca.appsimulations.jlqninterface.lqn.model.writer;

import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.lqn.model.LqnXmlDetails;
import ca.appsimulations.jlqninterface.lqn.model.SolverParams;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlAttributes.*;
import static ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlElements.*;

@Slf4j
public class LqnModelWriter {
    private static String PREFIX = "xsi";

    public static void write(LqnModel lqnModel, String outputPath) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element lqnModelRoot = appendLqnModel(doc, lqnModel.xmlDetails());
            appendSolverParams(doc, lqnModelRoot, lqnModel.solverParams());
            appendProcessors(doc, lqnModelRoot, lqnModel.processors());
            writeDocument(doc, outputPath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Element appendLqnModel(Document doc, LqnXmlDetails lqnXmlDetails) {
        Element lqnModelRoot = doc.createElement(LQN_MODEL.value());
        lqnModelRoot.setAttribute(XMLNS_XSI.value(), lqnXmlDetails.xmlnsXsi());
        lqnModelRoot.setAttribute(NAME.value(), lqnXmlDetails.name());
        lqnModelRoot.setAttribute(DESCRIPTION.value(), lqnXmlDetails.description());
        lqnModelRoot.setAttribute(SCHEMA_LOCATION.value(), lqnXmlDetails.schemaLocation());
        doc.appendChild(lqnModelRoot);
        return lqnModelRoot;
    }

    private static void appendSolverParams(Document doc,
                                           Element lqnModelRoot,
                                           SolverParams solverParams) {
        Element solverParamsElem = doc.createElement(SOLVER_PARAMS.value());
        solverParamsElem.setAttribute(COMMENT.value(), solverParams.comment());
        solverParamsElem.setAttribute(CONVERGENCE_VALUE.value(), Double.toString(solverParams.convergence()));
        solverParamsElem.setAttribute(ITERATION_LIMIT.value(), Integer.toString(solverParams.iterationLimit()));
        solverParamsElem.setAttribute(UNDER_RELAX_COEFF.value(), Double.toString(solverParams.underRelaxCoeff()));
        solverParamsElem.setAttribute(PRINT_INTERVAL.value(), Integer.toString(solverParams.printInterval()));
        lqnModelRoot.appendChild(solverParamsElem);
    }

    private static void appendProcessors(Document doc, Element lqnModelRoot, ArrayList<Processor> processors) {
        processors.forEach(processor -> {
            Element processorElem = doc.createElement(PROCESSOR.value());
            processorElem.setAttribute(NAME.value(), processor.getName());
            processorElem.setAttribute(SCHEDULING.value(), processor.getScheduling().value());
            if (processor.getScheduling().equals(ProcessorSchedulingType.PS)) {
                processorElem.setAttribute(QUANTUM.value(), Double.toString(processor.getQuantum()));
            }
            appendTasks(doc, processorElem, processor.getTasks());
            lqnModelRoot.appendChild(processorElem);
        });
    }

    private static void appendTasks(Document doc, Element processorElem, ArrayList<Task> tasks) {
        tasks.forEach(task -> {
            Element taskElem = doc.createElement(TASK.value());
            taskElem.setAttribute(NAME.value(), task.getName());
            taskElem.setAttribute(MULTIPLICITY.value(), task.getMutiplicityString());
            taskElem.setAttribute(SCHEDULING.value(), task.getScheduling().value());
            appendEntries(doc, taskElem, task.getEntries());
            processorElem.appendChild(taskElem);
        });
    }

    private static void appendEntries(Document doc, Element taskElem, ArrayList<Entry> entries) {
        entries.forEach(entry -> {
            Element entryElem = doc.createElement(ENTRY.value());
            entryElem.setAttribute(NAME.value(), entry.getName());
            entryElem.setAttribute(TYPE.value(), entry.getEntryType().value());
            appendEntryPhaseActivities(doc, entryElem, entry.getEntryPhaseActivities());
            taskElem.appendChild(entryElem);
        });
    }

    private static void appendEntryPhaseActivities(Document doc,
                                                   Element entryElem,
                                                   EntryPhaseActivities entryPhaseActivities) {
        Element entryPhaseElem = doc.createElement(ENTRY_PHASE_ACTIVITIES.value());
        appendActivities(doc, entryPhaseElem, entryPhaseActivities.getActivityAtPhase(1));
        entryElem.appendChild(entryPhaseElem);
    }

    private static void appendActivities(Document doc, Element entryPhaseElem, ActivityPhases activity) {
        Element activityElem = doc.createElement(ACTIVITY.value());
        activityElem.setAttribute(NAME.value(), activity.getName());
        activityElem.setAttribute(PHASE.value(), Integer.toString(activity.getPhase()));
        activityElem.setAttribute(HOST_DEMAND_MEAN.value(), Double.toString(activity.getHost_demand_mean()));
        appendSynchCalls(doc, activityElem, activity);
        entryPhaseElem.appendChild(activityElem);
    }

    private static void appendSynchCalls(Document doc, Element activityElem, ActivityPhases activity) {
        activity.getSynchCalls().forEach(synchCall -> {
            Element synchCallElem = doc.createElement(SYNCH_CALL.value());
            synchCallElem.setAttribute(DEST.value(), synchCall.getStrDestEntry());
            synchCallElem.setAttribute(CALLS_MEAN.value(), Double.toString(synchCall.getCallsMean()));
            activityElem.appendChild(synchCallElem);
        });
    }

    private static void writeDocument(Document doc, String outputPath)
            throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);

        File file = new File(outputPath);
        try {
            file.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
            file = null;
        }

        FileOutputStream fileOutputStream;
        try {
            if (file != null) {
                fileOutputStream = new FileOutputStream(file);
                log.info("writing lqn model to file: " + file.getAbsolutePath());
                StreamResult console = new StreamResult(fileOutputStream);
                transformer.transform(source, console);
                log.info("document created");
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
