package org.palladiosimulator.simulizar.interpreter;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;
import org.palladiosimulator.simulizar.interpreter.listener.IRepositoryInterpreterListener;
import org.palladiosimulator.commons.designpatterns.AbstractObservable;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.simulizar.interpreter.BeginEndSwitch;
import org.palladiosimulator.pcm.repository.util.RepositorySwitch;
import org.palladiosimulator.simulizar.interpreter.listener.AssemblyProvidedOperationPassedEvent;

public class RepositoryNotificationHelper extends AbstractObservable<IRepositoryInterpreterListener> implements IObservableNotificationHelper {

  public void registerObserver(IRepositoryInterpreterListener observer) {
    this.addObserver(observer);
  }

  private RepositorySwitch<Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>> REPOSITORY_NOTIFICATOR_SELECTOR = new RepositorySwitch<Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>>() {
    public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>> caseOperationSignature(OperationSignature object) {
      return Optional.of(    
        new BeginEndSwitch().apply(
          ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().beginSystemOperationCallInterpretation((ModelElementPassedEvent<OperationSignature>) ev)),
          ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().endSystemOperationCallInterpretation((ModelElementPassedEvent<OperationSignature>) ev))
        )
      );		
  };
  
  public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>> caseOperationProvidedRole(OperationProvidedRole object) {
      return Optional.of(    
        new BeginEndSwitch().apply(
          ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().beginAssemblyProvidedOperationCallInterpretation((AssemblyProvidedOperationPassedEvent<OperationProvidedRole, OperationSignature>) ev)),
          ((ModelElementPassedEvent<? extends EObject> ev) -> getEventDispatcher().endAssemblyProvidedOperationCallInterpretation((AssemblyProvidedOperationPassedEvent<OperationProvidedRole, OperationSignature>) ev))
        )
      );	
  };
  
  public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>> defaultCase(EObject object) {
      return Optional.empty();
  };
  };

  public Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>  doSwitch(EObject theEObject) {
    return REPOSITORY_NOTIFICATOR_SELECTOR.doSwitch(theEObject);
  }
}