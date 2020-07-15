package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

public interface IUsageModelInterpreterListener {

  public default void beginUsageScenarioInterpretation(ModelElementPassedEvent<UsageScenario> event) {

  }

  public default void endUsageScenarioInterpretation(ModelElementPassedEvent<UsageScenario> event) {

  }

  public default void beginEntryLevelSystemCallInterpretation(ModelElementPassedEvent<EntryLevelSystemCall> event) {
    
  }

  public default void endEntryLevelSystemCallInterpretation(ModelElementPassedEvent<EntryLevelSystemCall> event) {

  }
}