package org.Custom.Transformations.core;

import java.util.HashMap;
import java.util.Map;

public abstract class Convertible<Source, Destination> {
    private Map<String, String> params = new HashMap<>();

    public abstract Destination convert(Source src);

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
