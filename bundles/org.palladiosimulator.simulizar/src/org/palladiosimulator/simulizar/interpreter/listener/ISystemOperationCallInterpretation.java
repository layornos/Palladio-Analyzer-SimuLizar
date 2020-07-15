package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.repository.OperationSignature;

public interface ISystemOperationCallInterpretation {
  public default void beginSystemOperationCallInterpretation(ModelElementPassedEvent<OperationSignature> event) {

  }

  public default void endSystemOperationCallInterpretation(ModelElementPassedEvent<OperationSignature> event) {
    
  }
}