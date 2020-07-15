package org.palladiosimulator.simulizar.interpreter;

import java.util.Optional;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.commons.designpatterns.AbstractObservable;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;
import java.util.function.Consumer;
import org.palladiosimulator.simulizar.interpreter.BeginEndSwitch;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;
import org.palladiosimulator.simulizar.interpreter.listener.RDSEFFElementPassedEvent;
import org.palladiosimulator.simulizar.interpreter.listener.IBehaviourSEFFInterpreterListener;

public class SeffNotificatorHelper extends AbstractObservable<IBehaviourSEFFInterpreterListener> {

  private SeffSwitch<Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>> SEFF_NOTIFICATOR_SELECTOR = new SeffSwitch<Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>> () {
    public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>> caseExternalCallAction(ExternalCallAction object) {
      return Optional.of(
          new BeginEndSwitch().apply(
            ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().beginExternalCallInterpretation((RDSEFFElementPassedEvent<ExternalCallAction>) ev)),
            ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().endExternalCallInterpretation((RDSEFFElementPassedEvent<ExternalCallAction>) ev))
          )
          );		
      };
    
    public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>> defaultCase(EObject object) {
      return Optional.empty();
  };
  };
  /**
   * @return the sEFF_NOTIFICATOR_SELECTOR
   */
  public SeffSwitch<Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>> getSeffNotificatorSelector() {
    return SEFF_NOTIFICATOR_SELECTOR;
  }
}