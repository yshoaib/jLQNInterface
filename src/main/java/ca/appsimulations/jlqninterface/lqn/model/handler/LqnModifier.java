package ca.appsimulations.jlqninterface.lqn.model.handler;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.lqn.entities.ActivityPhases;
import ca.appsimulations.jlqninterface.lqn.entities.Entry;
import ca.appsimulations.jlqninterface.lqn.entities.SynchCall;
import ca.appsimulations.jlqninterface.lqn.entities.Task;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.*;

import ca.appsimulations.jlqninterface.lqn.entities.Processor;
import ca.appsimulations.jlqninterface.lqn.entities.ProcessorSchedulingType;
import ca.appsimulations.jlqninterface.lqn.entities.TaskSchedulingType;

import java.io.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//visit: http://docs.oracle.com/javase/tutorial/jaxp/xslt/writingDom.html
@Slf4j
public class LqnModifier {
	private TransformerFactory tFactory;
	private Transformer transformer;
	private LqnModel lqnModel;

	public LqnModifier(LqnModel lqnModel) {
		tFactory = TransformerFactory.newInstance();
		try {
			transformer = tFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			log.debug("[TransformerConfigurationException]: " + e.getMessage());
			e.printStackTrace();
		}

		this.lqnModel = lqnModel;
	}

	/**
	 * Add LQXCData and update processors, tasks, entries, etc.
	 * 
	 * Uses in-memory model image to change the model.
	 * Reads the in-memory model, parses the inputFilePath, does comparison between them to determine changes
	 * and outputs the updated model to the outputFilePath
	 * 
	 * @param lqxCDataString
	 * @param inputFilePath
	 * @param outputFilePath
	 */
	public void parseAndUpdateXML(String lqxCDataString, String inputFilePath, String outputFilePath) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		File f = new File(inputFilePath);

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);

			updateXMLProcessors(doc);
			updateXMLTasks(doc);
			updateXMLEntriesDepth(doc);

			addCDATAString(doc, lqxCDataString);

			transformLQNXMLDocument(doc, outputFilePath);
		} catch (Exception e) {
			log.debug("[Exception]: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void printNodes(Document doc) {
		// Printing nodes
		Node n = doc.getDocumentElement().getFirstChild();
		while (n != null) {
			log.info("NODE: " + n.getNodeName());
			n = n.getNextSibling();
		}
	}

	private void transformLQNXMLDocument(Document doc, String outputFilePath) {
		// Use a Transformer for output
		DOMSource source = new DOMSource(doc);
		FileWriter fstream;
		try {
			fstream = new FileWriter(outputFilePath);

			StreamResult result = new StreamResult(fstream);
			log.debug("[LqnModifier] Writing XML to " + outputFilePath + "\n\n");

			transformer.transform(source, result);
		} catch (TransformerException e) {
			log.debug("[TransformerConfigurationException]: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.debug("[IOException]: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void addCDATAString(Document doc, String lqxCDataString) {
		// Modifying LQX node
		NodeList lqxNodes = doc.getElementsByTagName(LqnXmlElements.LQX.toString());
		Node newChild = doc.createCDATASection(lqxCDataString);

		if (lqxNodes.getLength() > 0) {
			for (int i = 0; i < lqxNodes.getLength(); i++) {
				// System.out.println(lqxNodes.item(i).getTextContent());
				// lqxNodes.item(i).setTextContent("TEST");
				// lqxNodes.item(i).setTextContent("");
				lqxNodes.item(i).removeChild(lqxNodes.item(i).getFirstChild());
				lqxNodes.item(i).appendChild(newChild);
			}
		} else {
			Element lqxNode = doc.createElement(LqnXmlElements.LQX.toString());
			lqxNode.appendChild(newChild);
			doc.getFirstChild().appendChild(lqxNode);
		}
	}

	private void updateXMLProcessors(Document doc) {
		NodeList nodes = doc.getElementsByTagName(LqnXmlElements.PROCESSOR.toString());

		for (Processor p : lqnModel.getProcessors()) {
			Node n = getNodeByElemAttributeName(doc, LqnXmlElements.PROCESSOR, p.getName(), nodes);

			if (n == null) {
				// TODO
				log.debug("Duplicate processor: " + p + " " + p.getDuplicatedFrom());
				Node duplicatedFrom = getNodeByElemAttributeName(doc, LqnXmlElements.PROCESSOR, p.getDuplicatedFrom().toString(), nodes);
				Node cl = duplicatedFrom.cloneNode(false);
				setAttributeValue(cl, LqnXmlAttributes.NAME.toString(), p.getName());

				duplicatedFrom.getParentNode().appendChild(cl);
				// Node not present - manually added.
				continue;
			}

			NamedNodeMap nMap = n.getAttributes();

			// SCHEDULING
			Node tmpNode = nMap.getNamedItem(LqnXmlAttributes.SCHEDULING.toString());
			String nodeValue = tmpNode.getNodeValue();

			if (!nodeValue.equals(p.getScheduling().toString())) {
				// modification required here.
				tmpNode.setNodeValue(p.getScheduling().toString());
				log.debug("[Changed] processor scheduling");
			}

			// MULTIPLICITY
			tmpNode = nMap.getNamedItem(LqnXmlAttributes.MULTIPLICITY.toString());
			if (tmpNode != null) {
				nodeValue = tmpNode.getNodeValue();
				if (!nodeValue.equals(p.getMutiplicityString())) {
					// modification required here.
					tmpNode.setNodeValue(p.getMutiplicityString());
					log.debug("[Changed] processor multiplicity");
				}
			} else if ((tmpNode == null) && (p.getScheduling() != ProcessorSchedulingType.INF) && (p.getMultiplicity() != 1)) {
				Element e = (Element) n;
				e.setAttribute(LqnXmlAttributes.MULTIPLICITY.toString(), p.getMutiplicityString());
				log.debug("[Changed] processor multiplicity");
			}

			// REPLICATION
			tmpNode = nMap.getNamedItem(LqnXmlAttributes.REPLICATION.toString());
			String repString = "" + p.getReplication();
			if (tmpNode != null) {
				nodeValue = tmpNode.getNodeValue();
				if (!nodeValue.equals(repString)) {
					// modification required here.
					tmpNode.setNodeValue(repString);
					log.debug("[Changed] processor replication");
				}
			} else if ((tmpNode == null) && (p.getScheduling() != ProcessorSchedulingType.INF) && (p.getReplication() != 1)) {
				Element e = (Element) n;
				e.setAttribute(LqnXmlAttributes.REPLICATION.toString(), repString);
				log.debug("[Changed] processor replication");
			}
		}
	}

	/**
	 * 
	 * @param doc
	 * @param t
	 * @return true/false: is node present in XML doc
	 */
	private boolean updateXMLTask(Document doc, Task t) {
		NodeList nodes = doc.getElementsByTagName(LqnXmlElements.TASK.toString());
		Node n = getNodeByElemAttributeName(doc, LqnXmlElements.TASK, t.getName(), nodes);

		if (n == null) {
			String duplicatedFromName = t.getDuplicatedFrom().getName();
			log.debug("Duplicate task: " + t + " " + duplicatedFromName);
			Node duplicatedFrom = getNodeByElemAttributeName(doc, LqnXmlElements.PROCESSOR, duplicatedFromName, nodes);
			Node cl = duplicatedFrom.cloneNode(false);
			setAttributeValue(cl, LqnXmlAttributes.NAME.toString(), t.getName());
			Node parentNode = getNodeByElemAttributeName(doc, LqnXmlElements.PROCESSOR, t.getProcessor().getName());
			parentNode.appendChild(cl);
			// Node not present - manually added.

			return false;
		}

		NamedNodeMap nMap = n.getAttributes();

		// SCHEDULING
		Node tmpNode = nMap.getNamedItem(LqnXmlAttributes.SCHEDULING.toString());
		String nodeValue = tmpNode.getNodeValue();

		if (!nodeValue.equals(t.getScheduling().toString())) {
			// modification required here.
			tmpNode.setNodeValue(t.getScheduling().toString());
			log.debug("[Changed] task scheduling");
		}

		// MULTIPLICITY
		tmpNode = nMap.getNamedItem(LqnXmlAttributes.MULTIPLICITY.toString());
		if (tmpNode != null) {
			nodeValue = tmpNode.getNodeValue();
			if (!nodeValue.equals(t.getMutiplicityString())) {
				// modification required here.
				tmpNode.setNodeValue(t.getMutiplicityString());
				log.debug("[Changed] task multiplicity");
			}
		} else if ((tmpNode == null) && (t.getScheduling() != TaskSchedulingType.INF) && (t.getMultiplicity() != 1)) {
			Element e = (Element) n;
			e.setAttribute(LqnXmlAttributes.MULTIPLICITY.toString(), t.getMutiplicityString());
			log.debug("[Changed] task multiplicity");
		}

		// REPLICATION
		tmpNode = nMap.getNamedItem(LqnXmlAttributes.REPLICATION.toString());
		String repString = "" + t.getReplication();
		if (tmpNode != null) {
			nodeValue = tmpNode.getNodeValue();
			if (!nodeValue.equals(repString)) {
				// modification required here.
				tmpNode.setNodeValue(repString);
				log.debug("[Changed] task replication");
			}
		} else if ((tmpNode == null) && (t.getScheduling() != TaskSchedulingType.INF) && (t.getReplication() != 1)) {

			Element e = (Element) n;
			e.setAttribute(LqnXmlAttributes.REPLICATION.toString(), repString);
			log.debug("[Changed] task replication");
		}

		return true;
	}

	private void updateXMLTasks(Document doc) {
		boolean isPresent = false;
		for (Task t : lqnModel.getTasks()) {
			isPresent = updateXMLTask(doc, t);

			if (!isPresent) {
				// task manually added to workspace.
			}
		}
	}

	private boolean updateXMLEntryDepth(Document doc, Entry e) {
		NodeList nodes = doc.getElementsByTagName(LqnXmlElements.ENTRY.toString());

		Node n = getNodeByElemAttributeName(doc, LqnXmlElements.ENTRY, e.getName(), nodes);

		if (n == null) {
			// Node not present - manually added.

			log.debug("Duplicate entry: " + e + " " + e.getDuplicatedFrom());
			Node duplicatedFrom = getNodeByElemAttributeName(doc, LqnXmlElements.ENTRY, e.getDuplicatedFrom(), nodes);
			Node cl = duplicatedFrom.cloneNode(true);
			setAttributeValue(cl, LqnXmlAttributes.NAME.toString(), e.getName());
			Node parentNode = getNodeByElemAttributeName(doc, LqnXmlElements.TASK, e.getTask().getName());
			parentNode.appendChild(cl);

			n = cl;
			// return false;
		}
		updateEntryPhaseAcDepth(doc, n, e);

		return true;
	}

	private void updateEntryPhaseAcDepth(Document doc, Node entryNode, Entry e) {
		// entry-phase XML node
		Node entryPhAcNode = entryNode.getFirstChild().getNextSibling();
		if (!entryPhAcNode.getNodeName().equals(LqnXmlElements.ENTRY_PHASE_ACTIVITIES.toString())) {
			SAXException se = new SAXException("Node name is not <entry-phase-activities>");
			log.debug(se.getMessage());
			se.printStackTrace();
		}

		Node activityNode = entryPhAcNode.getFirstChild().getNextSibling();
		// update ActivityPhases
		for (int phase = 1; phase < e.getActivityPhasesSize() + 1; phase++) {
			// workspace phase1 activity
			ActivityPhases ap = (ActivityPhases) e.getActivityAtPhase(phase);
			if (ap == null) {
				SAXException se = new SAXException("LqnModel entry has no phase " + phase + " activities");
				log.debug(se.getMessage());
				se.printStackTrace();
			}

			// phase1 activity XML node
			if (!activityNode.getNodeName().equals(LqnXmlElements.ACTIVITY.toString())) {
				SAXException se = new SAXException("Node name is not <activitites>");
				log.debug(se.getMessage());
				se.printStackTrace();
			}
			updateActivityPhasesDepth(doc, activityNode, ap, phase);
			activityNode = activityNode.getNextSibling().getNextSibling();
		}
	}

	private void updateActivityPhasesDepth(Document doc, Node activityNode, ActivityPhases ap, int phase) {
		// check activity name
		NamedNodeMap nMap = activityNode.getAttributes();
		String acName = getAttributeValue(nMap, LqnXmlAttributes.NAME.toString());
		String acPhase = getAttributeValue(nMap, LqnXmlAttributes.PHASE.toString());
		String apName = ap.getName();

		// System.out.println(apName + " " + acName);
		if (!acName.equals(apName)) {
			if (ap.isDuplicate()) {
				setAttributeValue(activityNode, LqnXmlAttributes.NAME.toString(), apName);
			} else {
				SAXException se = new SAXException("XML Activity name is not same as workspace values");
				log.debug(se.getMessage());
				se.printStackTrace();
			}
		}

		// phase
		String apPhase = "" + ap.getPhase();
		if (!acPhase.equals(apPhase)) {
			SAXException se = new SAXException("XML Activity phase is not same as workspace phase");
			log.debug(se.getMessage());
			se.printStackTrace();
		}

		// host-demand-mean
		float acHostDemand = Float.parseFloat(nMap.getNamedItem(LqnXmlAttributes.HOST_DEMAND_MEAN.toString()).getNodeValue());
		if (acHostDemand != ap.getHost_demand_mean()) {
			Element element = (Element) activityNode;
			element.setAttribute(LqnXmlAttributes.HOST_DEMAND_MEAN.toString(), "" + ap.getHost_demand_mean());
		}

		// get synch calls
		NodeList childNodesActivity = activityNode.getChildNodes();

		for (SynchCall s : ap.getSynchCalls()) {
			String destName = s.getDestEntry().getName();
			Node destNode = this.getNodeFromNodeList(childNodesActivity, LqnXmlAttributes.DEST.toString(), destName);
			if (destNode == null) {
				log.debug("Duplicate synch-call: dest-entry is " + destName + " (actvity): " + ap.getName());
				// synch-call doesn't exist. manually-added
				int c = 0;
				Node cNode = null;
				while (c < childNodesActivity.getLength()) {
					cNode = childNodesActivity.item(c);
					if (cNode.getNodeName().equals(LqnXmlElements.SYNCH_CALL.toString())) {
						break;
					}
					c++;
				}

				if (cNode == null) {
					SAXException se = new SAXException("PROBLEM: cNode is null");
					log.debug(se.getMessage());
					se.printStackTrace();
				} else {
					Node newNode = cNode.cloneNode(true);
					this.setAttributeValue(newNode, LqnXmlAttributes.DEST.toString(), destName);
					cNode.getParentNode().appendChild(newNode);
				}
			}
		}

		for (int i = 0; i < childNodesActivity.getLength(); i++) {
			Node cNode = childNodesActivity.item(i);
			if (cNode.getNodeName().equals(LqnXmlElements.SYNCH_CALL.toString())) {
				String dest = this.getAttributeValue(cNode, LqnXmlAttributes.DEST.toString());
				String strFanin = this.getAttributeValue(cNode, LqnXmlAttributes.FANIN.toString());
				String strFanout = this.getAttributeValue(cNode, LqnXmlAttributes.FANOUT.toString());
				String strCallsMean = this.getAttributeValue(cNode, LqnXmlAttributes.CALLS_MEAN.toString());

				SynchCall s = ap.getSynchCallByStrDestEntry(dest);
				if (s == null) {
					log.debug("No synch-call by dest " + dest + " in workspace, activity: " + ap.getName());
					continue;
				}

				int fanin = 1, fanout = 1;
				float callsMean = 1.0f;

				if (strFanin != null) {
					fanin = Integer.parseInt(strFanin);
					if (fanin != s.getFanin()) {
						log.debug("[Changed] fanin");
						this.setAttributeValue(cNode, LqnXmlAttributes.FANIN.toString(), s.getFanin() + "");
					}
				}

				if (strFanout != null) {
					fanout = Integer.parseInt(strFanout);
					if (fanout != s.getFanout()) {
						log.debug("[Changed] fanout");
						this.setAttributeValue(cNode, LqnXmlAttributes.FANOUT.toString(), s.getFanout() + "");
					}
				}

				if (strCallsMean != null) {
					callsMean = Float.parseFloat(strCallsMean);

					if (callsMean != s.getCallsMean()) {
						log.debug("[Changed] callsmean");
						this.setAttributeValue(cNode, LqnXmlAttributes.CALLS_MEAN.toString(), s.getCallsMean() + "");
					}
				}
			}
		}
	}

	/**
	 * Update Entries in XML Doc and the child activities and synch-calls.
	 * (depth modification).
	 * 
	 * @param doc
	 */
	private void updateXMLEntriesDepth(Document doc) {
		boolean isPresent = false;
		// traverse through workspace entries and get the related xml node.
		for (Entry e : lqnModel.getEntries()) {
			isPresent = updateXMLEntryDepth(doc, e);

			// if (!isPresent) {
			// entry manually added to workspace.
			// }
		}
	}

	private Node getNodeByElemAttributeName(Document doc, LqnXmlElements e, String lqnElementName, NodeList nodes) {

		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			NamedNodeMap nMap = n.getAttributes();
			String nodeValue = nMap.getNamedItem(LqnXmlAttributes.NAME.toString()).getNodeValue();

			if (lqnElementName.equals(nodeValue)) {
				return n;
			}
		}

		return null;
	}

	private Node getNodeByElemAttributeName(Document doc, LqnXmlElements e, String lqnElementName) {
		NodeList nodes = doc.getElementsByTagName(e.toString());
		return (this.getNodeByElemAttributeName(doc, e, lqnElementName, nodes));
	}

	private String getAttributeValue(NamedNodeMap nMap, String attrName) {
		return nMap.getNamedItem(attrName).getNodeValue();
	}

	private String getAttributeValue(Node n, String attrName) {
		if (n == null) {
			return null;
		}
		NamedNodeMap nMap = n.getAttributes();
		if (nMap == null) {
			return null;
		}
		Node valueNode = nMap.getNamedItem(attrName);
		if (valueNode == null) {
			return null;
		}
		return valueNode.getNodeValue();
	}

	private void setAttributeValue(Node n, String attrName, String newValue) {
		Element element = (Element) n;
		element.setAttribute(attrName, newValue);
	}

	private Node getNodeFromNodeList(NodeList nl, String attrName, String attrValue) {
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			String value = getAttributeValue(n, attrName);
			if ((value != null) && value.equals(attrValue)) {
				return n;
			}
		}
		return null;
	}

}
