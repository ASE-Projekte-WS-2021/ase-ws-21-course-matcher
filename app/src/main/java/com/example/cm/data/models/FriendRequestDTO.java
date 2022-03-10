package com.example.cm.data.models;

public class FriendRequestDTO extends Request {

    private final String senderName;
    private String senderUserName;

    public FriendRequestDTO(String senderId, String senderName, String senderUserName, String receiverId) {
        super(senderId, receiverId);
        this.senderName = senderName;
        this.senderUserName = senderUserName;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }
}
