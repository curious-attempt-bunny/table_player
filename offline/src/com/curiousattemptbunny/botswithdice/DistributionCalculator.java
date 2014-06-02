package com.curiousattemptbunny.botswithdice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by merlyn on 6/1/14.
 */
public class DistributionCalculator {
    public static final int ACCURACY = 1_000_000_000;
    public static void main(String[] args) {
        Map<Integer, Map<Integer, BigDecimal>> distributions = new HashMap<Integer, Map<Integer, BigDecimal>>();
        for(int i=1; i<=100; i++) {
            distributions.put(i, null);
        }
        distributions.keySet().parallelStream().forEach(stop_at -> {
            Random r = new Random();
            Map<Integer, Integer> results = new HashMap<Integer, Integer>();
            for(int i=0; i<ACCURACY; i++) {
                int total = 0;
                while (total < stop_at) {
                    int roll = r.nextInt(6) + 1;
                    if (roll == 1) {
                        total = 0;
                        break;
                    }
                    total += roll;
                }
                if (!results.containsKey(total)) {
                    results.put(total, 0);
                }

                results.put(total, results.get(total) + 1);
            }

            Map<Integer, BigDecimal> probabilities = new HashMap<Integer, BigDecimal>();
            results.forEach((k,v) -> probabilities.put(k, new BigDecimal(v).divide(new BigDecimal(ACCURACY))));
            distributions.put(stop_at, probabilities);

//            final double[] expectation = {0.0};
//            probabilities.forEach((k,v) -> expectation[0] += k * v.doubleValue() ) ;
//            if (expectation[0] > 8.0) {
//                probabilities.forEach((k, v) -> System.out.println(k + ": " + v));
//                System.out.println("Stop_at " + stop_at + " has expectation " + expectation[0]);
//            }
        } );


        distributions.forEach((stop_at, distribution) -> {
            distribution.forEach((stops_at, probability) -> {
                System.out.println(stop_at + ", " + stops_at + ", " + probability);
            });
        });
    }

}
