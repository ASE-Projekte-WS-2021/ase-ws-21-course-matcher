package com.example.cm.config;

public enum FieldType {

    TEXT_INPUT("textInput"),
    TEXT_AREA("textArea");

    private final String type;

    FieldType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
