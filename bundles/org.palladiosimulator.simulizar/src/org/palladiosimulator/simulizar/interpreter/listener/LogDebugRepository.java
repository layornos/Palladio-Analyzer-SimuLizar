package org.palladiosimulator.simulizar.interpreter.listener;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.repository.OperationSignature;

public class LogDebugRepository implements IRepositoryInterpreterListener, ILogDebugListener {
  
    private static final Logger LOGGER = Logger.getLogger(LogDebugRepository.class);

  @Override
  public void beginSystemOperationCallInterpretation(final ModelElementPassedEvent<OperationSignature> event) {
    logEvent(event, LOGGER);
  }

  @Override
  public void endSystemOperationCallInterpretation(final ModelElementPassedEvent<OperationSignature> event) {
    logEvent(event, LOGGER);
  }
}