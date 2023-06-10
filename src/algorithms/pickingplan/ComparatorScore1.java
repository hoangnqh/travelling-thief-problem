package algorithms.pickingplan;

import models.Instance;
import models.ItemInTour;

import java.util.Comparator;

public class ComparatorScore1 implements Comparator<ItemInTour> {
    Instance instance = null;
    public  ComparatorScore1(Instance instance){
        this.instance = instance;
    }
    @Override
    public int compare(ItemInTour a, ItemInTour b) {
        return Double.compare(getScore(a), getScore(b));
    }
    private double getScore(ItemInTour a){
        double t = (double) a.distance / (instance.maxSpeed - ((instance.maxSpeed - instance.minSpeed) / instance.capacity) * a.weight);
        return (double) a.profit - instance.R * t;
    }
}
