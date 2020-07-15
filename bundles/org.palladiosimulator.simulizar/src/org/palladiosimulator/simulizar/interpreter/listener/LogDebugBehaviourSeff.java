package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.simulizar.interpreter.listener.IBehaviourSEFFInterpreterListener;
import org.palladiosimulator.pcm.seff.ExternalCallAction;

public class LogDebugBehaviourSeff implements IBehaviourSEFFInterpreterListener {
  
  @Override
  public  void beginExternalCallInterpretation(RDSEFFElementPassedEvent<ExternalCallAction> event) {
    LogDebugListener.logEvent(event);
  }
  @Override
  public  void endExternalCallInterpretation(RDSEFFElementPassedEvent<ExternalCallAction> event) {
    LogDebugListener.logEvent(event);
  }

}