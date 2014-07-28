package org.palladiosimulator.simulizar.syncer;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notification;
import org.palladiosimulator.simulizar.access.IModelAccess;
import org.palladiosimulator.simulizar.metrics.ResourceStateListener;
import org.palladiosimulator.simulizar.pms.MeasurementSpecification;
import org.palladiosimulator.simulizar.pms.PMSModel;
import org.palladiosimulator.simulizar.pms.PerformanceMetricEnum;
import org.palladiosimulator.simulizar.prm.PRMModel;
import org.palladiosimulator.simulizar.utils.PMSUtil;

import de.uka.ipd.sdq.pcm.resourceenvironment.ProcessingResourceSpecification;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceContainer;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceEnvironment;
import de.uka.ipd.sdq.pcm.resourcetype.SchedulingPolicy;
import de.uka.ipd.sdq.simucomframework.ModelsAtRuntime;
import de.uka.ipd.sdq.simucomframework.model.SimuComModel;
import de.uka.ipd.sdq.simucomframework.resources.AbstractScheduledResource;
import de.uka.ipd.sdq.simucomframework.resources.AbstractSimulatedResourceContainer;
import de.uka.ipd.sdq.simucomframework.resources.ScheduledResource;
import de.uka.ipd.sdq.simucomframework.resources.SchedulingStrategy;
import de.uka.ipd.sdq.simucomframework.resources.SimulatedResourceContainer;

/**
 * Class to sync resource environment model with SimuCom. UGLY DRAFT!
 * 
 * @author Joachim Meyer, Sebastian Lehrig
 */
public class ResourceEnvironmentSyncer
        extends AbstractSyncer<ResourceEnvironment>
        implements IModelSyncer
{

    private static final Logger LOG = Logger.getLogger(ResourceEnvironmentSyncer.class.getName());
    private final PMSModel pms;
    private final PRMModel prm;

    /**
     * Constructor
     * 
     * @param simuComModel
     *            the SimuCom model.
     * @param modelAccessFactory
     *            the modelAccessFactory.
     */
    public ResourceEnvironmentSyncer(final SimuComModel simuComModel, final IModelAccess modelAccessFactory) {
        super(simuComModel, modelAccessFactory.getGlobalPCMModel()
                .getAllocation().getTargetResourceEnvironment_Allocation());
        this.pms = modelAccessFactory.getPMSModel();
        this.prm = modelAccessFactory.getPRMModel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.palladiosimulator.simulizar.syncer.IModelSyncer#initializeSyncer()
     */
    @Override
    public void initializeSyncer() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Synchronise ResourceContainer and Simulated ResourcesContainer");
        }
        // add resource container, if not done already
        for (final ResourceContainer resourceContainer : model.getResourceContainer_ResourceEnvironment()) {
            final String resourceContainerId = resourceContainer.getId();

            SimulatedResourceContainer simulatedResourceContainer;
            if (simuComModel.getResourceRegistry().containsResourceContainer(resourceContainerId)) {
                simulatedResourceContainer = (SimulatedResourceContainer) simuComModel.getResourceRegistry()
                        .getResourceContainer(resourceContainerId);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SimulatedResourceContainer already exists: " + simulatedResourceContainer);
                }
                // now sync active resources
                syncActiveResources(resourceContainer, simulatedResourceContainer);
            } else {
                createSimulatedResource(resourceContainer, resourceContainerId);
            }

        }

        LOG.debug("Synchronisation done");
        // TODO remove unused
    }

    /**
     * @param resourceContainer
     * @param resourceContainerId
     */
    private void createSimulatedResource(ResourceContainer resourceContainer, final String resourceContainerId) {
        final AbstractSimulatedResourceContainer simulatedResourceContainer =
                simuComModel.getResourceRegistry().createResourceContainer(resourceContainerId);
        LOG.debug("Added SimulatedResourceContainer: ID: " + resourceContainerId + " "
                + simulatedResourceContainer);
        // now sync active resources
        syncActiveResources(resourceContainer, simulatedResourceContainer);
    }

    @Override
    protected void synchronizeSimulationEntities(final Notification notification) {
        // TODO: Inspect notification and act accordingly
        initializeSyncer();
    }

    /**
     * Checks whether simulated resource (by type id) already exists in given simulated resource
     * container.
     * 
     * @param simulatedResourceContainer
     *            the simulated resource container.
     * @param typeId
     *            id of the resource.
     * @return the ScheduledResource.
     */
    private ScheduledResource resourceAlreadyExist(final AbstractSimulatedResourceContainer simulatedResourceContainer,
            final String typeId) {
        // Resource already exists?
        for (final AbstractScheduledResource abstractScheduledResource : simulatedResourceContainer
                .getActiveResources()) {
            if (abstractScheduledResource.getResourceTypeId().equals(typeId)) {

                return (ScheduledResource) abstractScheduledResource;

            }
        }
        return null;
    }

    /**
     * Sync resources in resource container. If simulated resource already exists in SimuCom,
     * setProcessingRate will be updated.
     * 
     * @param resourceContainer
     *            the resource container.
     * @param simulatedResourceContainer
     *            the corresponding simulated resource container in SimuCom.
     */
    private void syncActiveResources(final ResourceContainer resourceContainer,
            final AbstractSimulatedResourceContainer simulatedResourceContainer) {

        // add resources
        for (final ProcessingResourceSpecification processingResource : resourceContainer
                .getActiveResourceSpecifications_ResourceContainer()) {
            final String typeId = processingResource.getActiveResourceType_ActiveResourceSpecification().getId();
            final String processingRate = processingResource.getProcessingRate_ProcessingResourceSpecification()
                    .getSpecification();
            // processingRate does not need to be evaluated, will be done in
            // simulatedResourceContainers

            // SchedulingStrategy
            final SchedulingPolicy schedulingPolicy = processingResource.getSchedulingPolicy();

            String schedulingStrategy = schedulingPolicy.getId();
            if (schedulingStrategy.equals("ProcessorSharing")) {
                schedulingStrategy = SchedulingStrategy.PROCESSOR_SHARING;
            } else if (schedulingStrategy.equals("FCFS")) {
                schedulingStrategy = SchedulingStrategy.FCFS;
            } else if (schedulingStrategy.equals("Delay")) {
                schedulingStrategy = SchedulingStrategy.DELAY;
            }

            final ScheduledResource scheduledResource = this.resourceAlreadyExist(simulatedResourceContainer, typeId);
            if (existsResource(scheduledResource)) {
                scheduledResource.setProcessingRate(processingRate);
            } else {
                createSimulatedActiveResource(resourceContainer, simulatedResourceContainer, processingResource,
                        schedulingStrategy);
            }
        }
    }

    /**
     * @param scheduledResource
     *            Resource which existence shall be checked
     * @return true if resource exists
     */
    private boolean existsResource(final ScheduledResource scheduledResource) {
        return scheduledResource != null;
    }

    /**
     * 
     * @param resourceContainer
     * @param simulatedResourceContainer
     * @param processingResource
     * @param schedulingStrategy
     */
    private void createSimulatedActiveResource(
            final ResourceContainer resourceContainer,
            final AbstractSimulatedResourceContainer simulatedResourceContainer,
            final ProcessingResourceSpecification processingResource, String schedulingStrategy) {
        ((SimulatedResourceContainer) simulatedResourceContainer).addActiveResource(
                ModelsAtRuntime.getResourceURI(processingResource),
                new String[] {},
                resourceContainer.getId(),
                schedulingStrategy);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Added ActiveResource. TypeID: "
                    + processingResource.getActiveResourceType_ActiveResourceSpecification().getId()
                    + ", Description: " + ", SchedulingStrategy: " + schedulingStrategy);
        }

        MeasurementSpecification measurementSpecification = PMSUtil.isMonitored(pms, resourceContainer,
                PerformanceMetricEnum.UTILIZATION);
        if (isMonitored(measurementSpecification)) {

            // get created active resource
            for (final AbstractScheduledResource abstractScheduledResource : simulatedResourceContainer
                    .getActiveResources()) {
                if (abstractScheduledResource.getName().equals(processingResource.getId())) {
                    new ResourceStateListener(
                            processingResource,
                            abstractScheduledResource,
                            simuComModel.getSimulationControl(),
                            measurementSpecification,
                            resourceContainer,
                            prm);
                    break;
                }

            }
        }
    }

    /**
     * @param measurementSpecification
     *            the measurement specification to check
     * @return true if it is monitored
     */
    private boolean isMonitored(final MeasurementSpecification measurementSpecification) {
        return measurementSpecification != null;
    }

}