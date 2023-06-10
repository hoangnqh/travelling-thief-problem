package models;

public class ItemInTour {
    // position of item
    public int pos;
    public int profit;
    public int weight;
    // Distance of this city to last city (city 0)
    public long distance;
    public ItemInTour(int pos, int profit, int weight, long distance){
        this.pos = pos;
        this.profit = profit;
        this.weight = weight;
        this.distance = distance;
    }

    @Override
    public String toString(){
        return "pos = "+this.pos+", profit = "+this.profit+", weight = "+this.weight+", distance = "+this.distance;
    }
}
