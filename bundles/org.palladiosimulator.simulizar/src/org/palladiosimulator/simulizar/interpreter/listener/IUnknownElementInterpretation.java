package org.palladiosimulator.simulizar.interpreter.listener;

import org.eclipse.emf.ecore.EObject;

public interface IUnknownElementInterpretation  {
  
  public default <T extends EObject>  void beginUnknownElementInterpretation(ModelElementPassedEvent<T> event) {

  }

  public default <T extends EObject>  void endUnknownElementInterpretation(ModelElementPassedEvent<T> event) {

  }
}