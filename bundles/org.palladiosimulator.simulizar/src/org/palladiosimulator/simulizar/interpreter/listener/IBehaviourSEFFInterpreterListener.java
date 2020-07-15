package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.seff.ExternalCallAction;

public interface IBehaviourSEFFInterpreterListener {
  public default void beginExternalCallInterpretation(RDSEFFElementPassedEvent<ExternalCallAction> event) {

  }

  public default void endExternalCallInterpretation(RDSEFFElementPassedEvent<ExternalCallAction> event) {

  }
}