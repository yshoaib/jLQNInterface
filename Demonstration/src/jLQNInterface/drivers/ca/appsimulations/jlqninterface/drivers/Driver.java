package ca.appsimulations.jlqninterface.drivers;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 * 
 * This is the main Driver program. Please run this program first.
 */

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import ca.appsimulations.jlqninterface.algorithms.Algorithm;
import ca.appsimulations.jlqninterface.algorithms.Algorithm1;
import ca.appsimulations.jlqninterface.core.Model;

public class Driver {

	public static void main(String[] args) {
		Model workspace = new Model("src/jLQNInterface/app.properties");
		Algorithm alg = new Algorithm1(workspace);
		alg.run();
	}

}
