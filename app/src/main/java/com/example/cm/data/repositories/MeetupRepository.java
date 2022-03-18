package com.example.cm.data.repositories;

import android.util.Log;

import static com.example.cm.data.models.MeetupPhase.MEETUP_ENDED;
import static com.example.cm.data.repositories.Repository.executorService;
import static com.example.cm.utils.Utils.getCurrentDay;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupPOJO;
import com.example.cm.data.models.MeetupPhase;
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

public class MeetupRepository {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupCollection = firestore.collection(CollectionConfig.MEETUPS.toString());
    private MutableLiveData<List<MutableLiveData<Meetup>>> meetupListMLD = new MutableLiveData<>();
    private MutableLiveData<Meetup> meetupMLD = new MutableLiveData<>();
    private MutableLiveData<List<String>> usersMLD = new MutableLiveData<>();

    public MeetupRepository() {
        listenToMeetupListChanges();
    }

    private void listenToMeetupListChanges() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        meetupCollection.whereArrayContains("confirmedFriends", currentUserId)
                .whereGreaterThan("timestamp", getCurrentDay())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(executorService, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        for (int i = 0; i < value.getDocuments().size(); i++) {
                            MeetupPhase currentPhase = value.getDocuments().get(i).get("phase", MeetupPhase.class);
                            if (currentPhase == MEETUP_ENDED) {
                                value.getDocuments().remove(i);
                            }
                        }
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

    public MutableLiveData<List<String>> getAllUsersForMeetup(String meetupId) {
        meetupCollection.document(meetupId).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                List<String> users = new ArrayList<>();
                if (value.get("invitedFriends") != null) {
                    users.addAll((List<String>) value.get("invitedFriends"));
                }
                if (value.get("confirmedFriends") != null) {
                    users.addAll((List<String>) value.get("confirmedFriends"));
                }
                if (value.get("declinedFriends") != null) {
                    users.addAll((List<String>) value.get("declinedFriends"));
                }
                usersMLD.postValue(users);
            }
        });
        return usersMLD;
    }

    public MutableLiveData<List<String>> getLateUsers(String meetupId) {
        meetupCollection.document(meetupId).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                List<String> lateUsers = (List<String>) value.get("lateFriends");
                usersMLD.postValue(lateUsers);
            }
        });
        return usersMLD;
    }

    public boolean addMeetup(Meetup meetup) {
        try {
            MeetupPOJO meetupPOJO = new MeetupPOJO(meetup);
            meetupCollection.document(meetup.getId()).set(meetupPOJO);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteMeetup(String meetupId) {
        meetupCollection.document(meetupId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                if (task.getResult() == null) {
                    return;
                }
                if (task.getResult().exists()) {
                    task.getResult().getReference().delete();
                }
            }
        });
    }

    public void addInvited(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update("invitedFriends", FieldValue.arrayUnion(participantId));
    }

    public void addConfirmed(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update("invitedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("declinedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("confirmedFriends", FieldValue.arrayUnion(participantId));
    }

    public void addDeclined(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update("invitedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("confirmedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("lateFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("declinedFriends", FieldValue.arrayUnion(participantId));
    }

    public void addLeft(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update("invitedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("confirmedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("lateFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("declinedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                Meetup meetup = snapshotToMeetup(value);
                if (meetup.getInvitedFriends().isEmpty() && meetup.getConfirmedFriends().isEmpty()) {
                    meetupCollection.document(meetupId).update("phase", MEETUP_ENDED);
                }
            }
        });
    }

    public void addPending(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update("declinedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("confirmedFriends", FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update("invitedFriends", FieldValue.arrayUnion(participantId));
    }

    public void addLate(String meetupId, String participantId, boolean comesLate) {
        if (comesLate) {
            meetupCollection.document(meetupId).update("lateFriends", FieldValue.arrayUnion(participantId));
        } else {
            meetupCollection.document(meetupId).update("lateFriends", FieldValue.arrayRemove(participantId));
        }
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
        MeetupPOJO meetupPOJO = document.toObject(MeetupPOJO.class);
        assert meetupPOJO != null;
        meetupPOJO.setId(document.getId());
        return meetupPOJO.toObject();
    }
}