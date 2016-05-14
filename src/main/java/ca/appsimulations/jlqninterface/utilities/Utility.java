package ca.appsimulations.jlqninterface.utilities;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import java.util.*;

public class Utility {

	public static String listToQutotationStrArray(List list) {
		StringBuilder strB = new StringBuilder();
		strB.append("[");
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				strB.append("\"");
				strB.append(list.get(i));
				strB.append("\"");
				strB.append(",");
			}
			strB.deleteCharAt(strB.length() - 1); // remove the ','
		}
		strB.append("]");

		return strB.toString();
	}

	public static void debug(String str) {
		System.err.println(str);
	}

	public static void print(String str) {
		System.out.println(str);
	}

	public static void printAndDebug(String str) {
		print(str);
		debug(str);
	}
}
