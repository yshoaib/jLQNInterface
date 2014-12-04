package ca.appsimulations.jlqninterface.lqns.modelhandling;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ca.appsimulations.jlqninterface.core.Model;
import ca.appsimulations.jlqninterface.lqns.entities.ActivityDef;
import ca.appsimulations.jlqninterface.lqns.entities.ActivityPhases;
import ca.appsimulations.jlqninterface.lqns.entities.EntryAcType;
import ca.appsimulations.jlqninterface.lqns.entities.LQNConstants;
import ca.appsimulations.jlqninterface.lqns.entities.LQNDefaults;
import ca.appsimulations.jlqninterface.lqns.entities.ProcessorSchedulingType;
import ca.appsimulations.jlqninterface.lqns.entities.SynchCall;
import ca.appsimulations.jlqninterface.lqns.entities.TaskSchedulingType;

/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */
public class LQNXmlModelInputParser extends LQNParser {

	public LQNXmlModelInputParser(Model workspace) {
		super(workspace);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		LQNXMLElements et = LQNXMLElements.getValue(qName);

		if (et == null) {
			return;
		}

		String attrName = attributes.getValue(LQNXMLAttributes.NAME.toString());
		String attrScheduling = attributes.getValue(LQNXMLAttributes.SCHEDULING.toString());
		String attrMultiplicity = attributes.getValue(LQNXMLAttributes.MULTIPLICITY.toString());
		String attrReplication = attributes.getValue(LQNXMLAttributes.REPLICATION.toString());

		switch (et) {
			case PROCESSOR:
				// name
				curProcessor = workspace.getProcessorByName(attrName, true);

				// scheduling
				ProcessorSchedulingType psType = ProcessorSchedulingType.getValue(attrScheduling);
				curProcessor.setScheduling(psType);

				// multiplicity
				if (psType == ProcessorSchedulingType.INF && (attrMultiplicity == null)) {
					curProcessor.setMultiplicity(LQNConstants.INFINITY.getConstantValue());
				} else if (attrMultiplicity != null) {
					curProcessor.setMultiplicity(Integer.parseInt(attrMultiplicity));
				} else if ((attrMultiplicity == null)) {
					curProcessor.setMultiplicity(LQNDefaults.PROCESSOR_MULTIPLICITY.getValue());
				}

				// replication
				if (attrReplication != null) {
					curProcessor.setReplication(Integer.parseInt(attrReplication));
				} else if ((attrReplication == null)) {
					curProcessor.setReplication(LQNDefaults.PROCESSOR_REPLICATION.getValue());
				}
				break;
			case RESULT_PROCESSOR:
/*
				// utilization
				String attrUtilization = attributes.getValue(LQNXMLAttributes.UTILIZATION.toString());
				float utilization = Float.parseFloat(attrUtilization);
				curProcessor.getResult().setUtilization(utilization);
				*/
				break;
				
			case TASK:
				// name
				curTask = workspace.getTaskByName(attrName, curProcessor, true);

				// scheduling
				TaskSchedulingType schedulingType = TaskSchedulingType.getValue(attrScheduling);
				curTask.setScheduling(schedulingType);

				// multiplicity
				if (schedulingType == TaskSchedulingType.INF && (attrMultiplicity == null)) {
					curTask.setMultiplicity(LQNConstants.INFINITY.getConstantValue());
				} else if (attrMultiplicity != null) {
					int multiplicity = Integer.parseInt(attrMultiplicity);
					curTask.setMultiplicity(multiplicity);
					if (curProcessor.getScheduling() == ProcessorSchedulingType.INF) {
						curProcessor.setMultiplicity(multiplicity);
					}
				} else if ((attrMultiplicity == null)) {
					curTask.setMultiplicity(LQNDefaults.TASK_MULTIPLICITY.getValue());
				}

				// replication
				if (attrReplication != null) {
					curTask.setReplication(Integer.parseInt(attrReplication));
				} else if ((attrReplication == null)) {
					curTask.setReplication(LQNDefaults.TASK_REPLICATION.getValue());
				}

				break;
			case RESULT_TASK:
				/*
				attrUtilization = attributes.getValue(LQNXMLAttributes.UTILIZATION.toString());
				String attrThroughput = attributes.getValue(LQNXMLAttributes.THROUGHPUT.toString());
				String attrPh1Utilization = attributes.getValue(LQNXMLAttributes.PHASE1_UTILIZATION.toString());
				String attrProcUtilization = attributes.getValue(LQNXMLAttributes.PROC_UTILIZATION.toString());

				// throughput, utilization
				float throughput = Float.parseFloat(attrThroughput);
				utilization = Float.parseFloat(attrUtilization);

				// ph1 utilization, proc utilization
				float ph1Utilization = 0;
				if (attrPh1Utilization != null) {
					ph1Utilization = Float.parseFloat(attrPh1Utilization);
				}
				float procUtilization = Float.parseFloat(attrProcUtilization);

				curTask.getResult().setThroughput(throughput);
				curTask.getResult().setUtilization(utilization);
				curTask.getResult().setPhase1_utilization(ph1Utilization);
				curTask.getResult().setProc_utilization(procUtilization);
				*/
				break;
				
			case ENTRY:
				String attrType = attributes.getValue(LQNXMLAttributes.TYPE.toString());

				// name,type
				curEntry = workspace.getEntryByName(attrName, curTask, true);
				curEntry.setEntryType(EntryAcType.getValue(attrType));
				break;
			case RESULT_ENTRY:
				/*
				attrUtilization = attributes.getValue(LQNXMLAttributes.UTILIZATION.toString());
				attrThroughput = attributes.getValue(LQNXMLAttributes.THROUGHPUT.toString());
				String attrSquaredCoeff = attributes.getValue(LQNXMLAttributes.SQUARED_COEFF_VARIATION.toString());
				attrProcUtilization = attributes.getValue(LQNXMLAttributes.PROC_UTILIZATION.toString());

				// throughput, utilization, squaredCoeffVariation,
				// proc-utilization
				throughput = Float.parseFloat(attrThroughput);
				utilization = Float.parseFloat(attrUtilization);
				float squaredCoeffVariation = Float.parseFloat(attrSquaredCoeff);
				procUtilization = Float.parseFloat(attrProcUtilization);

				// phaseX-serviceTime
				String attrPh1ServiceTime = attributes.getValue(LQNXMLAttributes.PHASE1_SERVICE_TIME.toString());
				if (attrPh1ServiceTime != null) {
					float ph1ServiceTime = Float.parseFloat(attrPh1ServiceTime);
					curEntry.getResult().setPhase1_service_time(ph1ServiceTime);
				}
				String attrPh2ServiceTime = attributes.getValue(LQNXMLAttributes.PHASE2_SERVICE_TIME.toString());
				if (attrPh2ServiceTime != null) {
					float ph2ServiceTime = Float.parseFloat(attrPh2ServiceTime);
					curEntry.getResult().setPhase1_service_time(ph2ServiceTime);
				}
				String attrPh3ServiceTime = attributes.getValue(LQNXMLAttributes.PHASE3_SERVICE_TIME.toString());
				if (attrPh3ServiceTime != null) {
					float ph3ServiceTime = Float.parseFloat(attrPh3ServiceTime);
					curEntry.getResult().setPhase1_service_time(ph3ServiceTime);
				}

				curEntry.getResult().setThroughput(throughput);
				curEntry.getResult().setUtilization(utilization);
				curEntry.getResult().setSquared_coeff_variation(squaredCoeffVariation);
				curEntry.getResult().setProc_utilization(procUtilization);
				*/
				
				break;
			
			case ENTRY_PHASE_ACTIVITIES:
				curEntry.generateEntryPhaseActivities();
				isEntryPhaseActivities = true;
				break;
			case TASK_ACTIVITIES:
				// TODO need to find task activity by name
				curTaskActivities = curTask.generateTaskActivities();
				isTaskActivities = true;
				break;
			case ACTIVITY:
				String attrBoundEntry = attributes.getValue(LQNXMLAttributes.BOUND_TO_ENTRY.toString());
				String attrPhase = attributes.getValue(LQNXMLAttributes.PHASE.toString());
				String attrHostDemand = attributes.getValue(LQNXMLAttributes.HOST_DEMAND_MEAN.toString());

				if (isTaskActivities) {
					// bound-to-entry
					// note: bound-to-entry may be null
					if (attrBoundEntry != null) {
						curEntry = workspace.getEntryByName(attrBoundEntry);
					}

					// name
					curActivity = workspace.getActivityDefByName(attrName, curTask, curTaskActivities, curEntry, true);
				} else if (curEntry.getEntryType() == EntryAcType.PH1PH2) {
					int phase;
					if (attrPhase != null) {
						phase = Integer.parseInt(attrPhase);
					} else {
						SAXException se = new SAXException("[SAXException] attribute phase is null");
						se.printStackTrace();
						throw se;
					}

					// name, phase
					curActivity = workspace.getActivityPHByName(attrName, curEntry, phase, true);
				}
				// System.out.println("case activity: " + attrName +
				// "\t\t\t isTaskActivities: " + isTaskActivities +
				// "\t\t taskName: " + curTask + "\t\t entryName: " + curEntry);

				// host-demand-mean
				if (attrHostDemand != null) {
					float hostDemand = Float.parseFloat(attrHostDemand);
					curActivity.setHost_demand_mean(hostDemand);
				}

				break;
			case RESULT_ACTIVITY:
				/*
				String attrProcWaiting = attributes.getValue(LQNXMLAttributes.PROC_WAITING.toString());
				String attrServiceTime = attributes.getValue(LQNXMLAttributes.SERVICE_TIME.toString());
				String attrServiceTimeVar = attributes.getValue(LQNXMLAttributes.SERVICE_TIME_VARIANCE.toString());
				attrUtilization = attributes.getValue(LQNXMLAttributes.UTILIZATION.toString());

				// procwaiting, serviceTime, serviceTimVar, utilization
				float procWaiting = Float.parseFloat(attrProcWaiting);
				float serviceTime = Float.parseFloat(attrServiceTime);
				float serviceTimeVar = Float.parseFloat(attrServiceTimeVar);
				utilization = Float.parseFloat(attrUtilization);

				curActivity.getResult().setProc_waiting(procWaiting);
				curActivity.getResult().setService_time(serviceTime);
				curActivity.getResult().setService_time_variance(serviceTimeVar);
				curActivity.getResult().setUtilization(utilization);
				*/
				break;
				
			case SYNCH_CALL:
				// dest, callsmean, fanin,fanout
				String attrDest = attributes.getValue(LQNXMLAttributes.DEST.toString());
				String attrCallsMean = attributes.getValue(LQNXMLAttributes.CALLS_MEAN.toString());
				String attrFanin = attributes.getValue(LQNXMLAttributes.FANIN.toString());
				String attrFanout = attributes.getValue(LQNXMLAttributes.FANOUT.toString());
				float callsMean = Float.parseFloat(attrCallsMean);

				ActivityPhases ap = null;
				ActivityDef ad = null;
				SynchCall s = null;

				if (isEntryPhaseActivities) {
					ap = (ActivityPhases) curActivity;
					s = ap.getSynchCallByStrDestEntry(attrDest);
					if (s == null) {
						s = new SynchCall(this.workspace, attrDest, callsMean);
						ap.addSynchCall(s);
					}
				} else if (isTaskActivities) {
					ad = (ActivityDef) curActivity;
					s = ad.getSynchCallByStrDestEntry(attrDest);
					if (s == null) {
						s = new SynchCall(this.workspace, attrDest, callsMean);
						ad.addSynchCall(s);
					}
				}

				if (attrFanin != null) {
					s.setFanin(Integer.parseInt(attrFanin));
				}

				if (attrFanout != null) {
					s.setFanout(Integer.parseInt(attrFanout));
				}

				/*
				 * String attrDest =
				 * attributes.getValue(LQNXMLAttributes.DEST.toString()); String
				 * attrCallsMean =
				 * attributes.getValue(LQNXMLAttributes.CALLS_MEAN.toString());
				 * String attrFanin =
				 * attributes.getValue(LQNXMLAttributes.FANIN.toString());
				 * String attrFanout =
				 * attributes.getValue(LQNXMLAttributes.FANOUT.toString());
				 * float callsMean = Float.parseFloat(attrCallsMean);
				 * 
				 * SynchCall s = new SynchCall(this.workspace, attrDest,
				 * callsMean); if (attrFanin != null) {
				 * s.setFanin(Integer.parseInt(attrFanin)); }
				 * 
				 * if (attrFanout != null) {
				 * s.setFanout(Integer.parseInt(attrFanout)); }
				 * 
				 * if (isEntryPhaseActivities) { ActivityPhases ap =
				 * (ActivityPhases) curActivity; ap.addSynchCall(s); } else if
				 * (isTaskActivities) { ActivityDef ad = (ActivityDef)
				 * curActivity; ad.addSynchCall(s); }
				 */

				// curEntry.addSyncCall(attrDest);

				/*
				 * PHASES:
				 * 
				 * <entry-phase-activities> <activity name="funcA1_1_1"
				 * phase="1" host-demand-mean="3"> <synch-call dest="funcB1"
				 * calls-mean="1"/> </activity> <activity name="funcA1_1_2"
				 * phase="2" host-demand-mean="2"/> <activity name="funcA1_1_3"
				 * phase="3" host-demand-mean="1"/> </entry-phase-activities>
				 */
				break;
			default:
				break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		LQNXMLElements et = LQNXMLElements.getValue(qName);

		if (et == null) {
			return;
		}

		switch (et) {
			case PROCESSOR:
				// workspace.addProcessor(curProcessor);
				curProcessor = null;
				break;
			case TASK:
				// curProcessor.addTask(curTask);
				curTask = null;
				break;
			case ENTRY:
				// curTask.addEntry(curEntry);
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
				/*
				 * if (curActivity != null) { curEntry.setActivity(curActivity);
				 * }
				 */
				break;
			default:
				break;
		}
	}
}
