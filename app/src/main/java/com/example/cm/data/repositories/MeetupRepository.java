package com.example.cm.data.repositories;

import static com.example.cm.Constants.FIELD_CONFIRMED_FRIENDS;
import static com.example.cm.Constants.FIELD_DECLINED_FRIENDS;
import static com.example.cm.Constants.FIELD_INVITED_FRIENDS;
import static com.example.cm.Constants.FIELD_LATE_FRIENDS;
import static com.example.cm.Constants.FIELD_PHASE;
import static com.example.cm.Constants.FIELD_TIMESTAMP;
import static com.example.cm.data.models.MeetupPhase.MEETUP_ACTIVE;
import static com.example.cm.data.models.MeetupPhase.MEETUP_ENDED;
import static com.example.cm.data.repositories.Repository.executorService;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.listener.MeetupListener;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupPOJO;
import com.example.cm.data.models.MeetupPhase;
import com.example.cm.data.models.User;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

public class MeetupRepository {
    private static MeetupRepository instance;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupCollection = firestore.collection(CollectionConfig.MEETUPS.toString());

    private final MutableLiveData<List<Meetup>> meetupListMLD = new MutableLiveData<>();
    private final MutableLiveData<List<Meetup>> meetupsForRequestsMLD = new MutableLiveData<>();
    private final MutableLiveData<Meetup> meetupMLD = new MutableLiveData<>();
    private final MutableLiveData<List<String>> usersMLD = new MutableLiveData<>();

    private List<Meetup> meetups = new ArrayList<>();

    public MeetupRepository() {
        listenToMeetupListChanges();
    }

    /**
     * Get the MeetupRepository
     *
     * @return the instance of the repository
     */
    public static MeetupRepository getInstance() {
        if (instance == null) {
            instance = new MeetupRepository();
        }
        return instance;
    }

    /**
     * Listen to changes in the meetup list
     */
    private void listenToMeetupListChanges() {
        if (auth.getCurrentUser() == null) {
            return;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        meetupCollection.whereArrayContains(FIELD_CONFIRMED_FRIENDS, currentUserId)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener(executorService, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        List<Meetup> meetups = new ArrayList<>();
                        for (int i = 0; i < value.getDocuments().size(); i++) {
                            Meetup meetup = snapshotToMeetup(value.getDocuments().get(i));
                            MeetupPhase currentPhase = meetup.getPhase();
                            MeetupPhase phaseInFirestore = value.getDocuments().get(i).get(FIELD_PHASE, MeetupPhase.class);

                            if (phaseInFirestore != MEETUP_ENDED) {
                                if (currentPhase != MEETUP_ENDED) {
                                    meetups.add(meetup);
                                }
                                value.getDocuments().get(i).getReference().update(FIELD_PHASE, currentPhase);
                            }
                        }
                        meetupListMLD.postValue(meetups);
                    }
                });
    }

    /**
     * Get meetup list by list of meetupIds
     *
     * @param meetupIds IDs of meetups to retrieve
     * @return MutableLiveData-List of mutable meetups with ids
     */
    public MutableLiveData<List<Meetup>> getMeetupsByIds(List<String> meetupIds) {
        if (meetupIds == null || meetupIds.isEmpty()) {
            meetupsForRequestsMLD.postValue(new ArrayList<>());
            return meetupsForRequestsMLD;
        }

        if (!meetups.isEmpty()) {
            meetups = new ArrayList<>();
        }

        List<String> userIdsNoDuplicates = new ArrayList<>(new HashSet<>(meetupIds));

        List<List<String>> subLists = Lists.partition(userIdsNoDuplicates, 10);
        for (List<String> subList : subLists) {
            meetupCollection.whereIn(FieldPath.documentId(), subList).addSnapshotListener(executorService,
                    (value, error) -> {
                        if (error != null) {
                            return;
                        }
                        if (value != null && !value.isEmpty()) {
                            List<Meetup> meetupList = new ArrayList<>();
                            for (int i = 0; i < value.getDocuments().size(); i++) {
                                Meetup meetup = snapshotToMeetup(value.getDocuments().get(i));
                                meetupList.add(meetup);
                            }
                            meetups.addAll(meetupList);
                            meetupsForRequestsMLD.postValue(meetups);
                        }
                    });
        }
        return meetupsForRequestsMLD;
    }

    /**
     * Get all meetups
     *
     * @return A LiveData list of meetups
     */
    public MutableLiveData<List<Meetup>> getMeetups() {
        if(meetupListMLD.getValue() == null) {
            meetupListMLD.postValue(new ArrayList<>());
        }
        return meetupListMLD;
    }

    /**
     * Get a list of currently running meetups
     *
     * @param listener listener to be notified when the list is updated
     */
    public void getCurrentMeetups(MeetupListener<List<Meetup>> listener) {
        if (auth.getCurrentUser() == null) {
            return;
        }

        String currentUserId = auth.getCurrentUser().getUid();

        meetupCollection.whereArrayContains(FIELD_CONFIRMED_FRIENDS, currentUserId).get()
                .addOnFailureListener(executorService, listener::onMeetupError)
                .addOnSuccessListener(executorService, (value) -> {
                    List<Meetup> meetups = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        MeetupPOJO meetupPOJO = document.toObject(MeetupPOJO.class);
                        meetupPOJO.setId(document.getId());

                        if (meetupPOJO.getPhase() == MEETUP_ACTIVE) {
                            meetups.add(meetupPOJO.toObject());
                        }
                    }
                    listener.onMeetupSuccess(meetups);
                });
    }

    /**
     * Get a meetup by its id
     *
     * @param id the id of the meetup
     * @return a LiveData containing the meetup
     */
    public MutableLiveData<Meetup> getMeetup(String id) {
        meetupCollection.document(id)
                .addSnapshotListener(executorService, (value, error) -> {
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

    /**
     * Get all late users of a meetup
     *
     * @param meetupId The id of the meetup
     * @return a LiveData list of late users
     */
    public MutableLiveData<List<String>> getLateUsers(String meetupId) {
        meetupCollection.document(meetupId)
                .addSnapshotListener(executorService, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.exists()) {
                        List<String> lateUsers = (List<String>) value.get(FIELD_LATE_FRIENDS);
                        usersMLD.postValue(lateUsers);
                    }
                });
        return usersMLD;
    }

    /**
     * Creates a new meetup
     *
     * @param meetup The meetup to be created
     */
    public void addMeetup(Meetup meetup) {
        try {
            MeetupPOJO meetupPOJO = new MeetupPOJO(meetup);
            meetupCollection.document(meetup.getId()).set(meetupPOJO);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Deletes a meetup from the database.
     *
     * @param meetupId The id of the meetup to delete.
     */
    public void deleteMeetup(String meetupId) {
        meetupCollection.document(meetupId).get()
                .addOnCompleteListener(executorService, task -> {
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

    /**
     * Add a user to the list of people who were invited to the meetup.
     *
     * @param meetupId      The id of the meetup.
     * @param participantId The id of the participant.
     */
    public void addInvited(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update(FIELD_INVITED_FRIENDS, FieldValue.arrayUnion(participantId));
    }

    /**
     * Add a user to the list of people who confirmed the meetup.
     *
     * @param meetupId      The id of the meetup.
     * @param participantId The id of the participant.
     */
    public void addConfirmed(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update(FIELD_INVITED_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_DECLINED_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_CONFIRMED_FRIENDS, FieldValue.arrayUnion(participantId));
    }

    /**
     * Add a user to the list of people who declined the meetup.
     *
     * @param meetupId      The id of the meetup.
     * @param participantId The id of the participant.
     */
    public void addDeclined(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update(FIELD_INVITED_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_CONFIRMED_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_LATE_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_DECLINED_FRIENDS, FieldValue.arrayUnion(participantId));
    }

    /**
     * Removes the user from the meetup
     *
     * @param meetupId      The id of the meetup
     * @param participantId The id of the user
     */
    public void addLeft(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update(FIELD_INVITED_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_CONFIRMED_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_LATE_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_DECLINED_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId)
                .addSnapshotListener(executorService, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.exists()) {
                        Meetup meetup = snapshotToMeetup(value);
                        if (meetup.getInvitedFriends().isEmpty() && meetup.getConfirmedFriends().isEmpty()) {
                            meetupCollection.document(meetupId).update(FIELD_PHASE, MEETUP_ENDED);
                        }
                    }
                });
    }

    /**
     * Adds a user to the list of invited people of the meetup.
     *
     * @param meetupId      The id of the meetup.
     * @param participantId The id of the user.
     */
    public void addPending(String meetupId, String participantId) {
        meetupCollection.document(meetupId).update(FIELD_DECLINED_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_CONFIRMED_FRIENDS, FieldValue.arrayRemove(participantId));
        meetupCollection.document(meetupId).update(FIELD_INVITED_FRIENDS, FieldValue.arrayUnion(participantId));
    }

    /**
     * Adds whether a user is late to a meetup or not
     *
     * @param meetupId      the id of the meetup
     * @param participantId the id of the participant
     * @param isComingLate  true if the user is coming late, false otherwise
     */
    public void addLate(String meetupId, String participantId, boolean isComingLate) {
        if (isComingLate) {
            meetupCollection.document(meetupId).update(FIELD_LATE_FRIENDS, FieldValue.arrayUnion(participantId));
        } else {
            meetupCollection.document(meetupId).update(FIELD_LATE_FRIENDS, FieldValue.arrayRemove(participantId));
        }
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

    /**
     * Deletes a user from all meetups he is a part of@
     *
     * @param userId   The user to be removed from the meetups
     * @param listener The listener to be notified when the deletion is complete
     */
    public void deleteUserFromMeetups(String userId, UserListener<Boolean> listener) {
        meetupCollection.get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                if (task.getResult() == null) {
                    return;
                }
                if (task.getResult().isEmpty()) {
                    return;
                }
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Meetup meetup = snapshotToMeetup(document);

                    if (meetup.getInvitedFriends() != null && meetup.getInvitedFriends().contains(userId)) {
                        meetupCollection.document(meetup.getId()).update(FIELD_INVITED_FRIENDS, FieldValue.arrayRemove(userId));
                    }
                    if (meetup.getConfirmedFriends() != null && meetup.getConfirmedFriends().contains(userId)) {
                        meetupCollection.document(meetup.getId()).update(FIELD_CONFIRMED_FRIENDS, FieldValue.arrayRemove(userId));
                    }
                    if (meetup.getDeclinedFriends() != null && meetup.getDeclinedFriends().contains(userId)) {
                        meetupCollection.document(meetup.getId()).update(FIELD_DECLINED_FRIENDS, FieldValue.arrayRemove(userId));
                    }
                    if (meetup.getLateFriends() != null && meetup.getLateFriends().contains(userId)) {
                        meetupCollection.document(meetup.getId()).update(FIELD_LATE_FRIENDS, FieldValue.arrayRemove(userId));
                    }
                }
                listener.onUserSuccess(true);
            } else {
                listener.onUserError(task.getException());
            }
        });
    }

    /**
     * Delete all meetups that the user created
     *
     * @param userId   The ID of the user who created the meetups
     * @param listener The listener to be notified when the operation is done
     */
    public void deleteMeetupsFromUser(String userId, UserListener<Boolean> listener) {
        meetupCollection.get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                if (task.getResult() == null) {
                    return;
                }
                if (task.getResult().isEmpty()) {
                    return;
                }
                for (QueryDocumentSnapshot document : task.getResult()) {
                    MeetupPOJO meetupPOJO = document.toObject(MeetupPOJO.class);
                    meetupPOJO.setId(document.getId());
                    Meetup meetup = meetupPOJO.toObject();

                    if (!meetup.getRequestingUser().equals(userId)) {
                        continue;
                    }
                    meetupCollection.document(meetup.getId()).delete();
                }
                listener.onUserSuccess(true);
            } else {
                listener.onUserError(task.getException());
            }
        });
    }
}