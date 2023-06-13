package algorithms;

import algorithms.pickingplan.ComparatorScore1;
import algorithms.pickingplan.FillSack;
import algorithms.pickingplan.SimulatedAnnealing;
import algorithms.pickingplan.FixPickingPlan;
import algorithms.tour.*;
import models.Instance;
import models.ItemInTour;
import models.OtherFunction;
import models.Solution;

import java.util.List;
import java.util.Random;

public class Algo2 implements Algorithm{
    @Override
    public Solution solve(Instance instance, int randomSeed){
        Random random = new Random(randomSeed);

        // Using LKH to find TSP
//        int[] tour = NearestNeighbor.get(instance);
        int[] tour = LinKernighan.get(instance);
        if (tour == null) return new Solution();

        // Get a pickingPlan using a greedy algorithm
        boolean[] pickingPlan = new boolean[instance.numOfItems];
        for (int i = 0; i < instance.numOfItems; i++){
            pickingPlan[i] = true;
        }
        FixPickingPlan.fix(instance, tour, pickingPlan);
//        BitFlip.optimize(instance, tour, pickingPlan, random);

        Solution solution = instance.evaluate(tour, pickingPlan);
        System.out.println("Start: "+solution.Z);
        boolean improved;
        do {
            // stop execution if interrupted
            if (Thread.currentThread().isInterrupted()) return solution;

            improved = false;

            TwoOpt.optimize(instance, tour, pickingPlan, random);
            TSPSimulatedAnnealing.optimize(instance, tour, pickingPlan, random);
            OWNN.optimize(instance, tour, pickingPlan, random);

//            MoveACity.optimize(instance, tour, pickingPlan, random);
//            ThreeOpt.optimize(instance, tour, pickingPlan, random);

            // stop execution if interrupted
            if (Thread.currentThread().isInterrupted()) return solution;

            // Improve pickingPlan base on tour
//            BitFlip.optimize(instance, tour, pickingPlan, random);

            SimulatedAnnealing.optimize(instance, tour, pickingPlan, random);

            // Update best if improvement
            Solution newSolution = instance.evaluate(tour, pickingPlan);
            if (newSolution.Z > solution.Z) {
                improved = true;
                solution = instance.evaluate(tour, pickingPlan, true);
            }
        }
        while (improved);

        return solution;
    }
}
