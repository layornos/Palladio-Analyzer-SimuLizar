package org.palladiosimulator.simulizar.interpreter;

import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.ComposedSwitch;
import org.eclipse.emf.ecore.util.Switch;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;
import org.palladiosimulator.simulizar.utils.SimulatedStackHelper;
import org.palladiosimulator.analyzer.completions.DelegatingExternalCallAction;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.AcquireAction;
import org.palladiosimulator.pcm.seff.ReleaseAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;
import org.palladiosimulator.simulizar.runtimestate.SimulatedBasicComponentInstance;
import org.palladiosimulator.simulizar.exceptions.SimulatedStackAccessException;
import org.palladiosimulator.pcm.seff.CollectionIteratorAction;
import org.palladiosimulator.pcm.repository.Parameter;
import de.uka.ipd.sdq.simucomframework.variables.StackContext;

public class RDSeffSwitchDelta extends SeffSwitch<Object> implements IComposableSwitch {

  private static final Boolean SUCCESS = true;
  private static final Logger LOGGER =
      Logger.getLogger(RDSeffSwitchDelta.class);
  private final InterpreterDefaultContext context;
  private final SimulatedStackframe<Object> resultStackFrame;
  private final SimulatedBasicComponentInstance basicComponentInstance;
  private ComposedSwitch<Object> parentSwitch;

  public RDSeffSwitchDelta(InterpreterDefaultContext context,
	      SimulatedStackframe<Object> resultStackFrame,
	      SimulatedBasicComponentInstance basicComponentInstance) {
	    this.resultStackFrame = resultStackFrame;
	    this.context = context;
	    this.basicComponentInstance = basicComponentInstance;
  }
  public RDSeffSwitchDelta(InterpreterDefaultContext context,
      SimulatedStackframe<Object> resultStackFrame,
      SimulatedBasicComponentInstance basicComponentInstance,
      ComposedSwitch<Object> parentSwitch) {
	  this(context, resultStackFrame, basicComponentInstance);
    this.parentSwitch = parentSwitch;
  }

  /**
  * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseExternalCallAction(org.palladiosimulator.pcm.seff.ExternalCallAction)
  */
  public Object caseExternalCallAction(final ExternalCallAction externalCall) {
    final ComposedStructureInnerSwitch composedStructureSwitch = new ComposedStructureInnerSwitch(
        this.context, externalCall.getCalledService_ExternalService(),
        externalCall.getRole_ExternalService());

    if (externalCall instanceof DelegatingExternalCallAction) {
      final SimulatedStackframe<Object> currentFrame = this.context.getStack().currentStackFrame();
      final SimulatedStackframe<Object> callFrame = SimulatedStackHelper.createAndPushNewStackFrame(
          this.context.getStack(), externalCall.getInputVariableUsages__CallAction(), currentFrame);
      callFrame.addVariables(this.resultStackFrame);
    } else {
      // create new stack frame for input parameter
      SimulatedStackHelper.createAndPushNewStackFrame(this.context.getStack(),
          externalCall.getInputVariableUsages__CallAction());
    }
    final AssemblyContext myContext = this.context.getAssemblyContextStack().pop();
    final SimulatedStackframe<Object> outputFrame = composedStructureSwitch.doSwitch(myContext);
    this.context.getAssemblyContextStack().push(myContext);
    this.context.getStack().removeStackFrame();

    SimulatedStackHelper.addParameterToStackFrame(outputFrame,
        externalCall.getReturnVariableUsage__CallReturnAction(),
        this.context.getStack().currentStackFrame());

    return SUCCESS;
  }

  /*
  * (non-Javadoc)
  *
  * @see
  * org.palladiosimulator.pcm.seff.util.SeffSwitch#caseAcquireAction(org.palladiosimulator.pcm.
  * seff.AcquireAction )
  */
  public Object caseAcquireAction(final AcquireAction acquireAction) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Process " + this.context.getThread().getId() + " tries to acquire "
          + acquireAction.getPassiveresource_AcquireAction().getEntityName());
    }
    this.basicComponentInstance.acquirePassiveResource(
        acquireAction.getPassiveresource_AcquireAction(), this.context,
        this.context.getModel().getConfiguration().getSimulateFailures(),
        acquireAction.getTimeoutValue());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Process " + this.context.getThread().getId() + " successfully acquired "
          + acquireAction.getPassiveresource_AcquireAction().getEntityName());
    }
    return SUCCESS;
  }

  /*
  * (non-Javadoc)
  *
  * @see
  * org.palladiosimulator.pcm.seff.util.SeffSwitch#caseReleaseAction(org.palladiosimulator.pcm.
  * seff.ReleaseAction )
  */
  public Object caseReleaseAction(final ReleaseAction releaseAction) {
    this.basicComponentInstance
        .releasePassiveResource(releaseAction.getPassiveResource_ReleaseAction(), this.context);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Process " + this.context.getThread().getId() + " released "
          + releaseAction.getPassiveResource_ReleaseAction().getEntityName());
    }
    return SUCCESS;
  }

  /**
  * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseSetVariableAction(org.palladiosimulator.pcm.seff.SetVariableAction)
  */
  public Object caseSetVariableAction(final SetVariableAction object) {
    SimulatedStackHelper.addParameterToStackFrame(this.context.getStack().currentStackFrame(),
        object.getLocalVariableUsages_SetVariableAction(), this.resultStackFrame);
    /*
     * Special attention has to be paid if the random variable to set is an INNER
     * characterisation. In this case, a late evaluating random variable has to be stored with
     * the current stack frame as evaluation context (cf. section 4.4.2).
     *
     * Why?
     */
    return SUCCESS;
  }

  /**
  * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseCollectionIteratorAction(org.palladiosimulator.pcm.seff.CollectionIteratorAction)
  */
  public Object caseCollectionIteratorAction(final CollectionIteratorAction object) {
    this.iterateOverCollection(object, object.getParameter_CollectionIteratorAction());

    return SUCCESS;
  }

  /**
   * Iterates over collection of given CollectionIteratorAction.
   *
   * @param object
   *            the CollectionIteratorAction.
   * @param parameter
   *            parameter of the collection.
   * @return
   */
  private void iterateOverCollection(final CollectionIteratorAction object,
      final Parameter parameter) {
    // TODO make better
    final String idNumberOfLoops = parameter.getParameterName() + ".NUMBER_OF_ELEMENTS";

    // get number of loops
    final int numberOfLoops = StackContext.evaluateStatic(idNumberOfLoops, Integer.class,
        this.context.getStack().currentStackFrame());

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Determined number of loops: " + numberOfLoops + " " + object);
    }
    for (int i = 0; i < numberOfLoops; i++) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Interpret loop number " + i + ": " + object);
      }

      // create new stack frame for value characterizations of inner
      // collection variable
      final SimulatedStackframe<Object> innerVariableStackFrame = this.context.getStack()
          .createAndPushNewStackFrame(this.context.getStack().currentStackFrame());

      /*
       * evaluate value characterization of inner collection variable, store them on created
       * top most stack frame. Add a . at the end of the parameter name because otherwise if
       * we search for parameter name "ab" we also get variables called "abc"
       */
      // TODO the point is not nice
      this.context.evaluateInner(innerVariableStackFrame, parameter.getParameterName() + ".");

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created new stackframe with evaluated inner collection variables: "
            + innerVariableStackFrame);
      }

      /*
       * now further access on inner variables are caught in the current top most frame. In
       * other words, they are currently overridden with their evaluated values. This has the
       * effect that actions within the iterator use the same evaluated values. This is very
       * important in case of EvaluationProxys which should not be reevaluated for each action
       * within an iteration.
       */

      getParentSwitch().doSwitch(object.getBodyBehaviour_Loop());

      // remove stack frame for value characterisations of inner
      // collection variable
      if (this.context.getStack().currentStackFrame() != innerVariableStackFrame) {
        throw new SimulatedStackAccessException(
            "Inner value characterisations of inner collection variable expected");
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Remove stack frame: " + innerVariableStackFrame);
      }
      this.context.getStack().removeStackFrame();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Finished loop number " + i + ": " + object);
      }
    }
  }

  @Override
  public Switch<Object> getParentSwitch() {
      if (this.parentSwitch != null) {
          return this.parentSwitch;
      }
      return this;
  }

}
