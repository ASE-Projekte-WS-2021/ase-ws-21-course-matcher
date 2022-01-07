package com.example.cm.ui.InviteFriends;

import com.example.cm.data.models.Meetup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class InviteFriendsRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupCollection = firestore.collection("meetups");

    public void addMeetup2(Meetup meetup){

        meetupCollection.add(meetup);
    }
}
