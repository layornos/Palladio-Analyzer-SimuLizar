package org.palladiosimulator.simulizar.interpreter.listener;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.simulizar.reconfiguration.Reconfigurator;
import org.palladiosimulator.simulizar.utils.PCMPartitionManager;

import de.uka.ipd.sdq.simucomframework.model.SimuComModel;

public abstract class AbstractProbeUnkownElementListener extends AbstractProbeFrameworkListener implements IUnknownElementInterpretation {


	public AbstractProbeUnkownElementListener(PCMPartitionManager pcmPartitionManager, SimuComModel simuComModel,
			Reconfigurator reconfigurator) {
		super(pcmPartitionManager, simuComModel, reconfigurator);
	}
    @Override
    public <T extends EObject> void beginUnknownElementInterpretation(final ModelElementPassedEvent<T> event) {
    }

    @Override
    public <T extends EObject> void endUnknownElementInterpretation(final ModelElementPassedEvent<T> event) {
    }

}
