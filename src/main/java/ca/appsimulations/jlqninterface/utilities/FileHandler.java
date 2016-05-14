package ca.appsimulations.jlqninterface.utilities;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */
import java.io.*;

public class FileHandler {

	public static boolean doesFileExist(String fileName) {
		File f = new File(fileName);
		if (f.exists() && !f.isDirectory()) {
			return true;
		}
		return false;
	}
}
