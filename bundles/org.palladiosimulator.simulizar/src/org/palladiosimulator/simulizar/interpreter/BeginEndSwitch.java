package org.palladiosimulator.simulizar.interpreter;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;

import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simulizar.interpreter.listener.EventType;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch;

public class BeginEndSwitch implements BinaryOperator<Consumer<ModelElementPassedEvent<? extends EObject>>> {
  
  @Override
  public Consumer<ModelElementPassedEvent<? extends EObject>> apply(Consumer<ModelElementPassedEvent<? extends EObject>> beginFunc, Consumer<ModelElementPassedEvent<? extends EObject>> endFunc) {
    return  ((ModelElementPassedEvent<? extends EObject> event) -> {
      if (event.getEventType() == EventType.BEGIN) 
        beginFunc.accept(event);
      else
        endFunc.accept(event);
      
  });
  }

}