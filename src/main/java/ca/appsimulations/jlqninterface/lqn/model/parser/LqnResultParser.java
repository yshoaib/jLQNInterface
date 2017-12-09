package ca.appsimulations.jlqninterface.lqn.model.parser;

import ca.appsimulations.jlqninterface.lqn.entities.ActivityDef;
import ca.appsimulations.jlqninterface.lqn.entities.ActivityPhases;
import ca.appsimulations.jlqninterface.lqn.entities.EntryAcType;
import ca.appsimulations.jlqninterface.lqn.entities.SynchCall;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlAttributes;
import ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlElements;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */
public class LqnResultParser extends AbstractLqnParser {

    public LqnResultParser(LqnModel lqnModel) {
        super(lqnModel);
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        LqnXmlElements et = LqnXmlElements.from(qName);

        if (et == null) {
            return;
        }

        String attrName = attributes.getValue(LqnXmlAttributes.NAME.toString());
        switch (et) {
            case RESULT_GENERAL:

                String attrValid = attributes.getValue(LqnXmlAttributes.VALID.toString());
                lqnModel.result().setResultValid(attrValid);
                break;

            case PROCESSOR:
                curProcessor = lqnModel.processorByName(attrName, true);
                break;
            case RESULT_PROCESSOR:

                // utilization
                String attrUtilization = attributes.getValue(LqnXmlAttributes.UTILIZATION.toString());
                double utilization = Double.parseDouble(attrUtilization);
                curProcessor.getResult().setUtilization(utilization);
                break;
            case TASK:
                curTask = lqnModel.taskByName(attrName, curProcessor, true);
                break;
            case RESULT_TASK:
                attrUtilization = attributes.getValue(LqnXmlAttributes.UTILIZATION.toString());
                String attrThroughput = attributes.getValue(LqnXmlAttributes.THROUGHPUT.toString());
                String attrPh1Utilization = attributes.getValue(LqnXmlAttributes.PHASE1_UTILIZATION.toString());
                String attrProcUtilization = attributes.getValue(LqnXmlAttributes.PROC_UTILIZATION.toString());

                // throughput, utilization
                double throughput = Double.parseDouble(attrThroughput);
                utilization = Double.parseDouble(attrUtilization);

                // ph1 utilization, proc utilization
                double ph1Utilization = 0;
                if (attrPh1Utilization != null) {
                    ph1Utilization = Double.parseDouble(attrPh1Utilization);
                }
                double procUtilization = Double.parseDouble(attrProcUtilization);

                curTask.getResult().setThroughput(throughput);
                curTask.getResult().setUtilization(utilization);
                curTask.getResult().setPhase1_utilization(ph1Utilization);
                curTask.getResult().setProc_utilization(procUtilization);
                break;
            case ENTRY:
                String attrType = attributes.getValue(LqnXmlAttributes.TYPE.toString());
                curEntry = lqnModel.entryByName(attrName, curTask, true);
                break;
            case RESULT_ENTRY:
                attrUtilization = attributes.getValue(LqnXmlAttributes.UTILIZATION.toString());
                attrThroughput = attributes.getValue(LqnXmlAttributes.THROUGHPUT.toString());
                String attrSquaredCoeff = attributes.getValue(LqnXmlAttributes.SQUARED_COEFF_VARIATION.toString());
                attrProcUtilization = attributes.getValue(LqnXmlAttributes.PROC_UTILIZATION.toString());

                // throughput, utilization, squaredCoeffVariation,
                // proc-utilization
                throughput = Double.parseDouble(attrThroughput);
                utilization = Double.parseDouble(attrUtilization);
                double squaredCoeffVariation = Double.parseDouble(attrSquaredCoeff);
                procUtilization = Double.parseDouble(attrProcUtilization);

                // phaseX-serviceTime
                String attrPh1ServiceTime = attributes.getValue(LqnXmlAttributes.PHASE1_SERVICE_TIME.toString());
                if (attrPh1ServiceTime != null) {
                    double ph1ServiceTime = Double.parseDouble(attrPh1ServiceTime);
                    curEntry.getResult().setPhase1_service_time(ph1ServiceTime);
                }
                String attrPh2ServiceTime = attributes.getValue(LqnXmlAttributes.PHASE2_SERVICE_TIME.toString());
                if (attrPh2ServiceTime != null) {
                    double ph2ServiceTime = Double.parseDouble(attrPh2ServiceTime);
                    curEntry.getResult().setPhase1_service_time(ph2ServiceTime);
                }
                String attrPh3ServiceTime = attributes.getValue(LqnXmlAttributes.PHASE3_SERVICE_TIME.toString());
                if (attrPh3ServiceTime != null) {
                    double ph3ServiceTime = Double.parseDouble(attrPh3ServiceTime);
                    curEntry.getResult().setPhase1_service_time(ph3ServiceTime);
                }

                curEntry.getResult().setThroughput(throughput);
                curEntry.getResult().setUtilization(utilization);
                curEntry.getResult().setSquared_coeff_variation(squaredCoeffVariation);
                curEntry.getResult().setProc_utilization(procUtilization);

                break;
            case ENTRY_PHASE_ACTIVITIES:
                isEntryPhaseActivities = true;
                break;
            case TASK_ACTIVITIES:
                // TODO need to find task activity by name
                isTaskActivities = true;
                break;
            case ACTIVITY:
                String attrBoundEntry = attributes.getValue(LqnXmlAttributes.BOUND_TO_ENTRY.toString());
                String attrPhase = attributes.getValue(LqnXmlAttributes.PHASE.toString());
                String attrHostDemand = attributes.getValue(LqnXmlAttributes.HOST_DEMAND_MEAN.toString());

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
                break;
            case RESULT_ACTIVITY:
                String attrProcWaiting = attributes.getValue(LqnXmlAttributes.PROC_WAITING.toString());
                String attrServiceTime = attributes.getValue(LqnXmlAttributes.SERVICE_TIME.toString());
                String attrServiceTimeVar = attributes.getValue(LqnXmlAttributes.SERVICE_TIME_VARIANCE.toString());
                attrUtilization = attributes.getValue(LqnXmlAttributes.UTILIZATION.toString());

                // procwaiting, serviceTime, serviceTimVar, utilization
                double procWaiting = Double.parseDouble(attrProcWaiting);
                double serviceTime = Double.parseDouble(attrServiceTime);
                double serviceTimeVar = Double.parseDouble(attrServiceTimeVar);
                utilization = Double.parseDouble(attrUtilization);

                curActivity.getResult().setProc_waiting(procWaiting);
                curActivity.getResult().setService_time(serviceTime);
                curActivity.getResult().setService_time_variance(serviceTimeVar);
                curActivity.getResult().setUtilization(utilization);

                break;
            case SYNCH_CALL:
                // dest, callsmean, fanin,fanout
                String attrDest = attributes.getValue(LqnXmlAttributes.DEST.toString());
                String attrCallsMean = attributes.getValue(LqnXmlAttributes.CALLS_MEAN.toString());
                String attrFanin = attributes.getValue(LqnXmlAttributes.FANIN.toString());
                String attrFanout = attributes.getValue(LqnXmlAttributes.FANOUT.toString());
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
                break;
            default:
                break;
        }
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
