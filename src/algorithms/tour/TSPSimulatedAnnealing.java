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


            for (int u=0; u<trials; u++) {
                // cleanup and stop execution if interrupted
                if (Thread.currentThread().isInterrupted()) return;

                int i = OtherFunction.randInt(1, instance.numOfCities - 1, random);

                double tempBestZ = -Double.MAX_VALUE;
                int[] newTour = new int[instance.numOfCities];


                for (int j = 1; j < instance.numOfCities; j++){
                    int[] tempTour = solution.tour.clone();
                    int pos = i;
                    while (pos < j){
                        int temp = tempTour[pos];
                        tempTour[pos] = tempTour[pos + 1];
                        tempTour[pos + 1] = temp;
                        pos += 1;
                    }
                    while (pos > j){
                        int temp = tempTour[pos];
                        tempTour[pos] = tempTour[pos - 1];
                        tempTour[pos - 1] = temp;
                        pos -= 1;
                    }
                    int cnt0 = 0;
                    for (int cc: tempTour) if (cc == 0) cnt0 += 1;
                    Solution tempSolution = instance.evaluate(tempTour, pickingPlan);
                    if (tempSolution.Z > tempBestZ){
                        tempBestZ = tempSolution.Z;
                        newTour = tempTour.clone();
                    }

                }

                double mu = random.nextDouble();
                double energy_gap = tempBestZ - GBest;
                boolean acceptance = energy_gap > 0.1 || Math.exp(energy_gap / T) > mu;
                if (acceptance) {
                    GBest = tempBestZ;
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
                solution = instance.evaluate(tour, pickingPlan, true);
            }

            // cool down temperature
            T = T * alpha;

            // stop when temperature reach absolute value
        } while (T > T_abs);
    }
}
