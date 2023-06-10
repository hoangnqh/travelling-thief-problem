package algorithms.pickingplan;

import models.ItemInTour;

import java.util.Comparator;

public class ItemInTourComparator implements Comparator<ItemInTour> {
    public double alpha = 1.5;
    public ItemInTourComparator(double alpha){
        this.alpha = alpha;
    }

    @Override
    public int compare(ItemInTour a, ItemInTour b) {
        return Double.compare(getValue(a), getValue(b));
    }
    private double getValue(ItemInTour a){
        return (double) Math.pow(a.profit, alpha) / (double) (Math.pow(a.weight, alpha) * a.distance);
    }
}
