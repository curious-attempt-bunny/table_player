package com.curiousattemptbunny.botswithdice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by merlyn on 6/1/14.
 */
public class Distribution {
    private static final Map<Integer, Distribution> distributions = new HashMap<>();

    static {
        Map<Integer, Map<Integer, BigDecimal>> _distributions = new HashMap<>();

        BufferedReader in = new BufferedReader(new InputStreamReader(Distribution.class.getResourceAsStream("distribution.csv")));

        while(true) {
            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null) break;

            String[] parts = line.split(",");
            int stop_at = Integer.parseInt(parts[0].trim());
            int stops_at = Integer.parseInt(parts[1].trim());
            BigDecimal probability = new BigDecimal(parts[2].trim());

            if (!_distributions.containsKey(stop_at)) {
                _distributions.put(stop_at, new HashMap<Integer, BigDecimal>());
            }
            _distributions.get(stop_at).put(stops_at, probability);
        }

        _distributions.forEach((stop_at, distribution) -> distributions.put(stop_at, new Distribution(distribution)));
    }

    public static Distribution lookup(int stop_at) {
        return distributions.get(stop_at);
    }

    private Map<Integer, BigDecimal> distribution;

    public Distribution(Map<Integer, BigDecimal> distribution) {
        this.distribution = distribution;
    }

    public void forEach(BiConsumer<Integer, BigDecimal> visitor) {
        distribution.forEach(visitor);
    }
}
