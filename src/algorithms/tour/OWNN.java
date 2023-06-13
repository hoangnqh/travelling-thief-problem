package algorithms.tour;

import models.Instance;
import models.OtherFunction;
import models.Solution;

import java.util.Arrays;
import java.util.Random;

// Optimize with nearest neighbor
public class OWNN {
    public static void optimize(Instance instance, int[] tour, boolean[] pickingPlan, Random random){
        Solution solution = instance.evaluate(tour, pickingPlan);
        boolean improvement = true;
        while (improvement){
            improvement = false;
            double bestZ = solution.Z;
            int[] bestTour = new int[instance.numOfItems];
            for (int i = 0; i < instance.numOfCities; i++){
                int pos = 0;
                int bestDis = Integer.MAX_VALUE;
                for (int j = 1; j < instance.numOfCities; j++){
                    if (j == i) continue;
                    int dis = instance.distance(tour[i], tour[j]);
                    if (dis < bestDis){
                        bestDis = dis;
                        pos = j;
                    }
                }
                int[] newTour = tour.clone();
                if (pos > i){
                    int temp = tour[pos];
                    for (int j = pos; j > i + 1; j--){
                        newTour[j] = newTour[j - 1];
                    }
                    newTour[i + 1] = temp;
                }
                else {
                    int temp = tour[pos];
                    for (int j = pos; j < i; j++){
                        newTour[j] = newTour[j + 1];
                    }
                    newTour[i] = temp;
                }
                Solution newSolution = instance.evaluate(newTour, pickingPlan);
                if (newSolution.Z > bestZ){
//                    improvement = true;
                    bestZ = newSolution.Z;
                    bestTour = newTour;
//                    System.arraycopy(newTour, 0, tour, 0, tour.length);
                    System.out.println("OWNN "+solution.Z);
//                    break;
                }
            }
            if (bestZ > solution.Z){
                improvement = true;
                solution.Z = bestZ;
                System.arraycopy(bestTour, 0, tour, 0, tour.length);
            }
        }

    }
}
