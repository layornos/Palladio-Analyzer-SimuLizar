package org.palladiosimulator.simulizar.interpreter;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;
import org.palladiosimulator.simulizar.interpreter.BeginEndSwitch;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch;
import org.palladiosimulator.commons.designpatterns.AbstractObservable;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.simulizar.interpreter.listener.IUsageModelInterpreterListener;
public class UsageModelNotificationHelper extends AbstractObservable<IUsageModelInterpreterListener> implements IObservableNotificationHelper {

  public void registerObserver(Object observer) {
    if(IUsageModelInterpreterListener.class.isAssignableFrom(observer.getClass())) {
    this.addObserver((IUsageModelInterpreterListener) observer);
    }
  }
  

      private UsagemodelSwitch<Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>> USAGE_MODEL_NOTIFICATOR_SELECTOR = new UsagemodelSwitch<Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>>() {
        public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>> caseEntryLevelSystemCall(EntryLevelSystemCall object) {
          return Optional.of(
              new BeginEndSwitch().apply(
                (ev -> getEventDispatcher().beginEntryLevelSystemCallInterpretation((ModelElementPassedEvent<EntryLevelSystemCall>) ev)),
                (ev -> getEventDispatcher().endEntryLevelSystemCallInterpretation((ModelElementPassedEvent<EntryLevelSystemCall>) ev))
            )
            );
        };
        
        public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>> caseUsageScenario(UsageScenario object) {
            return Optional.of(
            new BeginEndSwitch().apply(
                ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().beginUsageScenarioInterpretation((ModelElementPassedEvent<UsageScenario>) ev)),
                ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().endUsageScenarioInterpretation((ModelElementPassedEvent<UsageScenario>) ev))
              )
            );
                  
        };
        
        public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>> defaultCase(EObject object) {
            return Optional.empty();
        };
        };

        public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>  doSwitch(EObject theEObject) {
          return USAGE_MODEL_NOTIFICATOR_SELECTOR.doSwitch(theEObject);
        }
        
}
