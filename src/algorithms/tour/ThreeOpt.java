package algorithms.tour;

import models.Instance;
import models.OtherFunction;
import models.Solution;

import java.util.Arrays;
import java.util.Random;

public class ThreeOpt {
    public static void optimize(Instance instance, int[] tour, boolean[] pickingPlan, Random random){
        Solution solution = instance.evaluate(tour, pickingPlan);
        double bestZ = solution.Z;
        boolean improvement = true;

        // https://github.com/ozanyerli/tsp3opt/blob/main/tsp3opt.c
        int i, j, k;
        int n = instance.numOfCities;
        while (improvement){
            improvement = false;
            int c1, c2, c3;
            double newBestZ = bestZ;
            int[] newTour = new int[n];
            loops:
            for (c1 = 0; c1 < n; c1++){
                i = c1;

                for (c2 = 1; c2 < n - 2; c2++) {
                    j = (i + c2) % n;

                    // Case 3
                    int []tempTour = tour.clone();
                    OtherFunction.reverseSegment(tempTour, (i + 1) % n, j);
                    Solution solution3 = instance.evaluate(tempTour, pickingPlan, true);
                    if (solution3.Z > bestZ){
                        improvement = true;
                        bestZ = solution3.Z;
                        System.arraycopy(solution3.tour, 0, tour, 0, tour.length);
                        System.out.println("3.1-opt "+solution3.Z);
//                        newTour = solution3.tour;
                        break loops;
                    }

                    for (c3 = c2 + 1; c3 < n; c3++) {
                        k = (i + c3) % n;

                        // stop execution if interrupted
                        if (Thread.currentThread().isInterrupted()) return;

                        // Case 1
                        tempTour = tour.clone();
                        OtherFunction.reverseSegment(tempTour, (k + 1) % n, i);
                        Solution solution1 = instance.evaluate(tempTour, pickingPlan, true);

                        // Case 2
                        tempTour = tour.clone();
                        OtherFunction.reverseSegment(tempTour, (j + 1) % n, k);
                        Solution solution2 = instance.evaluate(tempTour, pickingPlan, true);
                        if (solution1.Z > solution2.Z) solution1 = solution2;

                        // Case 4
                        tempTour = tour.clone();
                        OtherFunction.reverseSegment(tempTour, (j + 1) % n, k);
                        OtherFunction.reverseSegment(tempTour, (i + 1) % n, j);
                        Solution solution4 = instance.evaluate(tempTour, pickingPlan, true);
                        if (solution1.Z > solution4.Z) solution1 = solution4;

                        // Case 5
                        tempTour = tour.clone();
                        OtherFunction.reverseSegment(tempTour, (k + 1) % n, i);
                        OtherFunction.reverseSegment(tempTour, (i + 1) % n, j);
                        Solution solution5 = instance.evaluate(tempTour, pickingPlan, true);
                        if (solution1.Z > solution5.Z) solution1 = solution5;

                        // Case 6
                        tempTour = tour.clone();
                        OtherFunction.reverseSegment(tempTour, (k + 1) % n, i);
                        OtherFunction.reverseSegment(tempTour, (j + 1) % n, k);
                        Solution solution6 = instance.evaluate(tempTour, pickingPlan, true);
                        if (solution1.Z > solution6.Z) solution1 = solution6;

                        // Case 7
                        tempTour = tour.clone();
                        OtherFunction.reverseSegment(tempTour, (k + 1) % n, i);
                        OtherFunction.reverseSegment(tempTour, (i + 1) % n, j);
                        OtherFunction.reverseSegment(tempTour, (j + 1) % n, k);
                        Solution solution7 = instance.evaluate(tempTour, pickingPlan, true);
                        if (solution1.Z > solution7.Z) solution1 = solution7;

                        if (solution1.Z > bestZ){
                            improvement = true;
                            bestZ = solution1.Z;
                            System.arraycopy(solution1.tour, 0, tour, 0, tour.length);
                            System.out.println("3.2-opt "+solution1.Z);
//                            newTour = solution1.tour;
                            break loops;
                        }
                    }
                }
            }
//            if (newBestZ > bestZ){
//                improvement = true;
//                bestZ = newBestZ;
//                System.arraycopy(newTour, 0, tour, 0, tour.length);
//            }
        }
    }
}
