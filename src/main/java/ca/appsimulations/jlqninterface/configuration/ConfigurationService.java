package ca.appsimulations.jlqninterface.configuration;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true, chain = true)
public class ConfigurationService {

    @Value("${inputFilePath}")
    private String inputFilePath;

    @Value("${autoInputFileName}")
    private String autoInputFilePath;

    @Value("${lqnXmlOutputFileName}")
    private String lqnXmlOutputFilePath;

    @Value("${responseTimeObjective}")
    private double responseTimeObjective;

    @Value("${maxVMReplicas}")
    private int maxVMReplicas = 10;

    @Value("${maxProcsPerVM}")
    private int maxProcsPerVM = 8;

    @Value("${spareVMs}")
    private int spareVMs = 20;

    @Value("${satThreshold}")
    private double satThreshold;

    @Value("${bottleneckMaxBStrengthTaskOnly}")
    private boolean bottleneckMaxBStrengthTaskOnly;

    @PostConstruct
    public ConfigurationService initialize() throws IOException {
        ClassPathResource inputFileResource = new ClassPathResource(inputFilePath);
        if (!inputFileResource.exists()) {
            throw new FileNotFoundException(inputFileResource.getPath() + " does not exist");
        }
        this.inputFilePath = inputFileResource.getFile().getAbsolutePath();

        if (SystemUtils.IS_OS_WINDOWS) {
            this.autoInputFilePath =
                    inputFilePath.substring(0, inputFilePath.lastIndexOf("\\")) + "\\" + autoInputFilePath;
        }
        else {
            this.autoInputFilePath =
                    inputFilePath.substring(0, inputFilePath.lastIndexOf("/")) + "/" + autoInputFilePath;
        }

        ClassPathResource autoInputFileResource = new ClassPathResource(autoInputFilePath);
        if (autoInputFileResource.exists()) {
            autoInputFileResource.getFile().delete();
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            this.lqnXmlOutputFilePath =
                    inputFilePath.substring(0, inputFilePath.lastIndexOf("\\")) + "\\" + lqnXmlOutputFilePath;
        }
        else {
            this.lqnXmlOutputFilePath =
                    inputFilePath.substring(0, inputFilePath.lastIndexOf("/")) + "/" + lqnXmlOutputFilePath;
        }
        ClassPathResource lqnXmlOutputFileResource = new ClassPathResource(lqnXmlOutputFilePath);
        if (lqnXmlOutputFileResource.exists()) {
            lqnXmlOutputFileResource.getFile().delete();
        }
        return this;
    }

}
