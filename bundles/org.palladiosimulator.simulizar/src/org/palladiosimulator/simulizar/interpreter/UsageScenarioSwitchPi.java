package org.palladiosimulator.simulizar.interpreter;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.ComposedSwitch;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Branch;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.Loop;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch;
import org.palladiosimulator.simulizar.utils.TransitionDeterminer;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;


public class UsageScenarioSwitchPi<T> extends UsagemodelSwitch<T> {
    private final InterpreterDefaultContext context;
    private final Logger LOGGER = Logger.getLogger(UsageScenarioSwitchPi.class);
    private final TransitionDeterminer transitionDeterminer;
    private final ComposedSwitch<T> parentSwitch;
    
    public UsageScenarioSwitchPi(InterpreterDefaultContext context, ComposedSwitch<T> parentSwitch ){
      this.context = context;
      this.transitionDeterminer = new TransitionDeterminer(context);
      this.parentSwitch = parentSwitch;
      
    }

    /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseBranch(org.palladiosimulator.pcm.usagemodel.Branch)
     */
    public T caseBranch(final Branch object) {
        // determine branch transition
        final BranchTransition branchTransition = this.transitionDeterminer
                .determineBranchTransition(object.getBranchTransitions_Branch());

        // interpret scenario behaviour of branch transition
        parentSwitch.doSwitch(branchTransition.getBranchedBehaviour_BranchTransition());
        return super.caseBranch(object);
    }

    /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseDelay(org.palladiosimulator.pcm.usagemodel.Delay)
     */
    public T caseDelay(final Delay object) {
        // determine delay
        final double delay = StackContext.evaluateStatic(object.getTimeSpecification_Delay().getSpecification(),
                Double.class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start delay " + delay + " @ simulation time "
                    + this.context.getModel().getSimulationControl().getCurrentSimulationTime());
        }
        // hold simulation process
        this.context.getThread().hold(delay);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Continue user @ simulation time "
                    + this.context.getModel().getSimulationControl().getCurrentSimulationTime());
        }
        return super.caseDelay(object);
    }
        /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseLoop(org.palladiosimulator.pcm.usagemodel.Loop)
     */
    public T caseLoop(final Loop object) {
        // determine number of loops
        final int numberOfLoops = StackContext.evaluateStatic(object.getLoopIteration_Loop().getSpecification(),
                Integer.class);
        for (int i = 0; i < numberOfLoops; i++) {
            LOGGER.debug("Interpret loop number " + i);
            parentSwitch.doSwitch(object.getBodyBehaviour_Loop());
            LOGGER.debug("Finished loop number " + i);
        }
        return super.caseLoop(object);
    }
        /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseScenarioBehaviour(org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour)
     */
    public T caseScenarioBehaviour(final ScenarioBehaviour object) {
        // interpret start user action
        for (final AbstractUserAction abstractUserAction : object.getActions_ScenarioBehaviour()) {
            if (abstractUserAction instanceof Start) {
                parentSwitch.doSwitch(abstractUserAction);
                break;
            }
        }
        return super.caseScenarioBehaviour(object);
    }
        /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseAbstractUserAction(org.palladiosimulator.pcm.usagemodel.AbstractUserAction)
     */
    public T caseAbstractUserAction(final AbstractUserAction object) {
        if (object.getSuccessor() != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Interpret " + object.getSuccessor().eClass().getName() + ": " + object);
            }
            parentSwitch.doSwitch(object.getSuccessor());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Finished Interpretation of " + object.getSuccessor().eClass().getName() + ": " + object);
            }
        }
        return super.caseAbstractUserAction(object);
    }



}