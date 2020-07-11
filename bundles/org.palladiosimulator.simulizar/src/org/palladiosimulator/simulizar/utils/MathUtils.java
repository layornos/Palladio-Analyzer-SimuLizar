package org.palladiosimulator.simulizar.src.org.palladiosimulator.simulizar.utils;

import java.util.ArrayList;
import java.util.List;
import de.uka.ipd.sdq.simucomframework.SimuComConfig;

public class MathUtils {
    private MathUtils(){
        
    }
    /**
     * Method calculates a random index for the given list of summed probabilities.
     *
     * @param summedProbabilityList
     *            a list of summed probabilities.
     * @param simuComConfig
     *            the SimuCom config.
     * @return a random index, or -1 if summedProbabilityList is empty, or no index can be
     *         determined.
     */
    public static int getRandomIndex(final List<Double> summedProbabilityList,
            final SimuComConfig simuComConfig) {
        if (summedProbabilityList.size() == 0) {
            return -1;
        }

        final double lastSum = summedProbabilityList.get(summedProbabilityList.size() - 1);
        final double randomNumer = simuComConfig.getRandomGenerator().random();

        // get branch
        int i = 0;
        for (final Double sum : summedProbabilityList) {
            if (lastSum * randomNumer < sum) {
                return i;
            }
            i++;
        }
        return -1;
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
    public static List<Double> createSummedProbabilityList(final List<Double> branchProbabilities) {
        double currentSum = 0;
        final List<Double> summedProbabilityList = new ArrayList<Double>();
        for (final Double probability : branchProbabilities) {
            summedProbabilityList.add((currentSum = currentSum + probability));
        }
        return summedProbabilityList;
    }
}
