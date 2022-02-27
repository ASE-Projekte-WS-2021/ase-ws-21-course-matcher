package com.example.cm.utils;

public class InputValidator {

    public static boolean hasMinLength(String input, int minLength) {
        return input.length() >= minLength;
    }

}
