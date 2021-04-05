package com.dk.lb.utils;

public class Timer {
    long startTime;
    long stopTime;
    String msg;

    public Timer(String msg) {
        this.msg = msg;
        startTime = System.currentTimeMillis();
    }

    public Timer stop() {
        stopTime = System.currentTimeMillis();
        return this;
    }

    public void log() {
        System.out.println(msg + " took " + (stopTime - startTime) + " millis");
    }
    public void log(String additionalMsg) {
        System.out.println(msg + additionalMsg + " took " + (stopTime - startTime) + " millis");
    }
}
