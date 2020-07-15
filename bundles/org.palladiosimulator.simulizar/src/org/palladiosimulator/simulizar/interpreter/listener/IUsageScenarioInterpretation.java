package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;

public interface IUsageScenarioInterpretation {
  public default void beginUsageScenarioInterpretation(ModelElementPassedEvent<UsageScenario> event) {

  }

  public default void endUsageScenarioInterpretation(ModelElementPassedEvent<UsageScenario> event) {

  }
}