package com.example.cm.data.models;

public class Status {
    private StatusFlag flag;
    private String message;

    public Status(StatusFlag flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public StatusFlag getFlag() {
        return flag;
    }

    public void setFlag(StatusFlag flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return flag.toString();
    }
}
