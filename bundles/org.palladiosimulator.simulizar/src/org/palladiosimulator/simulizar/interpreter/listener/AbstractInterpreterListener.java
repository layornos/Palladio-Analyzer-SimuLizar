/**
 *
 */
package org.palladiosimulator.simulizar.interpreter.listener;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

/**
 * @author snowball
 *
 */
public abstract class AbstractInterpreterListener {

    /**
     *
     */
    public AbstractInterpreterListener() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.upb.pcm.interpreter.interpreter.listener.IInterpreterListener#
     * beginUsageScenarioInterpretation
     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
     */
    
    public void beginUsageScenarioInterpretation(final ModelElementPassedEvent<UsageScenario> event) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.upb.pcm.interpreter.interpreter.listener.IInterpreterListener#
     * endUsageScenarioInterpretation
     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
     */
    
    public void endUsageScenarioInterpretation(final ModelElementPassedEvent<UsageScenario> event) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.upb.pcm.interpreter.interpreter.listener.IInterpreterListener#
     * beginEntryLevelSystemCallInterpretation
     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
     */
    
    public void beginEntryLevelSystemCallInterpretation(final ModelElementPassedEvent<EntryLevelSystemCall> event) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.upb.pcm.interpreter.interpreter.listener.IInterpreterListener#
     * endEntryLevelSystemCallInterpretation
     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
     */
    
    public void endEntryLevelSystemCallInterpretation(final ModelElementPassedEvent<EntryLevelSystemCall> event) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.upb.pcm.simulizar.interpreter.listener.IInterpreterListener#
     * beginExternalCallInterpretation
     * (de.upb.pcm.simulizar.interpreter.listener.ModelElementPassedEvent)
     */
    
    public void beginExternalCallInterpretation(final RDSEFFElementPassedEvent<ExternalCallAction> event) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.upb.pcm.simulizar.interpreter.listener.IInterpreterListener#endExternalCallInterpretation
     * (de.upb.pcm.simulizar.interpreter.listener.ModelElementPassedEvent)
     */
    
    public void endExternalCallInterpretation(final RDSEFFElementPassedEvent<ExternalCallAction> event) {
    }
    
	
	public <T extends EObject> void beginUnknownElementInterpretation(ModelElementPassedEvent<T> event) {
	}

	
	public <T extends EObject> void endUnknownElementInterpretation(ModelElementPassedEvent<T> event) {
	}

	
	public void beginSystemOperationCallInterpretation(ModelElementPassedEvent<OperationSignature> event) {
	}

	
	public void endSystemOperationCallInterpretation(ModelElementPassedEvent<OperationSignature> event) {
	}


    
    public <R extends ProvidedRole, S extends Signature> void beginAssemblyProvidedOperationCallInterpretation(
    		AssemblyProvidedOperationPassedEvent<R, S> event) {
    }
    
    
    public <R extends ProvidedRole, S extends Signature> void endAssemblyProvidedOperationCallInterpretation(
    		AssemblyProvidedOperationPassedEvent<R, S> event) {
    }
}
