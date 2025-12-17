package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.List;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        var ns = new AList<Integer>();
        var times = new AList<Double>();
        var opCounts = new AList<Integer>();

        List<Integer> sizes = List.of(1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000);
        for (int size : sizes) {
            var aList = new AList<Integer>();
            var sw = new Stopwatch();
            for (int i = 1; i < size - 1; i++) {
                aList.addLast(i);
            }
            double timeInSeconds = sw.elapsedTime();
            ns.addLast(size);
            times.addLast(timeInSeconds);
            opCounts.addLast(size);
        }

        System.out.println("Timing table for addLast");
        printTimingTable(ns, times, opCounts);
    }
}
