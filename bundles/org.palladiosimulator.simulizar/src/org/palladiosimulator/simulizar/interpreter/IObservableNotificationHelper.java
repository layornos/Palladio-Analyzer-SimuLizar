package org.palladiosimulator.simulizar.interpreter;

import java.util.List;
import java.util.function.Function;
import org.eclipse.emf.ecore.EObject;
import java.util.function.Consumer;
import java.util.Optional;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;

public interface IObservableNotificationHelper  {

/**
 * TODO: schreib mal doch @MartinWitt
 * Das teil dient dazu, wegen überladung die observer zu verteilen.
 * @param observer
 */
  public default void registerObserver(Object observer) {

  }

  public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>  doSwitch(EObject theEObject);

  public void removeAllObserver();
}