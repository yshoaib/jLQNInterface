package ca.appsimulations.jlqninterface.lqn.model.factory;

import ca.appsimulations.jlqninterface.lqn.entities.LqnConstants;
import ca.appsimulations.jlqninterface.lqn.entities.LqnDefaults;
import ca.appsimulations.jlqninterface.lqn.entities.Processor;
import ca.appsimulations.jlqninterface.lqn.entities.ProcessorSchedulingType;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

public class ProcessorFactory {

    public static Processor build(LqnModel lqnModel,
                                  String name,
                                  boolean refTask,
                                  int numOfCores) {
        Processor processor = new Processor(lqnModel,
                                            name);
        if (refTask) {
            processor.setScheduling(ProcessorSchedulingType.INF);
            processor.setMultiplicity(LqnConstants.INFINITY.getConstantValue());
        }
        else {
            processor.setScheduling(ProcessorSchedulingType.PS);
            processor.setMultiplicity(numOfCores);
            processor.setQuantum(0.2);
        }
        processor.setReplication(LqnDefaults.PROCESSOR_REPLICATION.getValue());
        return processor;
    }

    public static Processor build(LqnModel lqnModel,
                                  String name,
                                  boolean refTask,
                                  int numOfCores,
                                  int replicationCount) {
        Processor processor = new Processor(lqnModel,
                                            name);
        if (refTask) {
            processor.setScheduling(ProcessorSchedulingType.INF);
            processor.setMultiplicity(LqnConstants.INFINITY.getConstantValue());
        }
        else {
            processor.setScheduling(ProcessorSchedulingType.PS);
            processor.setMultiplicity(numOfCores);
            processor.setQuantum(0.2);
        }
        processor.setReplication(replicationCount);
        return processor;
    }
}
