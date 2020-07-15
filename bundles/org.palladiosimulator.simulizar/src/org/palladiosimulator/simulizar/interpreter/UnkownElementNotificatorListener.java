package org.palladiosimulator.simulizar.interpreter;

import org.palladiosimulator.commons.designpatterns.AbstractObservable;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;
import org.palladiosimulator.simulizar.interpreter.listener.IUnknownElementInterpretation;
import org.eclipse.emf.ecore.EObject;
import java.util.function.Consumer;

public class UnkownElementNotificatorListener  extends AbstractObservable<IUnknownElementInterpretation> {

  private Consumer<ModelElementPassedEvent<? extends EObject>> UNKNOWN_ELEMENT_NOTIFICATOR_SELECTOR =
  new BeginEndSwitch().apply(
    ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().beginUnknownElementInterpretation(ev)),
    ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().endUnknownElementInterpretation(ev))
    );
    /**
     * @return the uNKNOWN_ELEMENT_NOTIFICATOR_SELECTOR
     */
    public Consumer<ModelElementPassedEvent<? extends EObject>> getUnkownElementNotificatorSelector() {
      return UNKNOWN_ELEMENT_NOTIFICATOR_SELECTOR;
    }
}