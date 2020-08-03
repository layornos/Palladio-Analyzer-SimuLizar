package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.simulizar.reconfiguration.Reconfigurator;
import org.palladiosimulator.simulizar.utils.PCMPartitionManager;

import de.uka.ipd.sdq.simucomframework.model.SimuComModel;

public  abstract class AbstractProbeBehaviourSeffListener extends AbstractProbeFrameworkListener
		implements IBehaviourSEFFInterpreterListener {

	public AbstractProbeBehaviourSeffListener(PCMPartitionManager pcmPartitionManager, SimuComModel simuComModel,
			Reconfigurator reconfigurator) {
		super(pcmPartitionManager, simuComModel, reconfigurator);
	}
    /*
     * (non-Javadoc)
     *
     * @see de.upb.pcm.simulizar.interpreter.listener.AbstractInterpreterListener#
     * beginExternalCallInterpretation
     * (de.upb.pcm.simulizar.interpreter.listener.ModelElementPassedEvent)
     */
    @Override
    public void beginExternalCallInterpretation(final RDSEFFElementPassedEvent<ExternalCallAction> event) {
        this.startMeasurement(event);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.upb.pcm.simulizar.interpreter.listener.AbstractInterpreterListener#
     * endExternalCallInterpretation
     * (de.upb.pcm.simulizar.interpreter.listener.ModelElementPassedEvent)
     */
    @Override
    public void endExternalCallInterpretation(final RDSEFFElementPassedEvent<ExternalCallAction> event) {
        this.endMeasurement(event);
    }

}
