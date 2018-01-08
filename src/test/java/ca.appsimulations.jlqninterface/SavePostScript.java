package ca.appsimulations.jlqninterface;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.lqn.model.handler.LqnSolver;
import ca.appsimulations.jlqninterface.lqn.model.parser.LqnResultParser;
import ca.appsimulations.jlqninterface.utilities.FileHandler;
import org.testng.annotations.Test;

import java.io.File;

import static ca.appsimulations.jlqninterface.utilities.FileHandler.getResourceFile;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class SavePostScript {
    private final String INPUT_LQNX = "input.lqnx";

    @Test
    public void savePostScriptOfInputLqnModel() throws Exception {

        File outputPs = new File("input.ps");
        outputPs.delete();
        LqnSolver.savePostScript(getResourceFile(INPUT_LQNX).getAbsolutePath(),
                                 outputPs.getAbsolutePath());
        assertThat(FileHandler.fileExists(outputPs.getAbsolutePath())).isTrue();
    }

    @Test
    public void savePostScriptOfOutputLqnModel() throws Exception {
        File outputFile = new File("test-output.lqxo");
        File outputPs = new File("test-output.ps");
        outputFile.delete();
        outputPs.delete();

        LqnSolver.solveLqns(getResourceFile(INPUT_LQNX).getAbsolutePath(), new LqnResultParser(new LqnModel()),
                            outputFile.getAbsolutePath());
        assertThat(FileHandler.fileExists(outputFile.getAbsolutePath())).isTrue();

        LqnSolver.savePostScript(outputFile.getAbsolutePath(),
                                 outputPs.getAbsolutePath());
        assertThat(FileHandler.fileExists(outputPs.getAbsolutePath())).isTrue();
    }
}
