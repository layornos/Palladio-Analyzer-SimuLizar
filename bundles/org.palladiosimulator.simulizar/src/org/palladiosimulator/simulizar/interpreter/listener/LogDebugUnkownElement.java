package org.palladiosimulator.simulizar.interpreter.listener;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;

public class LogDebugUnkownElement implements IUnknownElementInterpretation, ILogDebugListener {
  
    private static final Logger LOGGER = Logger.getLogger(LogDebugUnkownElement.class);

  @Override
  public <T extends EObject> void beginUnknownElementInterpretation(final ModelElementPassedEvent<T> event) {
    logEvent(event, LOGGER);
  }

  @Override
  public <T extends EObject> void endUnknownElementInterpretation(final ModelElementPassedEvent<T> event) {
    logEvent(event, LOGGER);
  }
}