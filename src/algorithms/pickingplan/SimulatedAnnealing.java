package algorithms.pickingplan;

import models.Instance;
import models.OtherFunction;
import models.Solution;

import java.util.Arrays;
import java.util.Random;

public class SimulatedAnnealing {
    public static void optimize(Instance instance, int[] tour, boolean[] pickingPlan, Random random){
        int T_abs = 1;
        double alpha = 0.9578;
        int T0 = 98;
        int nbItems = instance.numOfItems;
        double trialFactor = generateTFLinFit(nbItems);

        // copy initial solution into improved solution
        Solution solution = instance.evaluate(tour, pickingPlan, true);

        // TTP data
        int nbCities = instance.numOfCities;
//        int[] A = ttp.getAvailability();
        double maxSpeed = instance.maxSpeed;
        double minSpeed = instance.minSpeed;
        long capacity = instance.capacity;
        double C = (maxSpeed - minSpeed) / capacity;
        double R = instance.R;


        // delta parameters
        long deltaP, deltaW;

        // best solution
        double GBest = solution.Z;
        double bestZ = solution.Z;

        // neighbor solution
        long fp;
        double ft, G;
        long wc;
        int origBF;
        int k, r;
        int nbIter = 0;

        double T = T0;

        long trials = Math.round(nbItems*trialFactor);
//        System.out.println("Trials: "+trials);
//        trials = 200000000 / nbCities;
//        System.out.println("Trials: "+trials);
        

        //===============================================
        // start simulated annealing process
        //===============================================
        do {
            nbIter++;

            // cleanup and stop execution if interrupted
            if (Thread.currentThread().isInterrupted()) return;

            for (int u=0; u<trials; u++) {

                // browse items randomly
                k = OtherFunction.randInt(0, nbItems - 1, random);

                // check if new weight doesn't exceed knapsack capacity
                if (solution.pickingPlan[k] == false && instance.weight[k] > solution.remainingCapacity) continue;

                // calculate deltaP and deltaW
                if (solution.pickingPlan[k] == false) {
                    deltaP = instance.profit[k];
                    deltaW = instance.weight[k];
                } else {
                    deltaP = -instance.profit[k];
                    deltaW = -instance.weight[k];;
                }
                fp = solution.profit + deltaP;

                // handle velocity constraint
                // index where Bit-Flip happened
//                origBF = sol.mapCI[A[k] - 1];
                origBF = solution.cityOrder[instance.cityOfItem[k]];
                // starting time
                ft = origBF == 0 ? .0 : solution.timeAcc[origBF - 1];
                // recalculate velocities from bit-flip city
                // to recover objective value
                for (r = origBF; r < nbCities; r++) {
                    wc = solution.weightAcc[r] + deltaW;
                    ft += (double) instance.distance(tour[r], tour[(r + 1) % nbCities]) / (maxSpeed - wc * C);
                }
                // compute recovered objective value
                G = fp - ft * R;

                //=====================================
                // update if improvement or
                // Boltzmann condition satisfied
                //=====================================
//                double mu = Math.random();
                double mu = random.nextDouble();
                double energy_gap = G - GBest;
                boolean acceptance = energy_gap > 0 || Math.exp(energy_gap / T) > mu;
                if (acceptance) {

                    GBest = G;

                    // bit-flip
                    solution.pickingPlan[k] = solution.pickingPlan[k] != false ? false : true;

                    //===========================================================
                    // recover accumulation vectors
                    //===========================================================
                    if (solution.pickingPlan[k] != false) {
                        deltaP = instance.profit[k];
                        deltaW = instance.weight[k];;
                    } else {
                        deltaP = -instance.profit[k];
                        deltaW = -instance.weight[k];;
                    }
                    fp = solution.profit + deltaP;
//                    origBF = sol.mapCI[A[k] - 1];
                    origBF = solution.cityOrder[instance.cityOfItem[k]];
                    ft = origBF == 0 ? 0 : solution.timeAcc[origBF - 1];
                    for (r = origBF; r < nbCities; r++) {
                        // recalculate velocities from bit-flip city
                        wc = solution.weightAcc[r] + deltaW;
                        ft += (double) instance.distance(tour[r], tour[(r + 1) % nbCities]) / (maxSpeed - wc * C);
                        // recover wacc and tacc
                        solution.weightAcc[r] = wc;
                        solution.timeAcc[r] = ft;
                    }
                    G = fp - ft * R;
                    solution.Z = G;
                    solution.profit = fp;
                    solution.time = ft;
                    solution.remainingCapacity = capacity - solution.weightAcc[nbCities - 1];
                    //===========================================================

                }

            }

            // update best if improvement
            if (solution.Z > bestZ) {
//                System.out.println("SA "+solution.Z);
                for (int i = 0; i < instance.numOfItems; i++){
                    pickingPlan[i] = solution.pickingPlan[i];
                }
                // solution = instance.evaluate(tour, solution.pickingPlan, true);
                bestZ = solution.Z;;
            }

            // cool down temperature
            T = T * alpha;

            // stop when temperature reach absolute value
        } while (T > T_abs);
    }

    public static double generateTFLinFit(int xi) {
        int i=-1;
//    int[] z = new int[]{ 1,  75, 375, 790, 2102, 15111, 70250, 140500, 338090};

//    int[] x = new int[]{         1,   75,    375,  790,  2102, 15111, 70250, 140500,  338090};
//    double[] y = new double[]{57872,  13896,  700,   350,    16,     1,   0.16,  0.0493,   0.03};

        int[] x = new int[]{ 1, 130, 496, 991, 3038, 18512, 75556, 169046, 338090};
        double[] y = new double[]{57872,  13896,  700,   350,    16,     1,   0.16,  0.0493,   0.03};


        int n=y.length;
        for (int k=0; k<n-1; k++) {
            if (x[k] <= xi && xi < x[k + 1]) {
                i = k;
                break;
            }
        }
        if (xi <= x[0]) {
            return 57872.0;
        }
        if (xi >= x[n-1]) {
            return 0.03;
        }

        double m = ( y[i]-y[i+1] ) / ( x[i]-x[i+1] );
        double b = y[i]-m*x[i];

        double yi = m*xi + b;

        return yi;
    }
}
