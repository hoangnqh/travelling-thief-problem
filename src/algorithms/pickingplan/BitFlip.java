package algorithms.pickingplan;

import models.Instance;
import models.Solution;

import java.util.Random;

public class BitFlip {
    public static void optimize(Instance instance, int[] tour, boolean[] pickingPlan, Random random){
        int nbCities = instance.numOfCities;
        Solution solution = instance.evaluate(tour, pickingPlan);
        double bestZ = solution.Z;

        int deltaP, deltaW;
        long fp;
        int origBF;
        double ft, G;
        int r;
        long wc;

        double maxSpeed = instance.maxSpeed;
        double minSpeed = instance.minSpeed;
        long capacity = instance.capacity;
        double C = (maxSpeed - minSpeed) / capacity;
        double R = instance.R;


        boolean improved = true;
        while(improved){
            improved = false;
            double newBestZ = bestZ;
            int pos = 0;
            for (int i = 0; i < instance.numOfItems; i++){
                // stop execution if interrupted
                if (Thread.currentThread().isInterrupted()) return;

                if (solution.pickingPlan[i] == false && instance.weight[i] > solution.remainingCapacity) continue;

                // calculate deltaP and deltaW
                if (solution.pickingPlan[i] == false) {
                    deltaP = instance.profit[i];
                    deltaW = instance.weight[i];
                } else {
                    deltaP = -instance.profit[i];
                    deltaW = -instance.weight[i];;
                }
                fp = solution.profit + deltaP;

                // handle velocity constraint
                // index where Bit-Flip happened
                origBF = solution.cityOrder[instance.cityOfItem[i]];

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

                if (G > newBestZ){
                    newBestZ = G;
                    pos = i;
                }
            }

            if (newBestZ > bestZ){
                bestZ = newBestZ;
                improved = true;
                if (pickingPlan[pos]) pickingPlan[pos] = false;
                else pickingPlan[pos] = true;
//                System.out.println("BitFlip "+bestZ);
                solution = instance.evaluate(tour, pickingPlan);
            }
        }
    }
}
