package org.palladiosimulator.simulizar.interpreter;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.simulizar.utils.SimulatedStackHelper;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEvent;
import org.palladiosimulator.simulizar.interpreter.listener.EventType;
import org.palladiosimulator.simulizar.exceptions.PCMModelInterpreterException;
import org.eclipse.emf.ecore.util.ComposedSwitch;
import org.palladiosimulator.pcm.repository.OperationSignature;

public class UsageScenarioSwitchDelta<T> extends UsagemodelSwitch<T>  {

        private final InterpreterDefaultContext context;
        private final ComposedSwitch<T> parentSwitch;

    public UsageScenarioSwitchDelta(InterpreterDefaultContext context, ComposedSwitch<T> parentSwitch){
        this.context = context;
        this.parentSwitch = parentSwitch;
    }
      /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseEntryLevelSystemCall(org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall)
     */
    public T caseEntryLevelSystemCall(final EntryLevelSystemCall entryLevelSystemCall) {
        final RepositoryComponentSwitch providedDelegationSwitch = new RepositoryComponentSwitch(this.context,
                RepositoryComponentSwitch.SYSTEM_ASSEMBLY_CONTEXT,
                entryLevelSystemCall.getOperationSignature__EntryLevelSystemCall(),
                entryLevelSystemCall.getProvidedRole_EntryLevelSystemCall());

        this.context.getRuntimeState().getEventNotificationHelper()
                .firePassedEvent(new ModelElementPassedEvent<EntryLevelSystemCall>(entryLevelSystemCall,
                        EventType.BEGIN, this.context));

        // FIXME We stick to single model elements here even though several would be needed to
        // uniquely identify the measuring point of interest (system + role + signature) [Lehrig]
        this.context.getRuntimeState().getEventNotificationHelper()
                .firePassedEvent(new ModelElementPassedEvent<OperationSignature>(
                        entryLevelSystemCall.getOperationSignature__EntryLevelSystemCall(), EventType.BEGIN,
                        this.context));

        // create new stack frame for input parameter
        SimulatedStackHelper.createAndPushNewStackFrame(this.context.getStack(),
                entryLevelSystemCall.getInputParameterUsages_EntryLevelSystemCall());
        providedDelegationSwitch.doSwitch(entryLevelSystemCall.getProvidedRole_EntryLevelSystemCall());
        this.context.getStack().removeStackFrame();

        this.context.getRuntimeState().getEventNotificationHelper()
                .firePassedEvent(new ModelElementPassedEvent<EntryLevelSystemCall>(entryLevelSystemCall, EventType.END,
                        this.context));

        // FIXME We stick to single model elements here even though several would be needed to
        // uniquely identify the measuring point of interest (system + role + signature) [Lehrig]
        this.context.getRuntimeState().getEventNotificationHelper()
                .firePassedEvent(new ModelElementPassedEvent<OperationSignature>(
                        entryLevelSystemCall.getOperationSignature__EntryLevelSystemCall(), EventType.END,
                        this.context));
        return super.caseEntryLevelSystemCall(entryLevelSystemCall);
    }
        /**
     * @see org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch#caseUsageScenario(org.palladiosimulator.pcm.usagemodel.UsageScenario)
     */
    public T caseUsageScenario(final UsageScenario usageScenario) {
        this.context.getRuntimeState().getEventNotificationHelper().firePassedEvent(
                new ModelElementPassedEvent<UsageScenario>(usageScenario, EventType.BEGIN, this.context));
        final int stacksize = this.context.getStack().size();
        parentSwitch.doSwitch(usageScenario.getScenarioBehaviour_UsageScenario());
        if (this.context.getStack().size() != stacksize) {
            throw new PCMModelInterpreterException("Interpreter did not pop all pushed stackframes");
        }
        this.context.getRuntimeState().getEventNotificationHelper().firePassedEvent(
                new ModelElementPassedEvent<UsageScenario>(usageScenario, EventType.END, this.context));
        return super.caseUsageScenario(usageScenario);
      }

}