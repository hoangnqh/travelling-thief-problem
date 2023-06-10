package algorithms.tour;

import models.ConfigHelper;
import models.Instance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinKernighan {
    /**
     * use Lin-Kernighan TSP tour
     * uses hardcoded tours
     */
    public static int[] get(Instance instance) {
        int nbCities = instance.numOfCities;
        int cnt = 0;
        int[] tour = new int[instance.numOfCities];

        String fileName = instance.fileName.split("_", 2)[0];
        String dirName = ConfigHelper.getProperty("lktours");
        fileName += ".linkern.tour";
        //Deb.echo(dirName + "/" + fileName);

        File file = new File(dirName + "/" + fileName);
        if (!file.exists()) return  null;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            // scan tour
            while ((line = br.readLine()) != null) {

                if (line.startsWith("TOUR_SECTION")) {

                    for (int j=0; j<instance.numOfCities; j++) {
                        line = br.readLine();
                        tour[cnt++] = Integer.parseInt(line) - 1;
                    }
                }
            }

            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return tour;
    }
}
