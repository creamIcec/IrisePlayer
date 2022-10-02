package com.Iriseplos.iriseplayer.renderer.tools;

public class TimeParser {
    public static String parseSecondsToMMSS(long seconds) {
        long second = seconds % 60;
        long minute = seconds / 60;
        String secondString;
        secondString = second < 10 ? "0" + second : Long.toString(second);
        return minute + ":" + secondString;
    }
}
