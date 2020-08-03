package org.palladiosimulator.simulizar.interpreter;

import org.palladiosimulator.simulizar.utils.SimulatedStackHelper;
import org.palladiosimulator.simulizar.exceptions.PCMModelAccessException;
import org.palladiosimulator.pcm.seff.seff_performance.ResourceCall;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.palladiosimulator.pcm.seff.seff_performance.InfrastructureCall;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.resourcetype.ResourceType;
import org.palladiosimulator.pcm.resourcetype.ResourceSignature;
import org.palladiosimulator.pcm.resourcetype.ResourceRepository;
import org.palladiosimulator.pcm.resourcetype.ResourceInterface;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.core.entity.ResourceProvidedRole;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.apache.log4j.Logger;
import java.util.ListIterator;
import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;
import de.uka.ipd.sdq.simucomframework.variables.StackContext;
import de.uka.ipd.sdq.simucomframework.variables.converter.NumberConverter;
import de.uka.ipd.sdq.simucomframework.ResourceRegistry;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.ComposedSwitch;
import org.eclipse.emf.ecore.util.Switch;
import org.palladiosimulator.simulizar.exceptions.PCMModelInterpreterException;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.simulizar.utils.TransitionDeterminer;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ForkAction;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import java.util.List;
import de.uka.ipd.sdq.simucomframework.fork.ForkExecutor;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.simulizar.interpreter.listener.EventType;
import org.palladiosimulator.pcm.seff.SeffPackage;
import de.uka.ipd.sdq.simucomframework.fork.ForkedBehaviourProcess;
import java.util.LinkedList;
import java.util.Collections;
import org.palladiosimulator.simulizar.interpreter.listener.RDSEFFElementPassedEvent;
import java.util.Stack;
import org.palladiosimulator.pcm.seff.ForkedBehaviour;
import java.util.ArrayList;
import org.palladiosimulator.simulizar.runtimestate.SimulatedBasicComponentInstance;


public class RDSeffSwitchPi extends SeffSwitch<Object> implements IComposableSwitch {
  private static final Boolean SUCCESS = true;
  private static final Logger LOGGER = Logger.getLogger(RDSeffSwitchPi.class);
  private final InterpreterDefaultContext context;
  private final Allocation allocation;
  private final TransitionDeterminer transitionDeterminer;
  private final SimulatedStackframe<Object> resultStackFrame;
  private final SimulatedBasicComponentInstance basicComponentInstance;
  private ComposedSwitch<Object> parentSwitch;
  
  public RDSeffSwitchPi(InterpreterDefaultContext context,
	      SimulatedStackframe<Object> resultStackFrame,
	      SimulatedBasicComponentInstance basicComponentInstance ) {
	    this.allocation = context.getLocalPCMModelAtContextCreation().getAllocation();
	    this.context = context;
	    this.transitionDeterminer = new TransitionDeterminer(context);
	    this.resultStackFrame = resultStackFrame;
	    this.basicComponentInstance = basicComponentInstance;
	  }

  public RDSeffSwitchPi(InterpreterDefaultContext context,
      SimulatedStackframe<Object> resultStackFrame,
      SimulatedBasicComponentInstance basicComponentInstance, ComposedSwitch<Object> parentSwitch
     ) {
	this(context, resultStackFrame, basicComponentInstance);
    this.parentSwitch = parentSwitch;

  }

  /**
  * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseInternalAction(org.palladiosimulator.pcm.seff.InternalAction)
  */
  public Object caseInternalAction(final InternalAction internalAction) {
    if (internalAction.getResourceDemand_Action().size() > 0) {
      interpretResourceDemands(internalAction);
    }
    if (internalAction.getInfrastructureCall__Action().size() > 0) {
      interpretInfrastructureCalls(internalAction);
    }
    if (internalAction.getInternalFailureOccurrenceDescriptions__InternalAction().size() > 0) {
      interpretFailures(internalAction);
    }
    if (internalAction.getResourceCall__Action().size() > 0) {
      interpretResourceCall(internalAction);
    }
    return SUCCESS;
  }

  /**
  * @param internalAction
  * 				The internal action containing the resource demand
  */
  private void interpretResourceDemands(final InternalAction internalAction) {
    final AllocationContext allocationContext = this.getAllocationContext(this.allocation);
    final ResourceContainer resourceContainer =
        allocationContext.getResourceContainer_AllocationContext();

    for (final ParametricResourceDemand parametricResourceDemand : internalAction
        .getResourceDemand_Action()) {

      final ResourceRegistry resourceRegistry = this.context.getModel().getResourceRegistry();
      final String idRequiredResourceType =
          parametricResourceDemand.getRequiredResource_ParametricResourceDemand().getId();
      final String specification =
          parametricResourceDemand.getSpecification_ParametericResourceDemand().getSpecification();
      final SimulatedStackframe<Object> currentStackFrame =
          this.context.getStack().currentStackFrame();
      final Double value =
          StackContext.evaluateStatic(specification, Double.class, currentStackFrame);

      resourceRegistry.getResourceContainer(resourceContainer.getId())
          .loadActiveResource(this.context.getThread(), idRequiredResourceType, value);

    }
  }

  /**
  * @param internalAction
  */
  private void interpretResourceCall(final InternalAction internalAction) {
    final AllocationContext allocationContext = this.getAllocationContext(this.allocation);
    final ResourceContainer resourceContainer =
        allocationContext.getResourceContainer_AllocationContext();

    for (final ResourceCall resourceCall : internalAction.getResourceCall__Action()) {

      // find the corresponding resource type which was invoked by the resource call
      final ResourceInterface resourceInterface =
          resourceCall.getSignature__ResourceCall().getResourceInterface__ResourceSignature();
      final ResourceRepository resourceRepository =
          resourceInterface.getResourceRepository__ResourceInterface();
      ResourceType currentResourceType = null;

      for (final ResourceType resourceType : resourceRepository
          .getAvailableResourceTypes_ResourceRepository()) {
        for (final ResourceProvidedRole resourceProvidedRole : resourceType
            .getResourceProvidedRoles__ResourceInterfaceProvidingEntity()) {
          if (resourceProvidedRole.getProvidedResourceInterface__ResourceProvidedRole().getId()
              .equals(resourceInterface.getId())) {
            currentResourceType = resourceType;
            break;
          }
        }
      }

      final ResourceSignature resourceSignature = resourceCall.getSignature__ResourceCall();
      final int resourceServiceId = resourceSignature.getResourceServiceId();

      final SimulatedStackframe<Object> currentStackFrame =
          this.context.getStack().currentStackFrame();
      final Double evaluatedDemand = NumberConverter.toDouble(StackContext.evaluateStatic(
          resourceCall.getNumberOfCalls__ResourceCall().getSpecification(), Double.class,
          currentStackFrame));
      final String idRequiredResourceType = currentResourceType.getId();

      final ResourceRegistry resourceRegistry = this.context.getModel().getResourceRegistry();

      resourceRegistry.getResourceContainer(resourceContainer.getId()).loadActiveResource(
          this.context.getThread(), resourceServiceId, idRequiredResourceType, evaluatedDemand);

    }
  }

  /**
  * @param internalAction
  */
  private void interpretInfrastructureCalls(final InternalAction internalAction) {
    for (final InfrastructureCall infrastructureCall : internalAction
        .getInfrastructureCall__Action()) {
      final SimulatedStackframe<Object> currentStackFrame =
          this.context.getStack().currentStackFrame();
      final int repetitions = StackContext.evaluateStatic(
          infrastructureCall.getNumberOfCalls__InfrastructureCall().getSpecification(),
          Integer.class, currentStackFrame);
      for (int i = 0; i < repetitions; i++) {
        final ComposedStructureInnerSwitch composedStructureSwitch =
            new ComposedStructureInnerSwitch(this.context,
                infrastructureCall.getSignature__InfrastructureCall(),
                infrastructureCall.getRequiredRole__InfrastructureCall());

        // create new stack frame for input parameter
        SimulatedStackHelper.createAndPushNewStackFrame(this.context.getStack(),
            infrastructureCall.getInputVariableUsages__CallAction());
        final AssemblyContext myContext = this.context.getAssemblyContextStack().pop();
        composedStructureSwitch.doSwitch(myContext);
        this.context.getAssemblyContextStack().push(myContext);
        this.context.getStack().removeStackFrame();
      }
    }
  }

  /**
  * @param internalAction
  *
  */
  private void interpretFailures(final InternalAction internalAction) {
    if (this.context.getModel().getConfiguration().getSimulateFailures()) {
      throw new UnsupportedOperationException(
          "Simulation of failures not yet supported by Simulizar");
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "A failure description is available in an action, but skipped due to configuration set to not simulate failures.");
      }
    }
  }

  /**
  * Gets the allocation context for the current assembly context stack. The stack is investigated
  * in a FIFO-manner, i.e., first upper elements are checked. This is needed for the case of sub
  * systems.
  *
  * @param allocation
  *            The allocation to find a suitable allocation context in.
  * @return The allocation context.
  * @throws PCMModelAccessException
  *             if no allocation context could be found.
  */
  private AllocationContext getAllocationContext(final Allocation allocation) {
    // For iterating top-down through a stack see:
    // http://stackoverflow.com/questions/16992758/is-there-a-bug-in-java-util-stacks-iterator
    for (final AllocationContext allocationContext : allocation
        .getAllocationContexts_Allocation()) {
      for (final ListIterator<AssemblyContext> iterator = this.context.getAssemblyContextStack()
          .listIterator(this.context.getAssemblyContextStack().size()); iterator.hasPrevious();) {
        if (allocationContext.getAssemblyContext_AllocationContext().getId()
            .equals(iterator.previous().getId())) {
          return allocationContext;
        }
      }
    }

    throw new PCMModelAccessException(
        "No AllocationContext in Allocation " + allocation + " for AssemblyContext "
            + this.context.getAssemblyContextStack().peek() + " or its parents.");
  }

  /**
  * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseBranchAction(org.palladiosimulator.pcm.seff.BranchAction)
  */
  public Object caseBranchAction(final BranchAction object) {
    final EList<AbstractBranchTransition> abstractBranchTransitions = object.getBranches_Branch();
    if (abstractBranchTransitions.isEmpty()) {
      throw new PCMModelInterpreterException("Empty branch action is not allowed");
    }

    if (LOGGER.isDebugEnabled()) {
      final StringBuilder sb = new StringBuilder();

      sb.append("Branch \"");
      sb.append(object.getEntityName());
      sb.append("\" [ID: ");
      sb.append(object.getId());
      sb.append("\"] with ");
      sb.append(object.getBranches_Branch().size());
      sb.append(" branches.");

      LOGGER.debug(sb.toString());
    }
    final AbstractBranchTransition branchTransition =
        this.transitionDeterminer.determineTransition(abstractBranchTransitions);

    /*
     * In case of a guarded transition, it must not necessarily be the case, that any branch
     * condition evaluated to true.
     */

    if (branchTransition == null) {
      LOGGER.error("No branch's condition evaluated to true, no branch selected: " + object);
      throw new PCMModelInterpreterException(
          "No branch transition was active. This is not allowed.");
    } else {
      parentSwitch
          .doSwitch(branchTransition.getBranchBehaviour_BranchTransition());
    }

    return SUCCESS;
  }

  /**
  * Interpret inner path of loop the given times
  *
  * @param object
  *            the LoopAction.
  * @param numberOfLoops
  *            number of loops.
  */
  private void interpretLoop(final LoopAction object, final int numberOfLoops) {
    for (int i = 0; i < numberOfLoops; i++) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Interpret loop number " + i + ": " + object);
      }
      getParentSwitch().doSwitch(object.getBodyBehaviour_Loop());
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Finished loop number " + i + ": " + object);
      }
    }
  }

  /**
  * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseLoopAction(org.palladiosimulator.pcm.seff.LoopAction)
  */
  public Object caseLoopAction(final LoopAction object) {
    final PCMRandomVariable iterationCount = object.getIterationCount_LoopAction();
    final String stoex = iterationCount.getSpecification();

    // we expect an int here
    final int numberOfLoops = StackContext.evaluateStatic(stoex, Integer.class,
        this.context.getStack().currentStackFrame());

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Determined number of loops: " + numberOfLoops + " " + object);
    }

    // interpret behavior the given number of times
    this.interpretLoop(object, numberOfLoops);

    return SUCCESS;
  }

  /**
  * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseForkAction(org.palladiosimulator.pcm.seff.ForkAction)
  */
  public Object caseForkAction(final ForkAction object) {
    /*
     * Component developers can use a SynchronisationPoint to join synchronously
     * ForkedBehaviours and specify a result of the computations with its attached
     * VariableUsages.
     *
     * For ForkedBehaviours attached to the SynchronizationPoint, it will be possible to return
     * results of their computations to the initiating ForkAction in future versions of the PCM.
     * Happe (2008) currently defines the necessary meta-model changes.
     *
     * THIS IS CURRENTLY NOT SUPPORTED BY THE INTERPRETER
     */

    // get asynced processes
    final List<ForkedBehaviourProcess> asyncProcesses =
        this.getProcesses(object.getAsynchronousForkedBehaviours_ForkAction(), true);

    // get synced processes
    final List<ForkedBehaviourProcess> syncProcesses = this.determineSyncedProcesses(object);

    // combine both
    final List<ForkedBehaviourProcess> combinedProcesses =
        this.combineProcesses(asyncProcesses, syncProcesses);

    // create and start fork executor
    final ForkExecutor forkExecutor = new ForkExecutor(this.context.getThread(),
        combinedProcesses.toArray(new ForkedBehaviourProcess[0]));

    forkExecutor.run();

    return SUCCESS;
  }

  /**
  * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseResourceDemandingBehaviour(org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour)
  */
  public Object caseResourceDemandingBehaviour(final ResourceDemandingBehaviour object) {
    final int stacksize = this.context.getStack().size();

    AbstractAction currentAction = null;
    // interpret start action
    for (final AbstractAction abstractAction : object.getSteps_Behaviour()) {
      if (abstractAction.eClass() == SeffPackage.eINSTANCE.getStartAction()) {
        this.firePassedEvent(abstractAction, EventType.BEGIN);
        currentAction = abstractAction.getSuccessor_AbstractAction();
        this.firePassedEvent(abstractAction, EventType.END);
        break;
      }
    }
    if (currentAction == null) {
      throw new PCMModelInterpreterException("RDSEFF is invalid, it misses a start action");
    }

    while (currentAction.eClass() != SeffPackage.eINSTANCE.getStopAction()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Interpret " + currentAction.eClass().getName() + ": " + currentAction);
      }
      this.firePassedEvent(currentAction, EventType.BEGIN);
     this.getParentSwitch().doSwitch(currentAction);
      this.firePassedEvent(currentAction, EventType.END);
      currentAction = currentAction.getSuccessor_AbstractAction();
    }

    if (this.context.getStack().size() != stacksize) {
      throw new PCMModelInterpreterException("Interpreter did not pop all pushed stackframes");
    }

    return this.resultStackFrame;
  }

  /**
  * Combines synced and asynced processes in a combined list.
  *
  * @param asyncProcesses
  *            list of asynced processes.
  * @param syncProcesses
  *            list of synced processes.
  * @return combined list.
  */
  private List<ForkedBehaviourProcess> combineProcesses(
      final List<ForkedBehaviourProcess> asyncProcesses,
      final List<ForkedBehaviourProcess> syncProcesses) {
    final List<ForkedBehaviourProcess> combinedProcesses = new LinkedList<ForkedBehaviourProcess>();
    combinedProcesses.addAll(asyncProcesses);
    combinedProcesses.addAll(syncProcesses);
    return Collections.synchronizedList(combinedProcesses);
  }

  /**
   * Determines the synced processes in a fork action.
   *
   * @param object
   *            the fork action.
   * @return a list with synced processes.
   */
  private List<ForkedBehaviourProcess> determineSyncedProcesses(final ForkAction object) {
    List<ForkedBehaviourProcess> syncProcesses = new ArrayList<ForkedBehaviourProcess>();

    if (object.getSynchronisingBehaviours_ForkAction() != null) {
      syncProcesses = this.getProcesses(object.getSynchronisingBehaviours_ForkAction()
          .getSynchronousForkedBehaviours_SynchronisationPoint(), false);
    }
    return syncProcesses;
  }

  /**
   * Creates a list of sync and async processes for given behaviors.
   *
   * @param forkedBehaviours
   *            the forked behaviors, independent of their sync or async character.
   * @param isAsync
   *            true if processes shall be async, otherwise false.
   * @return a list of configured forked behavior processes.
   */
  private List<ForkedBehaviourProcess> getProcesses(final List<ForkedBehaviour> forkedBehaviours,
      final boolean isAsync) {
    final List<ForkedBehaviourProcess> processes = new LinkedList<ForkedBehaviourProcess>();

    // for each create process, and add to array of processes

    for (final ForkedBehaviour forkedBehaviour : forkedBehaviours) {
      @SuppressWarnings("unchecked")
      final Stack<AssemblyContext> parentAssemblyContextStack =
          (Stack<AssemblyContext>) this.context.getAssemblyContextStack().clone();
      processes.add(new ForkedBehaviourProcess(this.context,
          this.context.getAssemblyContextStack().peek().getId(), isAsync) {

        @Override
        protected void executeBehaviour() {

          /*
           * The forked behavior process has its own copied stack in its context, for type
           * reasons we need an InterpreterDefaultContext. Thus we have to copy the
           * context including its stack.
           */
          final InterpreterDefaultContext seffContext = new InterpreterDefaultContext(
              this.myContext, RDSeffSwitchPi.this.context.getRuntimeState(), true,
              RDSeffSwitchPi.this.context.getLocalPCMModelAtContextCreation());
          seffContext.getAssemblyContextStack().addAll(parentAssemblyContextStack);
          final RDSeffSwitchPi seffInterpreter =
              new RDSeffSwitchPi(seffContext, resultStackFrame, RDSeffSwitchPi.this.basicComponentInstance);

          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created new RDSeff interpreter for "
                + ((this.isAsync()) ? "asynced" : "synced") + " forked baviour: " + this);
          }
          // no use of parentSwitch.doSwitch() because we want the inner switches
          seffInterpreter.doSwitch(forkedBehaviour);
        }

      });
    }
    return processes;
  }

  /**
  * @param abstractAction
  * @param eventType
  */
  private <T extends AbstractAction> void firePassedEvent(final T abstractAction,
      final EventType eventType) {
    this.context.getRuntimeState().getEventNotificationHelper()
        .firePassedEvent(new RDSEFFElementPassedEvent<T>(abstractAction, eventType, this.context,
            this.context.getAssemblyContextStack().peek()));
  }

  @Override
  public Switch<Object> getParentSwitch() {
      if (this.parentSwitch != null) {
          return this.parentSwitch;
      }
      return this;
  }
}
