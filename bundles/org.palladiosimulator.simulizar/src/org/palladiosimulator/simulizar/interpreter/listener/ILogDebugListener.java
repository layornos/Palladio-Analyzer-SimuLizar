package org.palladiosimulator.simulizar.interpreter.listener;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;



/**
 * @author snowball
 *
 */
public interface ILogDebugListener extends IListener {


   public default <T extends EObject> void logEvent(final ModelElementPassedEvent<T> event, Logger LOGGER) {
        if (LOGGER.isDebugEnabled()) {
            final StringBuilder msgBuilder = new StringBuilder();
            switch (event.getEventType()) {
            case BEGIN:
                msgBuilder.append("Starting to interpret ");
                break;
            case END:
                msgBuilder.append("Finished interpreting ");
            default:
                msgBuilder.append("Unknown event ");
                break;
            }
            msgBuilder.append(event.getModelElement().eClass().getName());
            msgBuilder.append(" in Simuation Thread \"");
            msgBuilder.append(event.getThread().getId());
            msgBuilder.append("\" at simulation time ");
            msgBuilder.append(event.getPassageTime());
            LOGGER.debug(msgBuilder.toString());
        }
    }

}
