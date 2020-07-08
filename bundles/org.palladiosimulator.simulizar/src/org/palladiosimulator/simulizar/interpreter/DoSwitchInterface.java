package org.palladiosimulator.simulizar.interpreter;

import org.eclipse.emf.ecore.EObject;

public interface DoSwitchInterface<T> {
  public T doSwitch(EObject theEObject);
}