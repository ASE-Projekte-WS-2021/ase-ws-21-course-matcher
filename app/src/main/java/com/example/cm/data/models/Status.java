package com.example.cm.data.models;

public class Status {
    private StatusFlag flag;
    private int messageResourceId;

    public Status(StatusFlag flag, int messageResourceId) {
        this.flag = flag;
        this.messageResourceId = messageResourceId;
    }

    public Status(StatusFlag flag) {
        this.flag = flag;
    }

    public StatusFlag getFlag() {
        return flag;
    }

    public void setFlag(StatusFlag flag) {
        this.flag = flag;
    }

    public int getMessageResourceId() {
        return messageResourceId;
    }

    public String toString() {
        return flag.toString();
    }
}
