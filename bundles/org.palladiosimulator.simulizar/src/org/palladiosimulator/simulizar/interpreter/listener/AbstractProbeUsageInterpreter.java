package org.palladiosimulator.simulizar.interpreter.listener;

import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.simulizar.reconfiguration.Reconfigurator;
import org.palladiosimulator.simulizar.utils.PCMPartitionManager;

import de.uka.ipd.sdq.simucomframework.model.SimuComModel;

public abstract class AbstractProbeUsageInterpreter extends AbstractProbeFrameworkListener
		implements IUsageModelInterpreterListener {


	public AbstractProbeUsageInterpreter(PCMPartitionManager pcmPartitionManager, SimuComModel simuComModel,
			Reconfigurator reconfigurator) {
		super(pcmPartitionManager, simuComModel, reconfigurator);
	}

	    /*
	     * (non-Javadoc)
	     *
	     * @see de.upb.pcm.interpreter.interpreter.listener.AbstractInterpreterListener#
	     * beginUsageScenarioInterpretation
	     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
	     */
	    @Override
	    public void beginUsageScenarioInterpretation(final ModelElementPassedEvent<UsageScenario> event) {
	        this.startMeasurement(event);
	    }

	    /*
	     * (non-Javadoc)
	     *
	     * @see de.upb.pcm.interpreter.interpreter.listener.AbstractInterpreterListener#
	     * endUsageScenarioInterpretation
	     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
	     */
	    @Override
	    public void endUsageScenarioInterpretation(final ModelElementPassedEvent<UsageScenario> event) {
	        this.endMeasurement(event);
	    }
	    /*
	     * (non-Javadoc)
	     *
	     * @see de.upb.pcm.interpreter.interpreter.listener.AbstractInterpreterListener#
	     * beginEntryLevelSystemCallInterpretation
	     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
	     */
	    @Override
	    public void beginEntryLevelSystemCallInterpretation(final ModelElementPassedEvent<EntryLevelSystemCall> event) {
	        this.startMeasurement(event);
	    }

	    /*
	     * (non-Javadoc)
	     *
	     * @see de.upb.pcm.interpreter.interpreter.listener.AbstractInterpreterListener#
	     * endEntryLevelSystemCallInterpretation
	     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
	     */
	    @Override
	    public void endEntryLevelSystemCallInterpretation(final ModelElementPassedEvent<EntryLevelSystemCall> event) {
	        this.endMeasurement(event);
	    }

}
