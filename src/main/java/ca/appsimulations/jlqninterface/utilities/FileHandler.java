package ca.appsimulations.jlqninterface.utilities;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
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

    public static void openFile(String filePath) {
        if (SystemUtils.IS_OS_WINDOWS) {
            log.info("openFile() is not supported for windows");
            return;
        }
        boolean result = false;
        StringBuilder strCmd = new StringBuilder();
        strCmd.append("open ").append(filePath);

        try {
            Process p = Runtime.getRuntime().exec(strCmd.toString());
            p.waitFor();

            if ((p.exitValue() == 0)) {
                result = true;
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
