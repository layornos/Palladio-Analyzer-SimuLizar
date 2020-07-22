package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
public class LogDebugUsageModel implements IUsageModelInterpreterListener, ILogDebugListener {
  
    private static final Logger LOGGER = Logger.getLogger(LogDebugUsageModel.class);

  public  void beginUsageScenarioInterpretation(ModelElementPassedEvent<UsageScenario> event) {
    logEvent(event, LOGGER);

  }

  public  void endUsageScenarioInterpretation(ModelElementPassedEvent<UsageScenario> event) {
    logEvent(event, LOGGER);

  }

  public  void beginEntryLevelSystemCallInterpretation(ModelElementPassedEvent<EntryLevelSystemCall> event) {
    logEvent(event, LOGGER);

  }

  public  void endEntryLevelSystemCallInterpretation(ModelElementPassedEvent<EntryLevelSystemCall> event) {
    logEvent(event, LOGGER);

  }
}