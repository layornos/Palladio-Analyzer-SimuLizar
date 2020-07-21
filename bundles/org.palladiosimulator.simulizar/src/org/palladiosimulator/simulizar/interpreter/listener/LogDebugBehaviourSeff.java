package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.seff.ExternalCallAction;

public class LogDebugBehaviourSeff implements IBehaviourSEFFInterpreterListener {
  
  @Override
  public  void beginExternalCallInterpretation(RDSEFFElementPassedEvent<ExternalCallAction> event) {
    ILogDebugListener.logEvent(event);
  }
  @Override
  public  void endExternalCallInterpretation(RDSEFFElementPassedEvent<ExternalCallAction> event) {
    ILogDebugListener.logEvent(event);
  }

}