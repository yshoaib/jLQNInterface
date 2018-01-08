package ca.appsimulations.jlqninterface.lqn.model.handler;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import ca.appsimulations.jlqninterface.lqn.entities.ResultTypes;
import ca.appsimulations.jlqninterface.lqn.model.parser.LqnResultParser;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class LqnSolver {

    public static boolean solveLqns(String inputPath, LqnResultParser lqnResultParser, String xmlOutputFilePath) {
        StringBuilder strCmd = new StringBuilder();
        Boolean isError = false;

        strCmd.append("lqns ").append("-x ").append("-o ").append(xmlOutputFilePath).append(" ").append(inputPath);
        log.info("----Running lqns ----");

        try {
            Process p = Runtime.getRuntime().exec(strCmd.toString());
            p.waitFor();

            String read;

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((read = inputReader.readLine()) != null) {
                log.debug("[Input Reader]: " + read);
                if (read.indexOf("error:") != -1 || read.indexOf("advisory:") != -1) {
                    log.debug("[error] above");
                    isError = true;
                }
            }
            inputReader.close();
            while ((read = errorReader.readLine()) != null) {
                isError = true;
                log.debug("[Error Reader]: " + read);
            }
            errorReader.close();

            if ((p.exitValue() != 0) || isError) {
                return false;
            }

            log.info("Parsing output file: " + xmlOutputFilePath);

            try {
                lqnResultParser.parseFile(xmlOutputFilePath);
            }
            catch (FileNotFoundException fnfe) {
                log.debug("[FileNotFoundException]: " + fnfe.getMessage());
                fnfe.printStackTrace();
                return false;
            }

            return lqnResultParser.getLqnModel().result().getResultValid().equals(ResultTypes.YES);

        }
        catch (IOException io) {
            log.debug("[IOException]: " + io.getMessage());
        }
        catch (InterruptedException intE) {
            log.debug("[InterruptedException]: " + intE.getMessage());
        }
        return false;
    }

    public static boolean savePostScript(String filePath, String outputPath) {
        boolean result = false;
        StringBuilder strCmd = new StringBuilder();
        strCmd.append("lqn2ps ").append("-o ").append(outputPath).append(" ").append(filePath);
        log.info("----Running lqn2ps ----");

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

        return result;
    }
}
