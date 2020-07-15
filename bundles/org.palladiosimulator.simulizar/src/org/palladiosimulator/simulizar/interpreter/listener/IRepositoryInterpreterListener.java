package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;

public interface IRepositoryInterpreterListener  {
  public default void beginSystemOperationCallInterpretation(
      ModelElementPassedEvent<OperationSignature> event) {

  }

  public default void endSystemOperationCallInterpretation(
      ModelElementPassedEvent<OperationSignature> event) {

  }

  public default <R extends ProvidedRole, S extends Signature> void beginAssemblyProvidedOperationCallInterpretation(
      AssemblyProvidedOperationPassedEvent<R, S> event) {

  }

  public default <R extends ProvidedRole, S extends Signature> void endAssemblyProvidedOperationCallInterpretation(
      AssemblyProvidedOperationPassedEvent<R, S> event) {
  }
}
