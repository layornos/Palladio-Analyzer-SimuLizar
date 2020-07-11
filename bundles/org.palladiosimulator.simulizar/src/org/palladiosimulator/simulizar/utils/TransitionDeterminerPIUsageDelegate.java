package org.palladiosimulator.simulizar.utils;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import org.palladiosimulator.simulizar.src.org.palladiosimulator.simulizar.utils.MathUtils;
import org.eclipse.emf.common.util.EList;

public class TransitionDeterminerPIUsageDelegate {

  private static final Logger LOGGER = Logger.getLogger(TransitionDeterminerPIUsageDelegate.class.getName());
  private final SimuComConfig config;

  public TransitionDeterminerPIUsageDelegate(SimuComConfig config) {
    this.config = config;
  }
      /**
     * Extracts the probabilities of a list of BranchTransition.
     *
     * @param branchTransitions
     *            the list of BranchTransition.
     * @return a list only containing the probabilities.
     */
    public List<Double> extractProbabiltiesUsageModel(final EList<BranchTransition> branchTransitions) {
      final List<Double> probabilityList = new ArrayList<Double>();
      for (final BranchTransition branchTransition : branchTransitions) {
          probabilityList.add(branchTransition.getBranchProbability());
      }
      return probabilityList;
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
      final List<Double> summedProbabilityList = MathUtils
              .createSummedProbabilityList(this.extractProbabiltiesUsageModel(branchTransitions));

      final int transitionIndex = MathUtils.getRandomIndex(summedProbabilityList, this.config);

      final BranchTransition branchTransition = branchTransitions.get(transitionIndex);
      if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Chosen branch transition " + transitionIndex + " " + branchTransition);
      }
      return branchTransition;
  }



}