package org.palladiosimulator.simulizar.interpreter;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;

public interface IObservableNotificationHelper  {

/**
 * TODO: schreib mal doch @MartinWitt
 * Das teil dient dazu, wegen überladung die observer zu verteilen.
 * @param observer
 */
  public void registerObserver(Object observer);

  public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>  doSwitch(EObject theEObject);

  public void removeAllObserver();
}