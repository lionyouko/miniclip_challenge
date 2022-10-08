package com.thelion.lytics.helpers;

import org.json.JSONObject;

public class Parameter {
    private String key;
    private JSONObject values;

    public Parameter(String key, JSONObject values) {
        this.key = key;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JSONObject getValues() {
        return values;
    }

    public void setValues(JSONObject values) {
        this.values = values;
    }
}
