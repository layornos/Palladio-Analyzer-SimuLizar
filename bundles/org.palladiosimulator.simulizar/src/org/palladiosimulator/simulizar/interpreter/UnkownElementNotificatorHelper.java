package org.palladiosimulator.simulizar.interpreter;

import java.util.Optional;
import org.palladiosimulator.commons.designpatterns.AbstractObservable;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;
import org.palladiosimulator.simulizar.interpreter.listener.IUnknownElementInterpretation;
import org.eclipse.emf.ecore.EObject;
import java.util.function.Consumer;

public class UnkownElementNotificatorHelper  extends AbstractObservable<IUnknownElementInterpretation> implements IObservableNotificationHelper {

  public void registerObserver(Object observer) {
    if(IUnknownElementInterpretation.class.isAssignableFrom(observer.getClass()){
    this.addObserver(observer);
    }
  }

  private Consumer<ModelElementPassedEvent<? extends EObject>> UNKNOWN_ELEMENT_NOTIFICATOR_SELECTOR =
  new BeginEndSwitch().apply(
    ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().beginUnknownElementInterpretation(ev)),
    ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().endUnknownElementInterpretation(ev))
    );

    public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>> doSwitch(EObject theEObject) {
      return Optional.of(UNKNOWN_ELEMENT_NOTIFICATOR_SELECTOR);
      }

}