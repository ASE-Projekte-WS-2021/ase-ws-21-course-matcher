package com.example.cm.data.models;

public class FriendRequest extends Request {

    public FriendRequest() {
        super();
    }

    public FriendRequest(String senderId, String senderName, String receiverId) {
        super(senderId, senderName, receiverId);
    }

}
