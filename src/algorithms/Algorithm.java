package algorithms;

import models.Instance;
import models.Solution;


public interface Algorithm {
       public Solution solve(Instance instance, int randomSeed);
}
