package com.curiousattemptbunny.botswithdice;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by merlyn on 6/1/14.
 */
public class TableBuilder {
    private static Map<Integer, Map<Integer, StopAtStrategy>> strategies = new HashMap<>();

    public static void main(String[] args) {
        for(int depth = 1; depth<=100; depth++) {
            for(int i=1; i<=depth; i++) {
                for(int repeats = 0; repeats<1000; repeats++) {
                    populate(i, depth);
                    populate(depth, i);
                }
                display(i, depth);
                if (i != depth) {
                    display(depth, i);
                }
            }
        }
    }

    private static void display(int our_hp, int their_hp) {
        StopAtStrategy best_strategy = strategies.get(our_hp).get(their_hp);
        System.out.println(their_hp+", "+our_hp+", "+best_strategy.getStopAt()+", "+best_strategy.getWinPercentage());
    }

    private static void populate(int our_hp, int their_hp) {
        if (!strategies.containsKey(our_hp)) {
            strategies.put(our_hp, new HashMap<>());
        }
        if (!strategies.containsKey(their_hp)) {
            strategies.put(their_hp, new HashMap<>());
        }
//        if (strategies.get(our_hp).containsKey(their_hp)) {
//            return;
//        }

        for(int iterations = 0; iterations < 1; iterations++) {
            List<StopAtStrategy> candidates = new ArrayList<>();

            boolean debug = (our_hp == 4 && our_hp == their_hp);
            debug = false;
            PrintStream out = debug ? System.out : new PrintStream(new ByteArrayOutputStream());

            for (int stop_at = 2; stop_at <= Math.max(2, their_hp); stop_at++) {
                int sa = stop_at;
                final BigDecimal[] average_win_percentage = {new BigDecimal(0)};

                Distribution distribution = Distribution.lookup(stop_at);
                distribution.forEach((stops_at, win_percentage) -> {
                    BigDecimal next_win_percentage;

                    int their_next_hp = their_hp - stops_at;
                    if (their_next_hp <= 0) {
                        next_win_percentage = new BigDecimal(0);
                    } else {
                        StopAtStrategy next_strategy = strategies.get(their_next_hp).get(our_hp);
                        if (stops_at == 0 && next_strategy == null) {
//                            System.out.println("No fallback for us " + their_next_hp + " vs them " + our_hp+" stops_at is "+stops_at+" from us " + our_hp + " vs them " + their_hp);
                            next_strategy = strategies.get(their_next_hp).get(our_hp - 1);
                            if (next_strategy == null) {
                                Map<Integer, StopAtStrategy> s = strategies.get(their_next_hp - 1);
                                if (s == null) {
                                    System.out.println("Guessing for us " + their_next_hp + " vs them " + our_hp);
                                    next_strategy = new StopAtStrategy(-1, new BigDecimal(5).divide(new BigDecimal(6), 20, RoundingMode.FLOOR));
                                } else {
                                    next_strategy = s.get(our_hp);
                                }
                            }
                        }
                        next_win_percentage = next_strategy.getWinPercentage();
                    }
                    out.println("From us " + our_hp + " vs them " + their_hp + " getting " + stops_at + " has a chance of " + win_percentage);
                    out.println("\tand leads to 'us' " + their_next_hp + " vs 'them' " + our_hp + " with a WP of " + next_win_percentage);

                    BigDecimal projected_win_percentage = new BigDecimal(1).subtract(next_win_percentage).multiply(win_percentage);
                    out.println("\tProjecting a WP of " + projected_win_percentage);
                    average_win_percentage[0] = average_win_percentage[0].add(projected_win_percentage);
                });

                candidates.add(new StopAtStrategy(stop_at, average_win_percentage[0].round(MathContext.DECIMAL64)));

                //            if (our_hp == their_hp && our_hp == 7) {
                //            if (our_hp == 3 && their_hp == 3) {
                out.println("Us " + our_hp + " vs them " + their_hp + " stop_at " + stop_at + " wins " + average_win_percentage[0]);
                out.println();
                //            }
            }

            Collections.reverse(candidates);
            StopAtStrategy best_strategy = candidates.stream().max((a, b) ->
                            a.getWinPercentage().divide(new BigDecimal(1), 18, RoundingMode.FLOOR).
                                    compareTo(b.getWinPercentage().divide(new BigDecimal(1), 18, RoundingMode.FLOOR))
            ).get();
            //        if (best_strategy.getStopAt() < their_hp && their_hp <= 16) {
            //        if (our_hp == 3 && their_hp == 3) {
//            System.out.println("Us " + our_hp + " vs them " + their_hp + " best_stop_at " + best_strategy.getStopAt() + " wins " + best_strategy.getWinPercentage());
            //            System.exit(0);
            //        }
            strategies.get(our_hp).put(their_hp, best_strategy);
        }
    }

    private static class StopAtStrategy {
        private final int stop_at;
        private final BigDecimal win_percentage;

        public StopAtStrategy(int stop_at, BigDecimal win_percentage) {

            this.stop_at = stop_at;
            this.win_percentage = win_percentage;
        }

        public int getStopAt() {
            return stop_at;
        }

        public BigDecimal getWinPercentage() {
            return win_percentage;
        }
    }
}
