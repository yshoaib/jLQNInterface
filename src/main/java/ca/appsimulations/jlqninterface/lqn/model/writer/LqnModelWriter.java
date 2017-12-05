package ca.appsimulations.jlqninterface.lqn.model.writer;

import ca.appsimulations.jlqninterface.lqn.entities.ActivityPhases;
import ca.appsimulations.jlqninterface.lqn.entities.Entry;
import ca.appsimulations.jlqninterface.lqn.entities.EntryPhaseActivities;
import ca.appsimulations.jlqninterface.lqn.entities.Task;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
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
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlAttributes.COMMENT;
import static ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlAttributes.CONVERGE_VALUE;
import static ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlAttributes.NAME;
import static ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlElements.*;

@Slf4j
public class LqnModelWriter {
    private static String XML_NS_URL = "http://www.w3.org/2001/XMLSchema-instance";

    public void WriteFile(LqnModel lqnModel, String output) throws FileNotFoundException {

        //        if (!FileHandler.doesFileExist(output)) {
        //            throw new java.io.FileNotFoundException("Filename: " + output + " doesn't exist");
        //        }

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element lqnModelRoot = doc.createElementNS(XML_NS_URL, LQN_MODEL.value());
            doc.appendChild(lqnModelRoot);

            Element solverParamsElem = doc.createElement(SOLVER_PARAMS.value());
            solverParamsElem.setAttribute(COMMENT.value(), "test");
            solverParamsElem.setAttribute(CONVERGE_VALUE.value(), "test2");
            lqnModelRoot.appendChild(solverParamsElem);


            lqnModel.processors().forEach(processor -> {
                Element processorElem = doc.createElement(PROCESSOR.value());
                processorElem.setAttribute(NAME.value(), processor.getName());


                appendTasks(doc, processorElem, processor.getTasks());

                lqnModelRoot.appendChild(processorElem);
            });



            doWrite(doc);
        }
        catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void appendTasks(Document doc, Element processorElem, ArrayList<Task> tasks) {
        tasks.forEach(task -> {
            Element taskElem = doc.createElement(TASK.value());
            taskElem.setAttribute(NAME.value(), task.getName());

            appendEntries(doc, taskElem, task.getEntries());
            processorElem.appendChild(taskElem);
        });
    }

    private void appendEntries(Document doc, Element taskElem, ArrayList<Entry> entries) {
        entries.forEach(entry -> {
            Element entryElem = doc.createElement(ENTRY.value());
            entryElem.setAttribute(NAME.value(), entry.getName());
            appendEntryPhaseActivites(doc, entryElem, entry.getEntryPhaseActivities());
            taskElem.appendChild(entryElem);
        });
    }

    private void appendEntryPhaseActivites(Document doc, Element entryElem, EntryPhaseActivities entryPhaseActivities) {
        Element entryPhaseElem = doc.createElement(ENTRY_PHASE_ACTIVITIES.value());
        appendActivities(doc, entryPhaseElem, entryPhaseActivities.getActivityAtPhase(1));
        entryElem.appendChild(entryPhaseElem);
    }

    private void appendActivities(Document doc, Element entryPhaseElem, ActivityPhases activityAtPhase) {
        Element activityElem = doc.createElement(ACTIVITY.value());
        activityElem.setAttribute(NAME.value(), activityAtPhase.getName());
        entryPhaseElem.appendChild(activityElem);
    }

    private void doWrite(Document doc) throws TransformerException {
        // output DOM XML to console
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult console = new StreamResult(System.out);
        transformer.transform(source, console);
        log.info("XML DOM Created Successfully..");
    }

}
