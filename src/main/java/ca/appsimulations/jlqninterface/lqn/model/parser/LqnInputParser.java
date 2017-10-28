package ca.appsimulations.jlqninterface.lqn.model.parser;

import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlAttributes;
import ca.appsimulations.jlqninterface.lqn.model.handler.LqnXmlElements;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */
public class LqnInputParser extends LqnParser {

	private final boolean parseProcessors;
	public LqnInputParser(LqnModel lqnModel, boolean parseProcessors) {
		super(lqnModel);
		this.parseProcessors = parseProcessors;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		LqnXmlElements et = LqnXmlElements.getValue(qName);

		if (et == null) {
			return;
		}

		String attrName = attributes.getValue(LqnXmlAttributes.NAME.toString());
		String attrScheduling = attributes.getValue(LqnXmlAttributes.SCHEDULING.toString());
		String attrMultiplicity = attributes.getValue(LqnXmlAttributes.MULTIPLICITY.toString());
		String attrReplication = attributes.getValue(LqnXmlAttributes.REPLICATION.toString());

		switch (et) {
			case PROCESSOR:
				if(parseProcessors) {
					// name
					curProcessor = lqnModel.getProcessorByName(attrName, true);

					// scheduling
					ProcessorSchedulingType psType = ProcessorSchedulingType.getValue(attrScheduling);
					curProcessor.setScheduling(psType);

					// multiplicity
					if (psType == ProcessorSchedulingType.INF && (attrMultiplicity == null)) {
						curProcessor.setMultiplicity(LqnConstants.INFINITY.getConstantValue());
					} else if (attrMultiplicity != null) {
						curProcessor.setMultiplicity(Integer.parseInt(attrMultiplicity));
					} else if ((attrMultiplicity == null)) {
						curProcessor.setMultiplicity(LqnDefaults.PROCESSOR_MULTIPLICITY.getValue());
					}

					// replication
					if (attrReplication != null) {
						curProcessor.setReplication(Integer.parseInt(attrReplication));
					} else if ((attrReplication == null)) {
						curProcessor.setReplication(LqnDefaults.PROCESSOR_REPLICATION.getValue());
					}
				}else {
					curProcessor = new Processor(lqnModel);
				}
				break;
			case RESULT_PROCESSOR:
				break;
				
			case TASK:
				// name
				curTask = lqnModel.getTaskByName(attrName, curProcessor, true);

				// scheduling
				TaskSchedulingType schedulingType = TaskSchedulingType.getValue(attrScheduling);
				curTask.setScheduling(schedulingType);

				// multiplicity
				if (schedulingType == TaskSchedulingType.INF && (attrMultiplicity == null)) {
					curTask.setMultiplicity(LqnConstants.INFINITY.getConstantValue());
				} else if (attrMultiplicity != null) {
					int multiplicity = Integer.parseInt(attrMultiplicity);
					curTask.setMultiplicity(multiplicity);
					if (curProcessor != null && curProcessor.getScheduling() == ProcessorSchedulingType.INF) {
						curProcessor.setMultiplicity(multiplicity);
					}
				} else if ((attrMultiplicity == null)) {
					curTask.setMultiplicity(LqnDefaults.TASK_MULTIPLICITY.getValue());
				}

				// replication
				if (attrReplication != null) {
					curTask.setReplication(Integer.parseInt(attrReplication));
				} else if ((attrReplication == null)) {
					curTask.setReplication(LqnDefaults.TASK_REPLICATION.getValue());
				}

				break;
			case RESULT_TASK:
				break;
				
			case ENTRY:
				String attrType = attributes.getValue(LqnXmlAttributes.TYPE.toString());

				// name,type
				curEntry = lqnModel.getEntryByName(attrName, curTask, true);
				curEntry.setEntryType(EntryAcType.getValue(attrType));
				break;
			case RESULT_ENTRY:
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
				String attrBoundEntry = attributes.getValue(LqnXmlAttributes.BOUND_TO_ENTRY.toString());
				String attrPhase = attributes.getValue(LqnXmlAttributes.PHASE.toString());
				String attrHostDemand = attributes.getValue(LqnXmlAttributes.HOST_DEMAND_MEAN.toString());

				if (isTaskActivities) {
					// bound-to-entry
					// note: bound-to-entry may be null
					if (attrBoundEntry != null) {
						curEntry = lqnModel.getEntryByName(attrBoundEntry);
					}

					// name
					curActivity = lqnModel.getActivityDefByName(attrName, curTask, curTaskActivities, curEntry, true);
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
					curActivity = lqnModel.getActivityPHByName(attrName, curEntry, phase, true);
				}

				// host-demand-mean
				if (attrHostDemand != null) {
					float hostDemand = Float.parseFloat(attrHostDemand);
					curActivity.setHost_demand_mean(hostDemand);
				}

				break;
			case RESULT_ACTIVITY:
				break;
				
			case SYNCH_CALL:
				// dest, callsmean, fanin,fanout
				String attrDest = attributes.getValue(LqnXmlAttributes.DEST.toString());
				String attrCallsMean = attributes.getValue(LqnXmlAttributes.CALLS_MEAN.toString());
				String attrFanin = attributes.getValue(LqnXmlAttributes.FANIN.toString());
				String attrFanout = attributes.getValue(LqnXmlAttributes.FANOUT.toString());
				float callsMean = Float.parseFloat(attrCallsMean);

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
				} else if (isTaskActivities) {
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

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		LqnXmlElements et = LqnXmlElements.getValue(qName);

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
