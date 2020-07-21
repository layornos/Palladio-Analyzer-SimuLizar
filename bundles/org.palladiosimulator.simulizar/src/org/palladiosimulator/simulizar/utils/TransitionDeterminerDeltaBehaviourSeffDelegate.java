package org.palladiosimulator.simulizar.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.GuardedBranchTransition;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simulizar.interpreter.InterpreterDefaultContext;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import de.uka.ipd.sdq.simucomframework.variables.StackContext;

public class TransitionDeterminerDeltaBehaviourSeffDelegate {

  private final SimuComConfig config;
  private final InterpreterDefaultContext context;
  private static final Logger LOGGER =
      Logger.getLogger(TransitionDeterminerDeltaBehaviourSeffDelegate.class.getName());

  public TransitionDeterminerDeltaBehaviourSeffDelegate(InterpreterDefaultContext context, SimuComConfig config) {
    this.config = config;
    this.context = context;
  }

  public ProbabilisticBranchTransition determineProbabilisticBranchTransition(
      final EList<AbstractBranchTransition> probabilisticBranchTransitions) {
    final List<Double> summedProbabilityList = MathUtils.createSummedProbabilityList(
        this.extractProbabiltiesRDSEFF(probabilisticBranchTransitions));

    final int transitionIndex = MathUtils.getRandomIndex(summedProbabilityList, this.config);

    final ProbabilisticBranchTransition branchTransition =
        (ProbabilisticBranchTransition) probabilisticBranchTransitions.get(transitionIndex);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Chosen branch transition " + transitionIndex + " " + branchTransition);
    }
    return branchTransition;
  }

      /**
     * Determines a guarded branch transition out of a list of guarded branch transitions.
     *
     * @param guardedBranchTransitions
     *            the list of guarded branch transition.
     * @return a guarded branch transition. This is the branch transition whose condition holds
     *         first.
     */
    public GuardedBranchTransition determineGuardedBranchTransition(
            final EList<AbstractBranchTransition> guardedBranchTransitions) {

        /*
         * There is no predefined order in evaluating the guards attached to a BranchAction. So the
         * first guard which evaluates to true will be chosen.
         *
         * Further: As it is unclear for INNER variables in branch conditions if different or if the
         * same collection element is meant by the component developer, the current PCM version
         * forbids the use of INNER characterizations in branch conditions. Thus, this problem has
         * not to be addressed like in the collection iterator (EvaluationProxies and the same value
         * for all occurrences in one iteration).
         */
        int i = 0;
        GuardedBranchTransition branchTransition = null;
        for (final AbstractBranchTransition abstractBranchTransition : guardedBranchTransitions) {
            final GuardedBranchTransition guardedBranchTransition = (GuardedBranchTransition) abstractBranchTransition;
            final PCMRandomVariable condition = guardedBranchTransition.getBranchCondition_GuardedBranchTransition();

            if (this.conditionHolds(condition)) {
                branchTransition = (GuardedBranchTransition) guardedBranchTransitions.get(i);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Conditions holds for branch transition " + i + " " + branchTransition);
                }
                break;
            }
            i++;

        }
        return branchTransition;
    }
        /**
     * Checks whether the boolean expression in the condition holds or not.
     *
     * @param condition
     *            the condition (must be a boolean expression).
     * @return true if holds, otherwise false.
     */
    private boolean conditionHolds(final PCMRandomVariable condition) {
      return StackContext.evaluateStatic(condition.getSpecification(), Boolean.class,
              this.context.getStack().currentStackFrame());
  }
      /**
     * Extracts the probabilities of a list of ProbabilisticBranchTransition.
     *
     * @param probabilisticBranchTransitions
     *            the list of ProbabilisticBranchTransition.
     * @return a list only containing the probabilities.
     */
    public List<Double> extractProbabiltiesRDSEFF(
            final EList<AbstractBranchTransition> probabilisticBranchTransitions) {
        final List<Double> probabilityList = new ArrayList<Double>();
        for (final AbstractBranchTransition probabilisticBranchTransition : probabilisticBranchTransitions) {
            probabilityList.add(((ProbabilisticBranchTransition) probabilisticBranchTransition).getBranchProbability());
        }
        return probabilityList;
    }

}
