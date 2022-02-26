package com.example.cm.data.repositories;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.cm.Constants;
import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupPhase;
import com.example.cm.utils.Utils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.cm.data.repositories.Repository.executorService;

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
                .whereGreaterThan("timestamp", Constants.getCurrentDay())
                .whereNotEqualTo("phase", MeetupPhase.MEETUP_ENDED)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
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
        meetupCollection.document(id).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                Meetup meetup = snapshotToMeetup(value);
                meetupMLD.postValue(meetup);
            }
        });
        return meetupMLD;
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
            MutableLiveData<Meetup> meetupMLD = new MutableLiveData<>();
            meetupMLD.postValue(snapshotToMeetup(document));
            meetups.add(meetupMLD);
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
        Meetup meetup = new Meetup();
        meetup.setId(document.getId());
        meetup.setConfirmedFriends(Utils.castList(document.get("confirmedFriends"), String.class));
        meetup.setRequestingUser(document.getString("requestingUser"));
        meetup.setInvitedFriends(Utils.castList(document.get("invitedFriends"), String.class));
        meetup.setLocation(document.getString("location"));
        meetup.setTimestamp(document.getDate("timestamp"));
        meetup.setPrivate(document.getBoolean("private"));
        meetup.setPrivate(document.getBoolean("hasEnded"));
        meetup.setPhase(document.get("phase", MeetupPhase.class));
        meetup.setDeclinedFriends(Utils.castList(document.get("declinedFriends"), String.class));
        return meetup;
    }
}