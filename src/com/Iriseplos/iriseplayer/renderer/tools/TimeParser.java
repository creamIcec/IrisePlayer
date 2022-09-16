package com.Iriseplos.iriseplayer.renderer.tools;

public class TimeParser {
    public static String parseSecondsTommss(long seconds) {
        Long second = seconds % 60;
        Long minute = seconds / 60;
        String secondString;
        secondString = second < 10 ? "0" + second : second.toString();
        return minute + ":" + secondString;
    }
}
