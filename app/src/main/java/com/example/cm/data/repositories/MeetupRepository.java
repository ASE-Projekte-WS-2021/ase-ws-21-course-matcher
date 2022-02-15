package com.example.cm.data.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.Meetup;
import com.example.cm.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeetupRepository {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupCollection = firestore.collection(CollectionConfig.MEETUPS.toString());
    private final MutableLiveData<List<Meetup>> meetupListMLD = new MutableLiveData<>();
    private final MutableLiveData<Meetup> meetupMLD = new MutableLiveData<>();

    public MeetupRepository() {
    }

    public MutableLiveData<List<Meetup>> getMeetups() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        meetupCollection.whereArrayContains("confirmedFriends", currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Meetup> meetups = snapshotToMeetupList(Objects.requireNonNull(task.getResult()));
                meetupListMLD.postValue(meetups);
            }
        });
        return meetupListMLD;
    }

    public MutableLiveData<Meetup> getMeetup(String id) {
        Log.e("ID", id);
        meetupCollection.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                Log.e("GET", document+"");
                meetupMLD.postValue(snapshotToMeetup(Objects.requireNonNull(document)));
            }
        });

        return meetupMLD;
    }

    public boolean addMeetup(Meetup meetup) {
        try {
            meetupCollection.add(meetup);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void addConfirmed(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update("confirmedFriends", FieldValue.arrayUnion(participantId));
        meetupCollection.document(meetupId).update("invitedFriends", FieldValue.arrayRemove(participantId));
    }

    public void addDeclined(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update("declinedFriends", FieldValue.arrayUnion(participantId));
        meetupCollection.document(meetupId).update("invitedFriends", FieldValue.arrayRemove(participantId));
    }

    public void addPending(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update("declinedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("confirmedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("invitedFriends", FieldValue.arrayRemove(participantId));
    }

    /**
     * Convert a list of snapshots to a list of meetups
     *
     * @param documents List of meetups returned from Firestore
     * @return Returns a list of meetups
     */
    private List<Meetup> snapshotToMeetupList(QuerySnapshot documents) {
        List<Meetup> meetups = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            meetups.add(snapshotToMeetup(document));
        }
        return meetups;
    }

    /**
     * Convert a single snapshot to a meetup model
     *
     * @param document Snapshot of a meetup returned from Firestore
     * @return Returns a meetup
     */
    private Meetup snapshotToMeetup(DocumentSnapshot document) {
        Log.e("TAG", document+"");
        Meetup meetup = new Meetup();
        meetup.setId(document.getId());
        meetup.setConfirmedFriends(Utils.castList(document.get("confirmedFriends"), String.class));
        meetup.setRequestingUser(document.getString("requestingUser"));
        meetup.setInvitedFriends(Utils.castList(document.get("invitedFriends"), String.class));
        meetup.setLocation(document.getString("location"));
        meetup.setTime(document.getString("time"));
        meetup.setPrivate(document.getBoolean("private"));
        meetup.setDeclinedFriends(Utils.castList(document.get("declinedFriends"), String.class));
        return meetup;
    }
}