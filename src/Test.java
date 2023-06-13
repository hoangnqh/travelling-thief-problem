import models.OtherFunction;

public class Test {
    public static void main(String[] arg){
        int[] a = new int[]{0, 1, 2, 3, 4, 5};
        OtherFunction.reverseSegment(a, 4, 1);
        for (int i: a) System.out.print(i+" ");

    }
}
