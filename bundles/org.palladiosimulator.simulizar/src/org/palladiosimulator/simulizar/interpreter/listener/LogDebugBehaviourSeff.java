package org.palladiosimulator.simulizar.interpreter.listener;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.seff.ExternalCallAction;

public class LogDebugBehaviourSeff implements IBehaviourSEFFInterpreterListener, ILogDebugListener {
  
    private static final Logger LOGGER = Logger.getLogger(LogDebugBehaviourSeff.class);

  @Override
  public  void beginExternalCallInterpretation(RDSEFFElementPassedEvent<ExternalCallAction> event) {
	  logEvent(event, LOGGER);
  }
  @Override
  public  void endExternalCallInterpretation(RDSEFFElementPassedEvent<ExternalCallAction> event) {
	  logEvent(event, LOGGER);
  }

}