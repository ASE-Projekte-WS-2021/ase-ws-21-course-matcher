package com.example.cm.data.repositories;

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

public class MeetupRepository extends Repository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupCollection = firestore.collection(CollectionConfig.MEETUPS.toString());
    private OnMeetupRepositoryListener listener;

    public MeetupRepository(){

    }

    public MeetupRepository(OnMeetupRepositoryListener listener){
        this.listener = listener;
    }

    /**
     * Convert a single snapshot to a notification model
     *
     * @param document Snapshot of a notification returned from Firestore
     * @return Returns a notification
     */
    private Meetup snapshotToMeetup(DocumentSnapshot document) {
        Meetup meetup = new Meetup();
        meetup.setRequestingUser(document.getString("requestingUser"));
        meetup.setParticipants(Utils.castList(document.get("participants"), String.class));
        meetup.setLocation(document.getString("location"));
        meetup.setTime(document.getString("time"));
        meetup.setPrivate(document.getBoolean("private"));
        meetup.setConfirmedFriends(Utils.castList(document.get("confirmedFriends"), String.class));
        return meetup;
    }

    public void getMeetupsByCurrentUser() {
        meetupCollection.whereArrayContains("participants", FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<Meetup> meetups = snapshotToMeetupList(Objects.requireNonNull(task.getResult()));
                listener.onMeetupsRetrieved(meetups);
            }
        });
    }

    public void addMeetup(Meetup meetup) {
        meetupCollection.add(meetup).addOnCompleteListener(executorService, task -> {
            if(task.isSuccessful()){
                String newMeetupId = Objects.requireNonNull(task.getResult()).getId();
                listener.onMeetupAdded(newMeetupId);
            }
        });
    }

    public void addConfirmed(String meetupId, String participantId){
        meetupCollection.document(meetupId).update("confirmedFriends", FieldValue.arrayUnion(participantId));
    }

    public void addDeclined(String meetupId, String participantId){
        meetupCollection.document(meetupId).update("declinedFriends", FieldValue.arrayUnion(participantId));
    }

    public interface OnMeetupRepositoryListener {
        void onMeetupsRetrieved(List<Meetup> meetups);
        void onMeetupAdded(String meetupId);
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
}

