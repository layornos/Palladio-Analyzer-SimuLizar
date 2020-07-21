package org.palladiosimulator.simulizar.elasticity.jobs;

import static org.palladiosimulator.metricspec.constants.MetricDescriptionConstants.RECONFIGURATION_TIME_METRIC_TUPLE;

import org.apache.log4j.Logger;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.probes.Probe;
import org.palladiosimulator.simulizar.elasticity.aggregator.ReconfigurationTimeAggregatorWithConfidence;
import org.palladiosimulator.simulizar.interpreter.listener.ProbeFrameworkUsageInterpreterListener;
import org.palladiosimulator.simulizar.reconfiguration.Reconfigurator;
import org.palladiosimulator.simulizar.reconfiguration.probes.TakeReconfigurationDurationProbe;
import org.palladiosimulator.simulizar.utils.PCMPartitionManager;

import de.uka.ipd.sdq.simucomframework.model.SimuComModel;
import de.uka.ipd.sdq.simucomframework.resources.CalculatorHelper;
import de.uka.ipd.sdq.statistics.StaticBatchAlgorithm;
import de.uka.ipd.sdq.statistics.estimation.SampleMeanEstimator;

public class ProbeFrameworkUsageListenerForElasticity extends ProbeFrameworkUsageInterpreterListener implements IListenerForElasticity  {
	private static final Logger LOGGER = Logger.getLogger(ProbeFrameworkUsageListenerForElasticity.class);

	public ProbeFrameworkUsageListenerForElasticity(PCMPartitionManager pcmPartitionManager, SimuComModel simuComModel,
			Reconfigurator reconfigurator) {
		super(pcmPartitionManager, simuComModel, reconfigurator);
	}
	@Override
	protected void initReconfigurationTimeMeasurement() {
		for (final MeasurementSpecification reconfigurationTimeMeasurementSpec : this
				.getMeasurementSpecificationsForMetricDescription(
						MetricDescriptionConstants.RECONFIGURATION_TIME_METRIC)) {
			final MeasuringPoint measuringPoint = reconfigurationTimeMeasurementSpec.getMonitor().getMeasuringPoint();
			final Probe probe = CalculatorHelper.getEventProbeSetWithCurrentTime(RECONFIGURATION_TIME_METRIC_TUPLE,
					this.getSimuComModel().getSimulationControl(),
					new TakeReconfigurationDurationProbe(reconfigurator));
			try {
				final Calculator calculator = this.calculatorFactory
				        .buildCalculator(RECONFIGURATION_TIME_METRIC_TUPLE, measuringPoint, 
				                DefaultCalculatorProbeSets.createSingularProbeConfiguration(probe));
				calculator.addObserver(RunElasticityAnalysisJob.aggregatorWithConfidence == null ? RunElasticityAnalysisJob.aggregatorWithConfidence = new ReconfigurationTimeAggregatorWithConfidence(
																							new StaticBatchAlgorithm(5, 5),
																							new SampleMeanEstimator(), 
																							this.getSimuComModel().getConfiguration().getConfidenceLevel() / RunElasticityAnalysisJob.ONE_HUNDERT_PERCENT,
																							this.getSimuComModel().getConfiguration().getConfidenceHalfWidth() / RunElasticityAnalysisJob.ONE_HUNDERT_PERCENT) 
																		: RunElasticityAnalysisJob.aggregatorWithConfidence);
			} catch (IllegalArgumentException iae) {
				LOGGER.info("Tried to add a calculator that already exists");
			}
		}
	}
}
