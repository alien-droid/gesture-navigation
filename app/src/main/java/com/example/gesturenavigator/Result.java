package com.example.gesturenavigator;

public class Result {
    //conf is the output
    private float conf;
    //input label
    private String label;

    Result() {
        this.conf = -1.0F;
        this.label = null;
    }

    void update(float conf, String label) {
        this.conf = conf;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public float getConf() {
        return conf;
    }
}
