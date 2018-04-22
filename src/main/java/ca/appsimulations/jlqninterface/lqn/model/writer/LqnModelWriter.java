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
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlAttributes.*;
import static ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlElements.*;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class LqnModelWriter {
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
            if (processor.getMultiplicity() > 1 &&
                processor.getScheduling().equals(ProcessorSchedulingType.INF) == false) {
                processorElem.setAttribute(MULTIPLICITY.value(), processor.getMutiplicityString());
            }
            if (processor.getScheduling().equals(ProcessorSchedulingType.PS)) {
                processorElem.setAttribute(QUANTUM.value(), Double.toString(processor.getQuantum()));
            }
            if (processor.getReplication() > 1 &&
                processor.getScheduling().equals(ProcessorSchedulingType.INF) == false) {
                processorElem.setAttribute(REPLICATION.value(), Integer.toString(processor.getReplication()));
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
            if (task.getReplication() > 1 && task.isRefTask() == false) {
                taskElem.setAttribute(REPLICATION.value(), Integer.toString(task.getReplication()));
            }
            appendEntries(doc, taskElem, task.getEntries(), task.isRefTask());
            appendFanIn(doc, taskElem, task.getFanInMap());
            appendFanOut(doc, taskElem, task.getFanOutMap());

            processorElem.appendChild(taskElem);
        });
    }

    private static void appendFanOut(Document doc, Element taskElem, Map<Task, Integer> fanOutMap) {
        Map<String, Integer> fanOuts =
                fanOutMap.entrySet().stream().collect(toMap(entry -> entry.getKey().getName(),
                                                            entry -> entry.getValue()));

        SortedSet<String> keys = new TreeSet<String>(fanOuts.keySet());

        keys.stream().forEach(key -> {
            Element fanOutElem = doc.createElement(FAN_OUT.value());
            fanOutElem.setAttribute(DEST.value(), key);
            fanOutElem.setAttribute(VALUE.value(), fanOuts.get(key).toString());
            taskElem.appendChild(fanOutElem);
        });
    }

    private static void appendFanIn(Document doc, Element taskElem, Map<Task, Integer> fanInMap) {

        Map<String, Integer> fanIns =
                fanInMap.entrySet().stream().collect(toMap(entry -> entry.getKey().getName(),
                                                           entry -> entry.getValue()));

        SortedSet<String> keys = new TreeSet<String>(fanIns.keySet());

        keys.stream().forEach(key -> {
            Element fanInElem = doc.createElement(FAN_IN.value());
            fanInElem.setAttribute(SOURCE.value(), key);
            fanInElem.setAttribute(VALUE.value(), fanIns.get(key).toString());
            taskElem.appendChild(fanInElem);
        });
    }

    private static void appendEntries(Document doc, Element taskElem, ArrayList<Entry> entries, boolean refTask) {
        entries.forEach(entry -> {
            Element entryElem = doc.createElement(ENTRY.value());
            entryElem.setAttribute(NAME.value(), entry.getName());
            entryElem.setAttribute(TYPE.value(), entry.getEntryType().value());
            appendEntryPhaseActivities(doc, entryElem, entry.getEntryPhaseActivities(), refTask);
            taskElem.appendChild(entryElem);
        });
    }

    private static void appendEntryPhaseActivities(Document doc,
                                                   Element entryElem,
                                                   EntryPhaseActivities entryPhaseActivities, boolean refTask) {
        Element entryPhaseElem = doc.createElement(ENTRY_PHASE_ACTIVITIES.value());
        appendActivities(doc, entryPhaseElem, entryPhaseActivities.getActivityAtPhase(1), refTask);
        entryElem.appendChild(entryPhaseElem);
    }

    private static void appendActivities(Document doc,
                                         Element entryPhaseElem,
                                         ActivityPhases activity,
                                         boolean refTask) {
        Element activityElem = doc.createElement(ACTIVITY.value());
        activityElem.setAttribute(NAME.value(), activity.getName());
        activityElem.setAttribute(PHASE.value(), Integer.toString(activity.getPhase()));
        activityElem.setAttribute(HOST_DEMAND_MEAN.value(), Double.toString(activity.getHost_demand_mean()));
        if (refTask == false && activity.getThinkTime() > 0) {
            throw new IllegalArgumentException("non ref task cannot have think time > 0");
        }
        else if (activity.getThinkTime() > 0) {
            activityElem.setAttribute(THINK_TIME.value(), Double.toString(activity.getThinkTime()));
        }
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
