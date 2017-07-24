package com.tomgrubbe.shoplite.utils;


public class Utils {

    private static Utils Instance = null;

    private Utils() {}

    public static synchronized Utils getInstance() {
        if (Instance == null) {
            Instance = new Utils();
        }
        return (Instance);
    }

    public static String capFirstWords(final String words)   {
        String[] arr = words.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : arr) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
            builder.append(cap + " ");
        }

        return builder.toString().trim();
    }
}
