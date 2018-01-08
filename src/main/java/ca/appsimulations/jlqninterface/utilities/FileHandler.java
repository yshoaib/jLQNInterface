package ca.appsimulations.jlqninterface.utilities;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import org.apache.commons.io.IOUtils;

import java.io.File;

public class FileHandler {

    public static boolean fileExists(String filePath) {
        File f = new File(filePath);
        if (f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }

    public static String readResource(String resourceName) throws Exception {
        return IOUtils.toString(
                FileHandler.class.getClassLoader().getResourceAsStream(resourceName),
                "UTF-8");
    }

    public static File getResourceFile(String resourceName) throws Exception {
        return new File(FileHandler.class.getClassLoader().getResource(resourceName).toURI());
    }

}
