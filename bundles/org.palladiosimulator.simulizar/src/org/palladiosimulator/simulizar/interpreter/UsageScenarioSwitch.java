package org.palladiosimulator.simulizar.interpreter;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Branch;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.Loop;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch;
import org.palladiosimulator.simulizar.exceptions.PCMModelInterpreterException;
import org.palladiosimulator.simulizar.interpreter.listener.EventType;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;
import org.palladiosimulator.simulizar.utils.SimulatedStackHelper;
import org.palladiosimulator.simulizar.utils.TransitionDeterminer;
import org.eclipse.emf.ecore.EObject;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 * Switch for Usage Scenario in Usage Model
 *
 * @author Joachim Meyer
 *
 * @param <T>
 *            return type of switch methods.
 */

public class UsageScenarioSwitch<T> extends UsagemodelSwitch<T> {

    protected static final Logger LOGGER = Logger.getLogger(UsageScenarioSwitch.class.getName());

    private final TransitionDeterminer transitionDeterminer;
    private final UsageScenarioSwitchDeltaDelegate delegateDelta;
    private final UsageScenarioSwitchPIDelegate delegatePI;
    /**
     * Constructor
     *
     * @param modelInterpreter
     *            the corresponding pcm model interpreter holding this switch..
     */
    public UsageScenarioSwitch(final InterpreterDefaultContext context) {
        this.transitionDeterminer = new TransitionDeterminer(context);
        this.delegateDelta = new UsageScenarioSwitchDeltaDelegate(context, new DoSwitchInterface<T>(){
            @Override
            public T doSwitch(EObject theEObject) {
                return this.doSwitch(theEObject);
            }
        });
        this.delegatePI = new UsageScenarioSwitchPIDelegate(context,LOGGER,new DoSwitchInterface<T>(){
            @Override
            public T doSwitch(EObject theEObject) {
                return this.doSwitch(theEObject);
            }
        });
    }

    /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseBranch(org.palladiosimulator.pcm.usagemodel.Branch)
     */
    @Override
    public T caseBranch(final Branch object) {
        delegatePI.caseBranch(object);
        return super.caseBranch(object);
    }

    /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseDelay(org.palladiosimulator.pcm.usagemodel.Delay)
     */
    @Override
    public T caseDelay(final Delay object) {
        delegatePI.caseDelay(object);
        return super.caseDelay(object);
    }

    /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseEntryLevelSystemCall(org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall)
     */
    @Override
    public T caseEntryLevelSystemCall(final EntryLevelSystemCall entryLevelSystemCall) {
        delegateDelta.caseEntryLevelSystemCall(entryLevelSystemCall);
        return super.caseEntryLevelSystemCall(entryLevelSystemCall);
    }

    /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseLoop(org.palladiosimulator.pcm.usagemodel.Loop)
     */
    @Override
    public T caseLoop(final Loop object) {
        delegatePI.caseLoop(object);
        return super.caseLoop(object);
    }

    /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseScenarioBehaviour(org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour)
     */
    @Override
    public T caseScenarioBehaviour(final ScenarioBehaviour object) {
        delegatePI.caseScenarioBehaviour(object);
        return super.caseScenarioBehaviour(object);
    }

    // /**
    // * @see
    // org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseStart(org.palladiosimulator.pcm.usagemodel.Start)
    // */
    // @Override
    // public T caseStart(final Start object)
    // {
    // InterpreterLogger.debug(LOGGER, "Interpret Start: " + object);
    //
    // AbstractUserAction currentAction = object;
    //
    // InterpreterLogger.debug(LOGGER, "Follow action chain");
    // // follow action chain, beginning with start action
    // while ((currentAction = currentAction.getSuccessor()) != null)
    // {
    // this.doSwitch(currentAction);
    // }
    //
    // InterpreterLogger.debug(LOGGER, "Finished start: " + object);
    // return super.caseStart(object);
    // }

    /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseAbstractUserAction(org.palladiosimulator.pcm.usagemodel.AbstractUserAction)
     */
    public T caseAbstractUserAction(final AbstractUserAction object) {
        delegatePI.caseAbstractUserAction(object);
        return super.caseAbstractUserAction(object);
    }

    /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseUsageScenario(org.palladiosimulator.pcm.usagemodel.UsageScenario)
     */
    public T caseUsageScenario(final UsageScenario usageScenario) {
        delegateDelta.caseUsageScenario(usageScenario);
        return super.caseUsageScenario(usageScenario);
    }
}
