package ca.appsimulations.jlqninterface;

import ca.appsimulations.jlqninterface.lqn.model.handler.LqnSolver;
import ca.appsimulations.jlqninterface.utilities.FileHandler;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SavePostScript {
    private final String INPUT_LQNX = "input.lqnx";
    private final String OUTPUT_PS = "input.ps";
    private File outputFile;

    @BeforeMethod
    void beforeMethod() {
        outputFile = new File(OUTPUT_PS);
        outputFile.delete();
    }


    @Test
    public void test() throws Exception {
        LqnSolver.savePostScript(FileHandler.getResourceFile(INPUT_LQNX).getAbsolutePath(),
                                 outputFile.getAbsolutePath());
        assertThat(FileHandler.fileExists(outputFile.getAbsolutePath())).isTrue();
    }
}
