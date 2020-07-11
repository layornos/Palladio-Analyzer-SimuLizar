package org.palladiosimulator.simulizar.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.GuardedBranchTransition;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import org.palladiosimulator.simulizar.interpreter.InterpreterDefaultContext;
import org.palladiosimulator.simulizar.src.org.palladiosimulator.simulizar.utils.MathUtils;
import de.uka.ipd.sdq.simucomframework.SimuComConfig;

/**
 *
 * Util class to determine a transition based on probabilities.
 *
 * @author Joachim Meyer
 *
 */
public class TransitionDeterminer {

    protected static final Logger LOGGER = Logger.getLogger(TransitionDeterminer.class.getName());

    private final SimuComConfig config;
    private final InterpreterDefaultContext context;
    private TransitionDeterminerPIUsageDelegate piDelegate;
    private TransitionDeterminerDeltaBehaviourSeffDelegate seffDelegate;
    /**
     * Constructor.
     *
     * @param config
     *            the SimuCom config for the random generator.
     * @param modelInterpreter
     *            the calling model interpreter.
     */
    public TransitionDeterminer(final InterpreterDefaultContext context) {
        super();
        this.config = context.getModel().getConfiguration();
        this.context = context;
    }



    /**
     * Sums the probabilities of the list of probabilities. In a list of summed probabilities, each
     * value of an element in the list has its own probability added by the previous probability.
     * Means, if the the first probabilities in the list of probabilities is 0.3, the value of the
     * first element in the summed probability list is 0.3. If the second probabilities in the list
     * of probabilities is 0.4, the corresponding value in the summed probability list is 0.4+0.3
     * and so on.
     *
     * @param branchProbabilities
     *            a list with branch probabilities.
     * @return the summed probability list.
     */
    protected List<Double> createSummedProbabilityList(final List<Double> branchProbabilities) {
        return MathUtils.createSummedProbabilityList(branchProbabilities);
    }

    /**
     * Determines a branch transition out of a list of branch transitions, with respect to their
     * probabilities.
     *
     * @param branchTransitions
     *            the list of branch transition.
     * @return a branch transition.
     */
    public BranchTransition determineBranchTransition(final EList<BranchTransition> branchTransitions) {
        return piDelegate.determineBranchTransition(branchTransitions);
    }

    /**
     * Determines a guarded branch transition out of a list of guarded branch transitions.
     *
     * @param guardedBranchTransitions
     *            the list of guarded branch transition.
     * @return a guarded branch transition. This is the branch transition whose condition holds
     *         first.
     */
    private GuardedBranchTransition determineGuardedBranchTransition(
            final EList<AbstractBranchTransition> guardedBranchTransitions) {
        return seffDelegate.determineGuardedBranchTransition(guardedBranchTransitions);
    }

    /**
     * Determines a probabilistic branch transition out of a list of probabilistic branch
     * transitions, with respect to their probabilities.
     *
     * @param probabilisticBranchTransitions
     *            the list of probabilistic branch transition.
     * @return a probabilistic branch transition.
     */
    public ProbabilisticBranchTransition determineProbabilisticBranchTransition(
            final EList<AbstractBranchTransition> probabilisticBranchTransitions) {
        return seffDelegate.determineProbabilisticBranchTransition(probabilisticBranchTransitions);
    }



    /**
     * Determines a branch transition in the list of branch transitions. The list can only contains
     * either probabilistic or guarded branch transitions.
     *
     * @param abstractBranchTransitions
     *            the list with branch transitions.
     * @return the determined AbstractBranchTransition.
     */
    public AbstractBranchTransition determineTransition(
            final EList<AbstractBranchTransition> abstractBranchTransitions) {
        /*
         * Mixed types with branch is not allowed, so the following is sufficient
         */
        AbstractBranchTransition branchTransition = null;
        if (abstractBranchTransitions.get(0) instanceof ProbabilisticBranchTransition) {
            LOGGER.debug("Found ProbabilisticBranchTransitions");
            branchTransition = this.determineProbabilisticBranchTransition(abstractBranchTransitions);

        } else {
            LOGGER.debug("Found GuardedBranchTransitions");
            branchTransition = this.determineGuardedBranchTransition(abstractBranchTransitions);
        }
        return branchTransition;
    }

    /**
     * Extracts the probabilities of a list of ProbabilisticBranchTransition.
     *
     * @param probabilisticBranchTransitions
     *            the list of ProbabilisticBranchTransition.
     * @return a list only containing the probabilities.
     */
    protected List<Double> extractProbabiltiesRDSEFF(
            final EList<AbstractBranchTransition> probabilisticBranchTransitions) {
    return seffDelegate.extractProbabiltiesRDSEFF(probabilisticBranchTransitions);
    }

    /**
     * Extracts the probabilities of a list of BranchTransition.
     *
     * @param branchTransitions
     *            the list of BranchTransition.
     * @return a list only containing the probabilities.
     */
    protected List<Double> extractProbabiltiesUsageModel(final EList<BranchTransition> branchTransitions) {
        return piDelegate.extractProbabiltiesUsageModel(branchTransitions);
    }
}
