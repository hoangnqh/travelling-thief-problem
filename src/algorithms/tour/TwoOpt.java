package algorithms.tour;

import models.Instance;
import models.Solution;

import java.util.Arrays;
import java.util.Random;

public class TwoOpt {
    public static void optimize(Instance instance, int[] tour, boolean[] pickingPlan, Random random){
        Solution solution = instance.evaluate(tour, pickingPlan);
        double bestZ = solution.Z;
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            for (int i = 1; i < tour.length - 1; i++) {

                // stop execution if interrupted
                if (Thread.currentThread().isInterrupted()) return;

                for (int j = i + 1; j < tour.length; j++) {
                    int[] newTour = swapTour(tour, i, j);
                    Solution newSolution = instance.evaluate(newTour, pickingPlan);
                    if (newSolution.Z > bestZ) {
//                        System.out.println("2-opt "+newSolution.Z);
//                        improvement = true;
                        bestZ = newSolution.Z;
                        for (int k = 0; k < tour.length; k++){
                            tour[k] = newTour[k];
                        }
                    }
                }
            }
        }
    }

    public static int[] swapTour(int[] tour, int i, int j){
        int[] newTour = Arrays.copyOf(tour, tour.length);
        while (i < j) {
            int temp = newTour[i];
            newTour[i] = newTour[j];
            newTour[j] = temp;
            i++;
            j--;
        }
        return newTour;
    }
}
