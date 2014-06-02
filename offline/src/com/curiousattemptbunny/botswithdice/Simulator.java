package com.curiousattemptbunny.botswithdice;

/**
 * Created by merlyn on 5/31/14.
 */
public class Simulator {
    private Strategy us;
    private Strategy them;
    private int our_hp;
    private int their_hp;

    public Simulator(Strategy us, Strategy them, int our_hp, int their_hp) {
        this.us = us;
        this.them = them;

        this.our_hp = our_hp;
        this.their_hp = their_hp;
    }


}
