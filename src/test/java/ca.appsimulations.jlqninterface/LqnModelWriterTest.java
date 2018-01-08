package ca.appsimulations.jlqninterface;


import ca.appsimulations.jlqninterface.lqn.model.LqnModel;
import ca.appsimulations.jlqninterface.lqn.model.parser.LqnInputParser;
import ca.appsimulations.jlqninterface.lqn.model.writer.LqnModelWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

import static ca.appsimulations.jlqninterface.utilities.FileHandler.getResourceFile;
import static org.assertj.core.api.Java6Assertions.assertThat;

@Slf4j
public class LqnModelWriterTest {
    private File outputFile;
    private SoftAssertions softly;
    private final String LQN_XSD = "lqn.xsd";
    private final String INPUT_LQNX = "input.lqnx";
    private final String OUTPUT_FILE = "test-output.lqnx";

    @BeforeMethod
    void beforeMethod() {
        outputFile = new File(OUTPUT_FILE);
        outputFile.delete();
        softly = new SoftAssertions();
    }

    @Test
    public void testWriter() throws Exception {

        LqnModel lqnModel = new LqnModel();
        new LqnInputParser(lqnModel, true).parseFile(getResourceFile(INPUT_LQNX).getAbsolutePath());
        new LqnModelWriter().write(lqnModel, outputFile.getAbsolutePath());
        assertXmlAgainstSchema(outputFile.getAbsolutePath(), LQN_XSD);
        assertThat(readFile(outputFile)).as("xml contents verification").isXmlEqualToContentOf(getResourceFile(
                INPUT_LQNX));
        softly.assertAll();
    }

    private String readFile(File file) throws IOException {
        return IOUtils.toString(file.toURI(), "UTF-8");
    }

    private void assertXmlAgainstSchema(String xmlFile, String schemaPath) throws Exception {
        Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
        v.setSchemaSource(new StreamSource(getResourceFile(schemaPath)));
        ValidationResult validationResult = v.validateInstance(new StreamSource(xmlFile));
        softly.assertThat(validationResult.isValid()).as("xml valid against schema").isTrue();
    }

}
