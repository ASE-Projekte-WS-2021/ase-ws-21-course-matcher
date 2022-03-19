package com.example.cm.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.MeetupPhase;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.google.android.gms.maps.model.LatLng;
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

import static com.example.cm.data.models.Request.RequestState.REQUEST_DECLINED;
import static com.example.cm.data.models.Request.RequestState.REQUEST_PENDING;

public class MeetupRequestRepository extends Repository {

    private static MeetupRequestRepository INSTANCE = null;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupRequestCollection = firestore.collection(CollectionConfig.MEETUP_REQUESTS.toString());
    private final MutableLiveData<List<MutableLiveData<MeetupRequest>>> receivedRequests = new MutableLiveData<>();

    public MeetupRequestRepository() {
    }

    public static MeetupRequestRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MeetupRequestRepository();
        }
        return INSTANCE;
    }

    /**
     * Get all meetup requests for currently signed in user
     */
    public MutableLiveData<List<MutableLiveData<MeetupRequest>>> getMeetupRequestsForUser() {
        if (auth.getCurrentUser() == null) {
            return receivedRequests;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        meetupRequestCollection.whereEqualTo("receiverId", currentUserId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        List<MutableLiveData<MeetupRequest>> requestsToReturn = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Request.RequestState currentState = snapshot.get("state", Request.RequestState.class);
                            if (currentState != REQUEST_DECLINED) {
                                requestsToReturn.add(new MutableLiveData<>(snapshotToMeetupRequest(snapshot)));
                            }
                        }
                        receivedRequests.postValue(requestsToReturn);
                    }
                });
        return receivedRequests;
    }

    /**
     * Convert a list of snapshots to a list of mutable meetup requests
     *
     * @param documents List of snapshots returned from Firestore
     * @return List of mutable meetup requests
     */
    private List<MutableLiveData<MeetupRequest>> snapshotToMutableMeetupRequestList(QuerySnapshot documents) {
        List<MutableLiveData<MeetupRequest>> requests = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            requests.add(new MutableLiveData<>(snapshotToMeetupRequest(document)));
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
        request.setPhase(document.get("phase", MeetupPhase.class));

        return request;
    }

    /**
     * Add a new Meetup Request to collection
     *
     * @param request Request to be stored
     */
    public void addMeetupRequest(MeetupRequest request) {
        meetupRequestCollection.add(request).addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                if (task.getResult() == null) {
                    return;
                }
                request.setId(task.getResult().getId());
                task.getResult().update("id", task.getResult().getId());
            }
        });
    }

    /**
     * Delete a Meetup Request from collection
     *
     * @param request Request to be deleted
     */
    public void deleteMeetupRequest(MeetupRequest request) {
        meetupRequestCollection.document(request.getId()).delete();
    }


    /**
     * Delete Meetup Requests with given meetupId
     *
     * @param meetupId meetup id
     */
    public void deleteRequestForMeetup(String meetupId) {
        meetupRequestCollection.whereEqualTo("meetupId", meetupId)
                .get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                if (task.getResult() == null) {
                    return;
                }
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
            }
        });
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

    public void undoDecline(MeetupRequest request) {
        request.setState(REQUEST_PENDING);
        addMeetupRequest(request);

        meetupRequestCollection.whereEqualTo("meetupId", request.getMeetupId())
                .whereEqualTo("state", Request.RequestState.REQUEST_ANSWERED)
                .whereEqualTo("senderId", request.getReceiverId())
                .whereEqualTo("type", MeetupRequest.MeetupRequestType.MEETUP_INFO_DECLINED)
                .get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                if (task.getResult() == null) {
                    return;
                }
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
            }
        });
    }
}
