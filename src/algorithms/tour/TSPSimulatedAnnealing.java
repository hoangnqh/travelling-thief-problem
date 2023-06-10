package algorithms.tour;

import models.Instance;
import models.OtherFunction;
import models.Solution;

import java.util.Arrays;
import java.util.Random;

public class TSPSimulatedAnnealing {
    public static void optimize(Instance instance, int[] tour, boolean[] pickingPlan, Random random) {
        int T_abs = 1;
        double alpha = 0.9578;
        int T0 = 98;

        // copy initial solution into improved solution
        Solution solution = instance.evaluate(tour, pickingPlan, true);

        // best solution
        double GBest = solution.Z;
        double bestZ = solution.Z;

        // neighbor solution
        int c1, c2;
        int nbIter = 0;

        double T = T0;
        long trials = (long) instance.numOfCities;


        //===============================================
        // start simulated annealing process
        //===============================================
        do {
            nbIter++;

            // cleanup and stop execution if interrupted
            if (Thread.currentThread().isInterrupted()) return;

            for (int u=0; u<trials; u++) {

                // browse items randomly
                c1 = OtherFunction.randInt(1, instance.numOfCities - 2, random);
                c2 = OtherFunction.randInt(c1 + 1, instance.numOfCities - 1, random);

                int[] newTour = TwoOpt.swapTour(tour, c1, c2);

                Solution newSolution = instance.evaluate(newTour, pickingPlan);


                double mu = random.nextDouble();
                double energy_gap = newSolution.Z - GBest;
                boolean acceptance = energy_gap > 0.1 || Math.exp(energy_gap / T) > mu;
                if (acceptance) {
                    GBest = newSolution.Z;
                    solution = instance.evaluate(newTour, pickingPlan, true);
                }

            }

            // update best if improvement
            if (solution.Z > bestZ) {
                System.out.println("TSPSA "+solution.Z);
                for (int i = 0; i < instance.numOfCities; i++){
                    tour[i] = solution.tour[i];
                }
                bestZ = solution.Z;;
            }

            // cool down temperature
            T = T * alpha;

            // stop when temperature reach absolute value
        } while (T > T_abs);
    }
}
