package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.repository.OperationSignature;

public class LogDebugRepository implements IRepositoryInterpreterListener {
  
  @Override
  public void beginSystemOperationCallInterpretation(final ModelElementPassedEvent<OperationSignature> event) {
    LogDebugListener.logEvent(event);
  }

  @Override
  public void endSystemOperationCallInterpretation(final ModelElementPassedEvent<OperationSignature> event) {
    LogDebugListener.logEvent(event);
  }
}