/**
 *
 */
package org.palladiosimulator.simulizar.interpreter;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;
import org.palladiosimulator.commons.designpatterns.AbstractObservable;
import org.palladiosimulator.simulizar.interpreter.listener.IUsageModelInterpreterListener;
import org.palladiosimulator.simulizar.interpreter.listener.IUnknownElementInterpretation;
import org.palladiosimulator.simulizar.interpreter.listener.IRepositoryInterpreterListener;
import org.palladiosimulator.simulizar.interpreter.listener.IBehaviourSEFFInterpreterListener;

import org.eclipse.emf.ecore.EObject;

/**
 * @author snowball, Sebastian Krach
 *
 */
public class EventNotificationHelper {
		private List<IObservableNotificationHelper> observableNotificationHelper = new ArrayList<>();
		private IObservableNotificationHelper helper1 = new UsageModelNotificationHelper();	
		private IObservableNotificationHelper helper2 = new RepositoryNotificationHelper();	
		private IObservableNotificationHelper helper3 = new SeffNotificatorHelper();
		private IObservableNotificationHelper unkownElementNotificatorHelper = new UnkownElementNotificatorHelper();

		public EventNotificationHelper() {
			//TODO: wegen guice entsorgen @MartinWitt
			observableNotificationHelper.add(helper1);
			observableNotificationHelper.add(helper2);
			observableNotificationHelper.add(helper3);
		}
		public EventNotificationHelper(List<IObservableNotificationHelper> notificationHelper) {
			observableNotificationHelper = notificationHelper;

		}
    
    public <T extends EObject> void firePassedEvent(final ModelElementPassedEvent<T> event) {
			observableNotificationHelper.stream()
			.map(sw -> sw.doSwitch(event.getModelElement()))
			.filter(Optional::isPresent).map(Optional::get).findFirst()
			.orElse(unkownElementNotificatorHelper.doSwitch(event.getModelElement()).get())
			.accept(event);
    }
    
    public void removeAllListener() {
			observableNotificationHelper.forEach(v-> v.removeAllObserver());
			unkownElementNotificatorHelper.removeAllObserver();
	}
	public void addObserver(Object observer){
		observableNotificationHelper.forEach(v-> v.registerObserver(observer));
	}

}
