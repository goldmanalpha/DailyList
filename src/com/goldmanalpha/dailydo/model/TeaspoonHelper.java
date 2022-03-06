package com.goldmanalpha.dailydo.model;

public class TeaspoonHelper {
    public static String shortName(TeaSpoons tsp) {
        switch (tsp) {
            case quarter:
                return "qtr";
            case eighth:
                return "8th";
            case sixteenth:
                return "16th";
            case thirtysecond:
                return "32nd";

            default:
                return tsp.toString();
        }
    }

    public static String shortName(String tsp) {
        try {
            return shortName(TeaSpoons.valueOf(tsp));
        } catch (IllegalArgumentException ex) {
            return "invalid";
        }
    }
}
