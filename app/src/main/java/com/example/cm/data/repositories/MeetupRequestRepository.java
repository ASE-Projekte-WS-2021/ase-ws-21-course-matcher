package com.example.cm.data.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeetupRequestRepository extends Repository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupRequestCollection = firestore.collection(CollectionConfig.MEETUP_REQUESTS.toString());;

    private MutableLiveData<List<MeetupRequest>> mutableReceivedRequestList = new MutableLiveData<>();

    public MeetupRequestRepository() {}

    /**
     * Get all meetup requests for currently signed in user
     */
    public MutableLiveData<List<MeetupRequest>> getMeetupRequestsForUser() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        String userId = auth.getCurrentUser().getUid();
        meetupRequestCollection.whereEqualTo("receiverId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<MeetupRequest> requests = snapshotToMeetupRequestList(Objects.requireNonNull(task.getResult()));
                mutableReceivedRequestList.postValue(requests);
            }
        });
        return mutableReceivedRequestList;
    }

    private List<MeetupRequest> snapshotToMeetupRequestList(QuerySnapshot documents) {
        List<MeetupRequest> requests = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            requests.add(snapshotToMeetupRequest(document));
        }
        return requests;
    }

    /**
     * Convert a single snapshot to a meetup request model
     *
     * @param document Snapshot of a meetup request returned from Firestore
     * @return Returns a meetup request built from firestore document
     */
    private MeetupRequest snapshotToMeetupRequest(DocumentSnapshot document) {
        MeetupRequest.MeetupRequestType notType = Objects.requireNonNull(document.get("type", MeetupRequest.MeetupRequestType.class));
        MeetupRequest request = new MeetupRequest(notType);
        request.setId(document.getId());
        request.setSenderId(document.getString("senderId"));
        request.setSenderName(document.getString("senderName"));
        request.setReceiverId(document.getString("receiverId"));
        request.setCreatedAt(document.getDate("createdAt"));
        request.setState(document.get("state", Request.RequestState.class));
        request.setMeetupId(document.getString("meetupId"));
        request.setLocation(document.getString("location"));
        request.setMeetupAt(document.getDate("meetupAt"));
        return request;
    }

    /**
     * Add a new Meetup Request to collection
     *
     * @param request Requests to be stored
     */
    public void addMeetupRequest(MeetupRequest request) {
        meetupRequestCollection.add(request);
    }

    /**
     * Set state of request
     *
     * @param request to accept/decline/undo decline
     */
    public void accept(MeetupRequest request) {
        meetupRequestCollection.document(request.getId()).
                update("state", Request.RequestState.REQUEST_ACCEPTED);
        meetupRequestCollection.document(request.getId()).
                update("createdAt", request.getCreatedAt());
    }

    public void decline(MeetupRequest request) {
        meetupRequestCollection.document(request.getId()).
                update("state", Request.RequestState.REQUEST_DECLINED);
    }

    public void undo(MeetupRequest request) {
        meetupRequestCollection.document(request.getId()).
                update("state", Request.RequestState.REQUEST_PENDING);
    }
}
