package org.palladiosimulator.simulizar.interpreter.listener;

import org.eclipse.emf.ecore.EObject;

public class LogDebugUnkownElement implements IUnknownElementInterpretation {
  
  @Override
  public <T extends EObject> void beginUnknownElementInterpretation(final ModelElementPassedEvent<T> event) {
    ILogDebugListener.logEvent(event);
  }

  @Override
  public <T extends EObject> void endUnknownElementInterpretation(final ModelElementPassedEvent<T> event) {
    ILogDebugListener.logEvent(event);
  }
}