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

import org.eclipse.emf.ecore.EObject;

/**
 * @author snowball, Sebastian Krach
 *
 */
public class EventNotificationHelper {
		private final List<Runnable> removeObservable = new ArrayList<>();
		private final List<Function<EObject, Optional<Consumer<ModelElementPassedEvent<? extends EObject>>>>> NOTIFICATOR_SELECTOR_SWITCHES = new ArrayList<>();
		public EventNotificationHelper() {
			UsageModelNotificationHelper helper1 = new UsageModelNotificationHelper();	
			RepositoryNotificationHelper helper2 = new RepositoryNotificationHelper();	
			SeffNotificatorHelper helper3 = new SeffNotificatorHelper(); 				
			removeObservable.add(helper1::removeAllObserver);
			removeObservable.add(helper2::removeAllObserver);
			removeObservable.add(helper3::removeAllObserver);
			NOTIFICATOR_SELECTOR_SWITCHES.add(helper1.getUsageMModelNotificatorSelector()::doSwitch);
			NOTIFICATOR_SELECTOR_SWITCHES.add(helper2.getRepositoryNotificatorSelector()::doSwitch);
			NOTIFICATOR_SELECTOR_SWITCHES.add(helper3.getSeffNotificatorSelector()::doSwitch);

		}
		
    
    public <T extends EObject> void firePassedEvent(final ModelElementPassedEvent<T> event) {
		NOTIFICATOR_SELECTOR_SWITCHES.stream()
			.map(sw -> sw.apply(event.getModelElement()))
			.filter(Optional::isPresent).map(Optional::get).findFirst()
			.orElse(new UnkownElementNotificatorListener().getUnkownElementNotificatorSelector())
			.accept(event);
    }
    
    public void removeAllListener() {
			removeObservable.forEach(v->v.run());
	}
}
