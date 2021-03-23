package org.nozomi.jikkenkichi.seiran;

/**
 * Record the information needed for short-circuiting
 */
public class CircuitBreakInfo {
    //make some hard coded value in demo
    //if FAIL_THRESHOLD_COUNT fail requests in FAIL_THRESHOLD_INTERVAL,it will down
    private static int FAIL_THRESHOLD_COUNT = 5;
    private static long FAIL_THRESHOLD_INTERVAL = 1000 * 10;
    //recover after RECOVER_INTERVAL
    private static long RECOVER_INTERVAL = 1000 * 10;

    private int idx = 0;
    private long[] failTimeArr = new long[FAIL_THRESHOLD_COUNT];
    private volatile long circuitBreakTime = -1;

    /**
     * add a fail counter
     *
     * @return this fail causes circuit break?
     */
    synchronized public boolean failCounter() {
        if (!checkAvailable()) {
            return false;
        }
        long now = System.currentTimeMillis();
        failTimeArr[idx] = now;
        idx = ++idx % failTimeArr.length;
        long earliest = failTimeArr[idx];
        if (now - earliest < FAIL_THRESHOLD_INTERVAL) {
            circuitBreakTime = now;
            return true;
        }
        return false;
    }

    public boolean checkAvailable() {
        return System.currentTimeMillis() - circuitBreakTime > RECOVER_INTERVAL;
    }

}
