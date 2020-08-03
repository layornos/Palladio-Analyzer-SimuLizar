package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.simulizar.reconfiguration.Reconfigurator;
import org.palladiosimulator.simulizar.utils.PCMPartitionManager;

import de.uka.ipd.sdq.simucomframework.model.SimuComModel;

public abstract class AbstractProbeRepositoryListener extends AbstractProbeFrameworkListener implements IRepositoryInterpreterListener {
    

	public AbstractProbeRepositoryListener(PCMPartitionManager pcmPartitionManager, SimuComModel simuComModel,
			Reconfigurator reconfigurator) {
		super(pcmPartitionManager, simuComModel, reconfigurator);
	}
    @Override
    public void beginSystemOperationCallInterpretation(final ModelElementPassedEvent<OperationSignature> event) {
        if (this.currentTimeProbes.containsKey(((Entity) event.getModelElement()).getId())
                && this.simulationIsRunning()) {
            this.currentTimeProbes.get(((Entity) event.getModelElement()).getId()).get(START_PROBE_INDEX)
                    .takeMeasurement(event.getThread().getRequestContext());
        }
    }

    @Override
    public void endSystemOperationCallInterpretation(final ModelElementPassedEvent<OperationSignature> event) {
        if (this.currentTimeProbes.containsKey(((Entity) event.getModelElement()).getId())
                && this.simulationIsRunning()) {
            this.currentTimeProbes.get(((Entity) event.getModelElement()).getId()).get(STOP_PROBE_INDEX)
                    .takeMeasurement(event.getThread().getRequestContext());
        }
    }

}
