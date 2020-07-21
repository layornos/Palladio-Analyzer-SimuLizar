package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
public class LogDebugUsageModel implements IUsageModelInterpreterListener {
  
  public  void beginUsageScenarioInterpretation(ModelElementPassedEvent<UsageScenario> event) {
    ILogDebugListener.logEvent(event);

  }

  public  void endUsageScenarioInterpretation(ModelElementPassedEvent<UsageScenario> event) {
    ILogDebugListener.logEvent(event);

  }

  public  void beginEntryLevelSystemCallInterpretation(ModelElementPassedEvent<EntryLevelSystemCall> event) {
    ILogDebugListener.logEvent(event);

  }

  public  void endEntryLevelSystemCallInterpretation(ModelElementPassedEvent<EntryLevelSystemCall> event) {
    ILogDebugListener.logEvent(event);

  }
}