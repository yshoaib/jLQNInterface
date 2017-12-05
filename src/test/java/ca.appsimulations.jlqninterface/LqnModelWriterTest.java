package ca.appsimulations.jlqninterface;


import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.lqn.model.parser.LqnInputParser;
import ca.appsimulations.jlqninterface.lqn.model.writer.LqnModelWriter;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;


public class LqnModelWriterTest {

    @Test
    public void testWriter() throws IOException {

        LqnModel lqnModel = new LqnModel();
        LqnInputParser lqnInputParser = new LqnInputParser(lqnModel, true);


        ClassPathResource inputFileResource = new ClassPathResource("input.lqnx");
        if (!inputFileResource.exists()) {
            throw new FileNotFoundException(inputFileResource.getPath() + " does not exist");
        }
        String inputFilePath = inputFileResource.getFile().getAbsolutePath();

        lqnInputParser.parseFile(inputFilePath);

        LqnModelWriter lqnModelWriter = new LqnModelWriter();
        try {
            lqnModelWriter.WriteFile(lqnModel,"test");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
