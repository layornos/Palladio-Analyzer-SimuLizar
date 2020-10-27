package org.palladiosimulator.simulizar.utils;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;

public interface ITransitionDeterminer {
	/**
     * Determines a branch transition in the list of branch transitions. The list can only contains
     * either probabilistic or guarded branch transitions.
     *
     * @param abstractBranchTransitions
     *            the list with branch transitions.
     * @return the determined AbstractBranchTransition.
     */
    public AbstractBranchTransition determineTransition(
            final EList<AbstractBranchTransition> abstractBranchTransitions);
}
