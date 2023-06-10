import algorithms.Algo2;
import algorithms.Algorithm;
import algorithms.Algo1;
import algorithms.SimpleHeuristic;
import models.ConfigHelper;
import models.Instance;
import models.Solution;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class Runner {
    // List algorithm
    static Algorithm[] algorithmList = {
            new SimpleHeuristic(),
            new Algo1(), // better than SimpleHeuristic
            new Algo2()
    };
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            args = new String[]{"eil76_n75_bounded-strongly-corr_01.ttp", "algo2"};
        }
        String[] spl = args[0].split("_",2);

        // TTP instance name
        final String inst = args[0];
        Instance instance = Instance.readInstance(spl[0]+"-ttp/"+inst);

        // algorithm name
        String algoName = args[1];
        Algorithm algorithm = algorithmList[Integer.parseInt(ConfigHelper.getProperty(algoName))];

        // output file
        final String outputFile;
        if (args.length >= 3)
            outputFile = "./output/"+args[2];
        else
            outputFile = "./output/"+algoName+".csv";

        // runtime limit
        long runtimeLimit = 600;
        if (args.length >= 4)
            runtimeLimit = Long.parseLong(args[3]);

        // random seed
        int temp_seed = (int) (Math.random() * 1000000000);;
        if (args.length >= 5)
            temp_seed = Integer.parseInt((args[4]));
        int seed = temp_seed;


        // runnable class
        class TTPRunnable implements Runnable {

            String resultLine;
            Solution solution;

            @Override
            public void run() {


                /* start search & measure runtime */
                long startTime, stopTime;
                long exTime;
                startTime = System.currentTimeMillis();

                solution = algorithm.solve(instance, seed);

                stopTime = System.currentTimeMillis();
                exTime = stopTime - startTime;

                /* print result */
                resultLine = inst + " " + Math.round(solution.Z) + " " + (exTime/1000.0);

            }
        };

        // my TTP runnable
        TTPRunnable ttprun = new TTPRunnable();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Future<?> future = executor.submit(ttprun);
        executor.shutdown();  // reject all further submissions

        // limit execution time to 600 seconds
        try {
            future.get(runtimeLimit, TimeUnit.SECONDS);  // wait X seconds to finish
        } catch (InterruptedException e) {
            System.out.println("job was interrupted");
        } catch (ExecutionException e) {
            System.out.println("caught exception: " + e.getCause());
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("/!\\ Timeout");
        }

        // wait for execution to be done
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // print results
        System.out.println(ttprun.resultLine);

        // log results into text file
        try {
            File file = new File(outputFile);
            if (!file.exists()) file.createNewFile();
            Files.write(Paths.get(outputFile), (ttprun.resultLine+"\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // save solution in a file
        try {
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss").format(new Date());
            PrintWriter pw = new PrintWriter("./output/solutions/"+inst+"-"+algoName+"-"+currentTime+".txt");
            pw.println(ttprun.solution);
            pw.println("Random seed: "+seed);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}