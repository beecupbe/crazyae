package dev.beecube31.crazyae2.common.util;

import java.util.ArrayList;
import java.util.List;

public class FeatureSet {

    private final List<Object> set = new ArrayList<>();

    public FeatureSet add(Object o) {
        set.add(o);
        return this;
    }

    public FeatureSet remove(Object o) {
        set.remove(o);
        return this;
    }

    public List<Object> get() {
        return this.set;
    }

}
