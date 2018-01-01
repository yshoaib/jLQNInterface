package ca.appsimulations.jlqninterface.lqn.model.parser;

import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.lqn.model.LqnXmlDetails;
import ca.appsimulations.jlqninterface.lqn.model.SolverParams;
import ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlAttributes;
import ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlElements;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static ca.appsimulations.jlqninterface.lqn.entities.LqnDefaults.*;
import static ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlAttributes.*;

/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */
public class LqnInputParser extends AbstractLqnParser {
    private static String XML_NS_URL = "http://www.w3.org/2001/XMLSchema-instance";

    private final boolean parseProcessors;

    public LqnInputParser(LqnModel lqnModel, boolean parseProcessors) {
        super(lqnModel);
        this.parseProcessors = parseProcessors;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        LqnXmlElements et = LqnXmlElements.from(qName);

        if (et == null) {
            return;
        }

        String attrName = getAttributeValue(attributes, NAME);
        String attrScheduling = getAttributeValue(attributes, SCHEDULING);
        String attrMultiplicity = getAttributeValue(attributes, MULTIPLICITY);
        String attrReplication = getAttributeValue(attributes, REPLICATION);

        switch (et) {
            case LQN_MODEL:
                lqnModel.xmlDetails(LqnXmlDetails
                                            .builder()
                                            .name(attrName)
                                            .xmlnsXsi(XML_NS_URL)
                                            .description(getAttributeValue(attributes, DESCRIPTION))
                                            .schemaLocation(getAttributeValue(attributes, SCHEMA_LOCATION))
                                            .build());
                break;
            case SOLVER_PARAMS:
                lqnModel.solverParams(SolverParams
                                              .builder()
                                              .comment(getAttributeValue(attributes, COMMENT))
                                              .convergence(Double.parseDouble(getAttributeValue(attributes,
                                                                                                CONVERGENCE_VALUE)))
                                              .iterationLimit(Integer.parseInt(getAttributeValue(attributes,
                                                                                                 ITERATION_LIMIT)))
                                              .underRelaxCoeff(Double.parseDouble(getAttributeValue(attributes,
                                                                                                    UNDER_RELAX_COEFF)))
                                              .printInterval(Integer.parseInt(getAttributeValue(attributes,
                                                                                                PRINT_INTERVAL)))

                                              .build());
                break;
            case PROCESSOR:
                if (parseProcessors) {
                    // name
                    curProcessor = lqnModel.processorByName(attrName, true);

                    // scheduling
                    ProcessorSchedulingType psType = ProcessorSchedulingType.from(attrScheduling);
                    curProcessor.setScheduling(psType);

                    // multiplicity
                    if (psType == ProcessorSchedulingType.INF && (attrMultiplicity == null)) {
                        curProcessor.setMultiplicity(LqnConstants.INFINITY.getConstantValue());
                    }
                    else if (attrMultiplicity != null) {
                        curProcessor.setMultiplicity(Integer.parseInt(attrMultiplicity));
                    }
                    else if ((attrMultiplicity == null)) {
                        curProcessor.setMultiplicity(PROCESSOR_MULTIPLICITY.getValue());
                    }

                    // replication
                    if (attrReplication != null) {
                        curProcessor.setReplication(Integer.parseInt(attrReplication));
                    }
                    else if ((attrReplication == null)) {
                        curProcessor.setReplication(PROCESSOR_REPLICATION.getValue());
                    }

                    String attrQuantum = getAttributeValue(attributes, QUANTUM);
                    if (psType == ProcessorSchedulingType.PS && attrQuantum != null) {
                        curProcessor.setQuantum(Double.parseDouble(attrQuantum));
                    }
                }
                else {
                    curProcessor = new Processor(lqnModel);
                }
                break;
            case RESULT_PROCESSOR:
                break;

            case TASK:
                // name
                curTask = lqnModel.taskByName(attrName, curProcessor, true);

                // scheduling
                TaskSchedulingType schedulingType = TaskSchedulingType.getValue(attrScheduling);
                curTask.setScheduling(schedulingType);

                // multiplicity
                if (schedulingType == TaskSchedulingType.INF && (attrMultiplicity == null)) {
                    curTask.setMultiplicity(LqnConstants.INFINITY.getConstantValue());
                }
                else if (attrMultiplicity != null) {
                    int multiplicity = Integer.parseInt(attrMultiplicity);
                    curTask.setMultiplicity(multiplicity);
                    if (curProcessor != null && curProcessor.getScheduling() == ProcessorSchedulingType.INF) {
                        curProcessor.setMultiplicity(multiplicity);
                    }
                }
                else if ((attrMultiplicity == null)) {
                    curTask.setMultiplicity(LqnDefaults.TASK_MULTIPLICITY.getValue());
                }

                // replication
                if (attrReplication != null) {
                    curTask.setReplication(Integer.parseInt(attrReplication));
                }
                else if ((attrReplication == null)) {
                    curTask.setReplication(TASK_REPLICATION.getValue());
                }

                break;
            case RESULT_TASK:
                break;

            case ENTRY:
                String attrType = getAttributeValue(attributes, TYPE);

                // name,type
                curEntry = lqnModel.entryByName(attrName, curTask, true);
                curEntry.setEntryType(EntryAcType.getValue(attrType));
                break;
            case RESULT_ENTRY:
                break;

            case ENTRY_PHASE_ACTIVITIES:
                isEntryPhaseActivities = true;
                break;
            case TASK_ACTIVITIES:
                // TODO need to find task activity by name
                curTaskActivity = curTask.buildAndAddTaskActivity();
                isTaskActivities = true;
                break;
            case ACTIVITY:
                String attrBoundEntry = getAttributeValue(attributes, BOUND_TO_ENTRY);
                String attrPhase = getAttributeValue(attributes, PHASE);
                String attrHostDemand = getAttributeValue(attributes, HOST_DEMAND_MEAN);

                if (isTaskActivities) {
                    // bound-to-entry
                    // note: bound-to-entry may be null
                    if (attrBoundEntry != null) {
                        curEntry = lqnModel.entryByName(attrBoundEntry);
                    }

                    // name
                    curActivity = lqnModel.activityDefByName(attrName, curTask, curTaskActivity, curEntry, true);
                }
                else if (curEntry.getEntryType() == EntryAcType.PH1PH2) {
                    int phase;
                    if (attrPhase != null) {
                        phase = Integer.parseInt(attrPhase);
                    }
                    else {
                        SAXException se = new SAXException("[SAXException] attribute phase is null");
                        se.printStackTrace();
                        throw se;
                    }

                    // name, phase
                    curActivity = lqnModel.activityPHByName(attrName, curEntry, phase, true);
                }

                // host-demand-mean
                if (attrHostDemand != null) {
                    double hostDemand = Double.parseDouble(attrHostDemand);
                    curActivity.setHost_demand_mean(hostDemand);
                }

                break;
            case RESULT_ACTIVITY:
                break;

            case SYNCH_CALL:
                // dest, callsmean, fanin,fanout
                String attrDest = getAttributeValue(attributes, DEST);
                String attrCallsMean = getAttributeValue(attributes, CALLS_MEAN);
                String attrFanin = getAttributeValue(attributes, FANIN);
                String attrFanout = getAttributeValue(attributes, FANOUT);
                double callsMean = Double.parseDouble(attrCallsMean);

                ActivityPhases ap = null;
                ActivityDef ad = null;
                SynchCall s = null;

                if (isEntryPhaseActivities) {
                    ap = (ActivityPhases) curActivity;
                    s = ap.getSynchCallByStrDestEntry(attrDest);
                    if (s == null) {
                        s = new SynchCall(this.lqnModel, attrDest, callsMean);
                        ap.addSynchCall(s);
                    }
                }
                else if (isTaskActivities) {
                    ad = (ActivityDef) curActivity;
                    s = ad.getSynchCallByStrDestEntry(attrDest);
                    if (s == null) {
                        s = new SynchCall(this.lqnModel, attrDest, callsMean);
                        ad.addSynchCall(s);
                    }
                }

                if (attrFanin != null) {
                    s.setFanin(Integer.parseInt(attrFanin));
                }

                if (attrFanout != null) {
                    s.setFanout(Integer.parseInt(attrFanout));
                }
                break;
            default:
                break;
        }
    }

    private String getAttributeValue(Attributes attributes, LqnXmlAttributes name) {
        return attributes.getValue(name.value());
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        LqnXmlElements et = LqnXmlElements.from(qName);

        if (et == null) {
            return;
        }

        switch (et) {
            case PROCESSOR:
                curProcessor = null;
                break;
            case TASK:
                curTask = null;
                break;
            case ENTRY:
                curEntry = null;
                break;
            case ENTRY_PHASE_ACTIVITIES:
                isEntryPhaseActivities = false;
                break;
            case TASK_ACTIVITIES:
                isTaskActivities = false;
                curEntry = null;
                break;
            case ACTIVITY:
                curActivity = null;
                break;
            default:
                break;
        }
    }
}
