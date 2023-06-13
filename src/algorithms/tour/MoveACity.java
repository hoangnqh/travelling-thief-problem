package algorithms.tour;

import models.Instance;
import models.Solution;

import java.util.Random;

public class MoveACity {
    public static void optimize(Instance instance, int[] tour, boolean[] pickingPlan, Random random){
        Solution solution = instance.evaluate(tour, pickingPlan);
        boolean improvement = true;
        while (improvement){
            improvement = false;
            for (int i = 1; i < instance.numOfCities; i++){
                for (int j = 1; j < instance.numOfCities; j++){
                    int[] newTour = tour.clone();
                    int pos = i;
                    while (pos < j){
                        int temp = newTour[pos];
                        newTour[pos] = newTour[pos + 1];
                        newTour[pos + 1] = temp;
                        pos += 1;
                    }
                    while (pos > j){
                        int temp = newTour[pos];
                        newTour[pos] = newTour[pos - 1];
                        newTour[pos - 1] = temp;
                        pos -= 1;
                    }
                    Solution newSolution = instance.evaluate(newTour, pickingPlan);
                    if (newSolution.Z > solution.Z){
                        improvement = true;
                        solution.Z = newSolution.Z;
                        System.arraycopy(newTour, 0, tour, 0, tour.length);
                        System.out.println("MAC "+solution.Z);
                    }
                }
            }
        }
    }
}
