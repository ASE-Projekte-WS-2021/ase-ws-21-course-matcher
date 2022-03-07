package com.example.cm.data.models;

public class FriendRequestDTO extends Request {

    private String senderName;

    public FriendRequestDTO() {
        super();
    }

    public FriendRequestDTO(String senderId, String senderName, String receiverId) {
        super(senderId, receiverId);
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
