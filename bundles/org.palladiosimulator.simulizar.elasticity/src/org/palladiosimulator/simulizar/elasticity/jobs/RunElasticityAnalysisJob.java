package org.palladiosimulator.simulizar.elasticity.jobs;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.commons.eclipseutils.ExtensionHelper;
import org.palladiosimulator.simulizar.elasticity.aggregator.ReconfigurationTimeAggregatorWithConfidence;
import org.palladiosimulator.simulizar.interpreter.listener.ILogDebugListener;
import org.palladiosimulator.simulizar.launcher.IConfigurator;
import org.palladiosimulator.simulizar.launcher.SimulizarConstants;
import org.palladiosimulator.simulizar.launcher.jobs.LoadSimuLizarModelsIntoBlackboardJob;
import org.palladiosimulator.simulizar.reconfiguration.Reconfigurator;
import org.palladiosimulator.simulizar.runconfig.SimuLizarWorkflowConfiguration;
import org.palladiosimulator.simulizar.runtimestate.AbstractSimuLizarRuntimeState;
import org.palladiosimulator.simulizar.runtimestate.IRuntimeStateAccessor;
import org.palladiosimulator.simulizar.runtimestate.SimulationCancelationDelegate;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class RunElasticityAnalysisJob implements IBlackboardInteractingJob<MDSDBlackboard> {
	private static final Logger LOGGER = Logger.getLogger(RunElasticityAnalysisJob.class.getName());

	private MDSDBlackboard blackboard;

	private final SimuLizarWorkflowConfiguration configuration;

	static ReconfigurationTimeAggregatorWithConfidence aggregatorWithConfidence;
	

	static int NUMBER_OF_RUNS_LIMIT = 50;

	static final double ONE_HUNDERT_PERCENT = 100.0;

	private LoadSimuLizarModelsIntoBlackboardJob loadSimuLizarModelsIntoBlackboardJob;
	
	private List<ILogDebugListener> logDebugListener;
	private List<IListenerForElasticity> probeFrameworkListeners;
	//TODO: GUICE @MartinWitt
	/**
	 * Constructor
	 *
	 * @param configuration
	 *            the SimuCom workflow configuration.
	 */
	public RunElasticityAnalysisJob(final SimuLizarWorkflowConfiguration configuration,
			LoadSimuLizarModelsIntoBlackboardJob loadSimuLizarModelsIntoBlackboardJob) {
		super();
		this.configuration = configuration;
		this.loadSimuLizarModelsIntoBlackboardJob = loadSimuLizarModelsIntoBlackboardJob;
		aggregatorWithConfidence = null;
	}

	/**
	 * @see de.uka.ipd.sdq.workflow.IJob#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		int numberOfRuns = 0;
		while ((aggregatorWithConfidence == null || !aggregatorWithConfidence.isConfidenceReached()) && numberOfRuns++ < NUMBER_OF_RUNS_LIMIT) {
			LOGGER.info("Elasticity analysis, run No. " + numberOfRuns);

			LOGGER.info("Start job: " + this);

			LOGGER.info("Initialise Simulizar runtime state");

			final List<IConfigurator> configurators = ExtensionHelper.getExecutableExtensions(
					SimulizarConstants.CONFIGURATOR_EXTENSION_POINT_ID,
					SimulizarConstants.CONFIGURATOR_EXTENSION_POINT_ATTRIBUTE);
			this.setBlackboard(new MDSDBlackboard());
			loadSimuLizarModelsIntoBlackboardJob.setBlackboard(this.blackboard);
			loadSimuLizarModelsIntoBlackboardJob.execute(monitor);
			for (final IConfigurator configurator : configurators) {
				configurator.configure(this.configuration, this.blackboard);
			}

			this.configuration.setReconfigurationRulesFolder(this.configuration.getReconfigurationRulesFolder());

			// FIXME @Igor: Use ModelAccess instead of
			// ModelAccessUseOriginalReferences.
			// After we find a way to copy models so that their links do not
			// point to intermediary, but
			// to the models directly.
			final AbstractSimuLizarRuntimeState runtimeState = new SimuLizarRuntimeStateElasticity(this.configuration, this.blackboard,
					new SimulationCancelationDelegate(monitor::isCanceled));
			this.initializeRuntimeStateAccessors(runtimeState);
			runtimeState.runSimulation();
			runtimeState.cleanUp();
			LOGGER.info("finished job: " + this);
		}
	}

	private void initializeRuntimeStateAccessors(final AbstractSimuLizarRuntimeState runtimeState) {
		final Iterable<IRuntimeStateAccessor> stateAccessors = ExtensionHelper.getExecutableExtensions(
				SimulizarConstants.RUNTIME_STATE_ACCESS_EXTENSION_POINT_ID,
				SimulizarConstants.RUNTIME_STATE_ACCESS_EXTENSION_POINT_ACCESSOR_ATTRIBUTE);

		for (final IRuntimeStateAccessor accessor : stateAccessors) {
			accessor.setRuntimeStateModel(runtimeState);
		}
	}

	/**
	 * @see de.uka.ipd.sdq.workflow.IJob#getName()
	 */
	@Override
	public String getName() {
		return "Run SimuLizar Elasticity Analysis";
	}

	/**
	 * @see de.uka.ipd.sdq.workflow.IJob#rollback(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {
	}

	/**
	 * @see de.uka.ipd.sdq.workflow.IBlackboardInteractingJob#setBlackboard(de.uka.ipd.sdq.workflow.Blackboard)
	 */
	@Override
	public void setBlackboard(final MDSDBlackboard blackboard) {
		this.blackboard = blackboard;
	}
	
	private class SimuLizarRuntimeStateElasticity extends AbstractSimuLizarRuntimeState {
		
		public SimuLizarRuntimeStateElasticity(SimuLizarWorkflowConfiguration configuration, MDSDBlackboard blackboard, final SimulationCancelationDelegate cancelationDelegate) {
			super(configuration, blackboard, cancelationDelegate);
		}

		@Override
		protected void initializeInterpreterListeners(Reconfigurator reconfigurator) {
			LOGGER.debug("Adding Debug and monitoring interpreter listeners");
	        logDebugListener.forEach(eventHelper::addObserver);
	        probeFrameworkListeners.forEach(eventHelper::addObserver);
		}

	}
	
	
}
