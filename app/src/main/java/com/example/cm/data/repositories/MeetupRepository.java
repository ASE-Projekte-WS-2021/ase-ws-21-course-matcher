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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeetupRepository {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupCollection = firestore.collection(CollectionConfig.MEETUPS.toString());
    private MutableLiveData<List<MutableLiveData<Meetup>>> meetupListMLD = new MutableLiveData<>();
    private MutableLiveData<Meetup> meetupMLD = new MutableLiveData<>();

    public MeetupRepository() {
        listenToMeetupListChanges();
    }

    private void listenToMeetupListChanges() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        meetupCollection.whereArrayContains("confirmedFriends", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("LIVE", "Listen failed.", error);
                        return;
                    }
                    List<MutableLiveData<Meetup>> meetups = snapshotToMeetupList(value);
                    meetupListMLD.postValue(meetups);
                });
    }

    public MutableLiveData<List<MutableLiveData<Meetup>>> getMeetups() {
        return meetupListMLD;
    }

    public MutableLiveData<Meetup> getMeetup(String id) {
        Log.e("MDL", meetupListMLD + "");
        List<MutableLiveData<Meetup>> meetups = meetupListMLD.getValue();
        Log.e("LIST", meetups + "");
        Log.e("LIST", meetups.size() + "");
        for (MutableLiveData<Meetup> meetup : meetups) {
            if (Objects.requireNonNull(meetup.getValue()).getId().equals(id)) {
                return meetup;
            }
        }
        return null;
    }

    public boolean addMeetup(Meetup meetup) {
        try {
            meetupCollection.document(meetup.getId()).set(meetup);
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
        meetupCollection.document(meetupId).update("invitedFriends", FieldValue.arrayUnion(participantId));
    }

    /**
     * Convert a list of snapshots to a list of meetups
     *
     * @param documents List of meetups returned from Firestore
     * @return Returns a list of meetups
     */
    private List<MutableLiveData<Meetup>> snapshotToMeetupList(QuerySnapshot documents) {
        List<MutableLiveData<Meetup>> meetups = new ArrayList<>();
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
    private MutableLiveData<Meetup> snapshotToMeetup(DocumentSnapshot document) {
        MutableLiveData<Meetup> meetupMutableLiveData = new MutableLiveData<>();
        Meetup meetup = new Meetup();

        meetup.setId(document.getId());
        meetup.setConfirmedFriends(Utils.castList(document.get("confirmedFriends"), String.class));
        meetup.setRequestingUser(document.getString("requestingUser"));
        meetup.setInvitedFriends(Utils.castList(document.get("invitedFriends"), String.class));
        meetup.setLocation(document.getString("location"));
        meetup.setTimestamp(document.getDate("timestamp"));
        meetup.setPrivate(document.getBoolean("private"));
        meetup.setDeclinedFriends(Utils.castList(document.get("declinedFriends"), String.class));

        meetupMutableLiveData.postValue(meetup);
        return meetupMutableLiveData;
    }
}