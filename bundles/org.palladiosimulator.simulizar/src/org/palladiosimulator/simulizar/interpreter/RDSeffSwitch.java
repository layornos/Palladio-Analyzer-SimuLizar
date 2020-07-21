package org.palladiosimulator.simulizar.interpreter;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.ComposedSwitch;
import org.eclipse.emf.ecore.util.Switch;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AcquireAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.CollectionIteratorAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ForkAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ReleaseAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;
import org.palladiosimulator.simulizar.runtimestate.SimulatedBasicComponentInstance;
import org.palladiosimulator.simulizar.utils.TransitionDeterminer;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

/**
 * Switch for RFSEFFs. This visitor is responsible for traversing RDSEFF behaviours.
 *
 * @author Joachim Meyer, Steffen Becker, Sebastian Lehrig
 *
 */
class RDSeffSwitch extends SeffSwitch<Object> implements IComposableSwitch {

    private static final Boolean SUCCESS = true;
    private static final Logger LOGGER = Logger.getLogger(RDSeffSwitch.class);

    private ComposedSwitch<Object> parentSwitch;
    private final TransitionDeterminer transitionDeterminer;
    private final InterpreterDefaultContext context;
    private final Allocation allocation;

    private final SimulatedStackframe<Object> resultStackFrame;

    private final SimulatedBasicComponentInstance basicComponentInstance;

    private RDSeffSwitchBehaviourDeltaSeffDelegate seffDeltaDelegate;
    private RDSeffSwitchPiSeffDelegate seffPiDelegate;

    /**
     * Constructor.
     *
     * @param context
     *            Default context for the pcm interpreter.
     * @param basicComponentInstance
     *            Simulated component
     */
    public RDSeffSwitch(final InterpreterDefaultContext context,
            final SimulatedBasicComponentInstance basicComponentInstance) {
        super();
        this.context = context;
        this.allocation = context.getLocalPCMModelAtContextCreation().getAllocation();
        this.transitionDeterminer = new TransitionDeterminer(context);
        this.resultStackFrame = new SimulatedStackframe<Object>();
        this.basicComponentInstance = basicComponentInstance;
        this.seffDeltaDelegate = new RDSeffSwitchBehaviourDeltaSeffDelegate(context,
                resultStackFrame, basicComponentInstance, new GetParentSwitchInterface() {
                    public Switch<Object> getParentSwitch() {
                        return this.getParentSwitch();
                    }
                });
        this.seffPiDelegate =
                new RDSeffSwitchPiSeffDelegate(context, allocation, transitionDeterminer,
                        resultStackFrame, basicComponentInstance, new GetParentSwitchInterface() {
                            public Switch<Object> getParentSwitch() {
                                return this.getParentSwitch();
                            }
                        });
    }


    /**
     * Constructor.
     *
     * @param context
     *				Default context for the pcm interpreter.
     * @param basicComponentInstance
     *				Simulated component
     * @param parentSwitch
     *				The composed switch which is containing this switch
     */
    public RDSeffSwitch(final InterpreterDefaultContext context,
            final SimulatedBasicComponentInstance basicComponentInstance,
            ComposedSwitch<Object> parentSwitch) {
        this(context, basicComponentInstance);
        this.parentSwitch = parentSwitch;
    }

    /**
     * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseResourceDemandingBehaviour(org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour)
     */
    @Override
    public Object caseResourceDemandingBehaviour(final ResourceDemandingBehaviour object) {
        return seffPiDelegate.caseResourceDemandingBehaviour(object);
    }

    /**
     * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseAbstractAction(org.palladiosimulator.pcm.seff.AbstractAction)
     */
    /*
     * (non-Javadoc)
     *
     * @see
     * org.palladiosimulator.pcm.seff.util.SeffSwitch#caseAbstractAction(org.palladiosimulator.pcm.
     * seff.AbstractAction )
     */
    @Override
    public SimulatedStackframe<Object> caseAbstractAction(final AbstractAction object) {
        throw new UnsupportedOperationException(
                "SEFF Interpreter tried to interpret unsupported action type: "
                        + object.eClass().getName());
    }

    /**
     * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseInternalAction(org.palladiosimulator.pcm.seff.InternalAction)
     */
    @Override
    public Object caseInternalAction(final InternalAction internalAction) {
        return seffPiDelegate.caseInternalAction(internalAction);
    }



    /**
     * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseExternalCallAction(org.palladiosimulator.pcm.seff.ExternalCallAction)
     */
    @Override
    public Object caseExternalCallAction(final ExternalCallAction externalCall) {
        return seffDeltaDelegate.caseExternalCallAction(externalCall);
    }

    /**
     * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseBranchAction(org.palladiosimulator.pcm.seff.BranchAction)
     */
    @Override
    public Object caseBranchAction(final BranchAction object) {
        return seffPiDelegate.caseBranchAction(object);
    }

    /**
     * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseCollectionIteratorAction(org.palladiosimulator.pcm.seff.CollectionIteratorAction)
     */
    @Override
    public Object caseCollectionIteratorAction(final CollectionIteratorAction object) {
        return seffDeltaDelegate.caseCollectionIteratorAction(object);
    }

    /**
     * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseForkAction(org.palladiosimulator.pcm.seff.ForkAction)
     */
    @Override
    public Object caseForkAction(final ForkAction object) {
        return seffPiDelegate.caseForkAction(object);
    }

    /**
     * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseLoopAction(org.palladiosimulator.pcm.seff.LoopAction)
     */
    @Override
    public Object caseLoopAction(final LoopAction object) {
        return seffPiDelegate.caseLoopAction(object);
    }

    /**
     * @see org.palladiosimulator.pcm.seff.util.SeffSwitch#caseSetVariableAction(org.palladiosimulator.pcm.seff.SetVariableAction)
     */
    @Override
    public Object caseSetVariableAction(final SetVariableAction object) {
        return seffDeltaDelegate.caseSetVariableAction(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.palladiosimulator.pcm.seff.util.SeffSwitch#caseAcquireAction(org.palladiosimulator.pcm.
     * seff.AcquireAction )
     */
    @Override
    public Object caseAcquireAction(final AcquireAction acquireAction) {
        return seffDeltaDelegate.caseAcquireAction(acquireAction);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.palladiosimulator.pcm.seff.util.SeffSwitch#caseReleaseAction(org.palladiosimulator.pcm.
     * seff.ReleaseAction )
     */
    @Override
    public Object caseReleaseAction(final ReleaseAction releaseAction) {
        return seffDeltaDelegate.caseReleaseAction(releaseAction);
    }



    @Override
    public Switch<Object> getParentSwitch() {
        if (this.parentSwitch != null) {
            return this.parentSwitch;
        }
        return this;
    }
}
