package dev.beecube31.crazyae2.common.util;

public class Longium {
    private long value = 0;

    public Longium(long initial) {
        this.value = initial;
    }

    public Longium() {}

    public void set(long value) {
        this.value = value;
    }

    public long get() {
        return this.value;
    }
}
