package com.inovactio.awakenawakennomi.util;

public class InoHelper {
    public static float linearInterpollation(float min, float max, float t) {
        return min + (max - min) * t;
    }
}
