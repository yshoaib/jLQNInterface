package ca.appsimulations.jlqninterface.utilities;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import java.util.List;

public class Utility {
    public static String listToQuotationStrArray(List list) {
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
}
