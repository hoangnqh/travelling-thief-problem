package algorithms.tour;

import models.Instance;
import models.Solution;

import java.util.Arrays;
import java.util.Random;

public class ThreeOpt {
    public static void optimize(Instance instance, int[] tour, boolean[] pickingPlan, Random random){
        Solution solution = instance.evaluate(tour, pickingPlan);
        double bestZ = solution.Z;
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            // c1: city 1, c2: city 2, c3: city 3
            for (int c1 = 1; c1 < tour.length - 2; c1++) {
                for (int c2 = c1 + 1; c2 < tour.length; c2++) {
                    // stop execution if interrupted
                    if (Thread.currentThread().isInterrupted()) return;

                    // Equivalent to 2-opt
                    int[] c1c2 = TwoOpt.swapTour(tour, c1, c2);
                    Solution newSolution = instance.evaluate(c1c2, pickingPlan);
                    if (newSolution.Z > bestZ){
                        System.out.println("2-opt "+newSolution.Z);
                        bestZ = newSolution.Z;
                        System.arraycopy(tour, 0, c1c2, 0, tour.length);
                        improvement = true;
                        break;
                    }

                    for (int c3 = c2 + 1; c3 < tour.length; c3++) {
                        int[] c1c2_c2c3 = TwoOpt.swapTour(c1c2, c2, c3);
                        newSolution = instance.evaluate(c1c2_c2c3, pickingPlan);
                        if (newSolution.Z > bestZ){
                            System.out.println("3.1-opt "+newSolution.Z);
                            bestZ = newSolution.Z;
                            System.arraycopy(tour, 0, c1c2_c2c3, 0, tour.length);
                            improvement = true;
                            break;
                        }

                        int[] c1c3 = TwoOpt.swapTour(tour, c1, c3);
                        int[] c1c3_c2c3 = TwoOpt.swapTour(c1c3, c2, c3);
                        newSolution = instance.evaluate(c1c3_c2c3, pickingPlan);
                        if (newSolution.Z > bestZ){
                            System.out.println("3.2-opt "+newSolution.Z);
                            bestZ = newSolution.Z;
                            System.arraycopy(tour, 0, c1c3_c2c3, 0, tour.length);
                            improvement = true;
                            break;
                        }
                    }
                    if (improvement) break;
                }
                if (improvement) break;
            }
        }
    }


}
