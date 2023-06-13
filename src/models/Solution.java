package models;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public int[] tour = null;
    public boolean[] pickingPlan = null;
    public double Z = Double.NEGATIVE_INFINITY;
    public double time = Double.POSITIVE_INFINITY;
    public long profit = Long.MIN_VALUE;
    public long remainingCapacity = -1;
    public int[] cityOrder = null;

    // Thoi gian di toi thanh pho thu i
    public double[] timeAcc = null;
    // Can nang khi di toi thanh pho thu i
    public long[] weightAcc = null;
    public long[] ZAcc = null;

    public Solution(){}

    @Override
    public String toString(){
        String s = "Final Z = "+Double.toString(Z)+'\n';
        s += "Tour        : ";
        for (int city: tour) s += Integer.toString(city)+" ";
        s += '\n'+"Picking plan: ";
        for (int i = 0; i < pickingPlan.length; i++){
            if (pickingPlan[i]) s += Integer.toString(i)+" ";
        }
        s += '\n';
        return s;
    }
}
