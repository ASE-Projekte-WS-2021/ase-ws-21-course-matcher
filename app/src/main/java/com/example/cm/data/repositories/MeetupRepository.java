package com.example.cm.data.repositories;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.Meetup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MeetupRepository extends Repository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupCollection = firestore.collection(CollectionConfig.MEETUPS.toString());

    public void addMeetup(Meetup meetup) {
        meetupCollection.add(meetup);
    }
}
