package com.example.cm.data.repositories;

import static com.example.cm.Constants.FIELD_CREATED_AT;
import static com.example.cm.Constants.FIELD_ID;
import static com.example.cm.Constants.FIELD_MEETUP_ID;
import static com.example.cm.Constants.FIELD_RECEIVER_ID;
import static com.example.cm.Constants.FIELD_SENDER_ID;
import static com.example.cm.Constants.FIELD_STATE;
import static com.example.cm.Constants.FIELD_TYPE;
import static com.example.cm.data.models.Request.RequestState.REQUEST_PENDING;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MeetupRequestRepository extends Repository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference meetupRequestCollection = firestore.collection(CollectionConfig.MEETUP_REQUESTS.toString());
    private final MutableLiveData<List<MeetupRequest>> receivedRequests = new MutableLiveData<>();

    public MeetupRequestRepository() {
    }

    /**
     * Get all meetup requests for currently signed in user
     *
     * @return LiveData List of meetup requests
     */
    public MutableLiveData<List<MeetupRequest>> getMeetupRequestsForUser() {
        if (auth.getCurrentUser() == null) {
            return receivedRequests;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        meetupRequestCollection.whereEqualTo(FIELD_RECEIVER_ID, currentUserId)
                .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.isEmpty()) {
                        receivedRequests.postValue(new ArrayList<>());
                    }

                    if (value != null && !value.isEmpty()) {

                        List<MeetupRequest> pendingRequests = new ArrayList<>();
                        List<MeetupRequest> endedRequests = new ArrayList<>();
                        List<MeetupRequest> otherRequests = new ArrayList<>();

                        List<MeetupRequest> requestsToReturn = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            MeetupRequest request = snapshotToMeetupRequest(snapshot);
                            Request.RequestState currentState = request.getState();

                            if (!isToday(request)) {
                                endedRequests.add(request);
                            } else if (currentState == REQUEST_PENDING) {
                                pendingRequests.add(request);
                            } else {
                                otherRequests.add(request);
                            }
                        }
                        requestsToReturn.addAll(pendingRequests);
                        requestsToReturn.addAll(otherRequests);
                        requestsToReturn.addAll(endedRequests);
                        receivedRequests.postValue(requestsToReturn);
                    }
                });
        return receivedRequests;
    }

    private boolean isToday(MeetupRequest request) {
        Calendar calendarNow = GregorianCalendar.getInstance();
        Calendar calendarMeetup = GregorianCalendar.getInstance();

        Date creationTime = request.getCreatedAt();
        Date now = new Date();

        calendarNow.setTime(now);
        calendarMeetup.setTime(creationTime);

        return (calendarNow.get(Calendar.YEAR) == calendarMeetup.get(Calendar.YEAR)
                && calendarNow.get(Calendar.MONTH) == calendarMeetup.get(Calendar.MONTH)
                && calendarNow.get(Calendar.DAY_OF_MONTH) == calendarMeetup.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Convert a single snapshot to a meetup request model
     *
     * @param document Snapshot of a meetup request returned from Firestore
     * @return Returns a meetup request built from firestore document
     */
    private MeetupRequest snapshotToMeetupRequest(DocumentSnapshot document) {
        MeetupRequest.MeetupRequestType notType = document.get(FIELD_TYPE, MeetupRequest.MeetupRequestType.class);
        MeetupRequest request = new MeetupRequest(notType);

        request.setId(document.getId());
        request.setSenderId(document.getString(FIELD_SENDER_ID));
        request.setReceiverId(document.getString(FIELD_RECEIVER_ID));
        request.setCreatedAt(document.getDate(FIELD_CREATED_AT));
        request.setState(document.get(FIELD_STATE, Request.RequestState.class));
        request.setMeetupId(document.getString(FIELD_MEETUP_ID));

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
                task.getResult().update(FIELD_ID, task.getResult().getId());
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
        meetupRequestCollection.whereEqualTo(FIELD_MEETUP_ID, meetupId)
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
                update(FIELD_STATE, Request.RequestState.REQUEST_ACCEPTED);
        meetupRequestCollection.document(request.getId()).
                update(FIELD_CREATED_AT, request.getCreatedAt());
    }

    /**
     * Decline a request
     *
     * @param request The request to decline
     */
    public void decline(MeetupRequest request) {
        meetupRequestCollection.document(request.getId()).
                update(FIELD_STATE, Request.RequestState.REQUEST_DECLINED);
    }

    /**
     * Undo decline of request
     *
     * @param request The request to undo decline
     */
    public void undoDecline(MeetupRequest request) {
        request.setState(REQUEST_PENDING);
        addMeetupRequest(request);

        meetupRequestCollection.whereEqualTo(FIELD_MEETUP_ID, request.getMeetupId())
                .whereEqualTo(FIELD_STATE, Request.RequestState.REQUEST_ANSWERED)
                .whereEqualTo(FIELD_SENDER_ID, request.getReceiverId())
                .whereEqualTo(FIELD_TYPE, MeetupRequest.MeetupRequestType.MEETUP_INFO_DECLINED)
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
     * Delete all request for a given user
     *
     * @param userId   The id of the user
     * @param listener The listener to be notified when the requests are deleted
     */
    public void deleteRequestsForUser(String userId, UserListener<Boolean> listener) {
        meetupRequestCollection.get()
                .addOnFailureListener(executorService, listener::onUserError)
                .addOnSuccessListener(executorService, queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        MeetupRequest request = snapshotToMeetupRequest(document);
                        boolean isUserSender = request.getSenderId().equals(userId);
                        boolean isUserReceiver = request.getReceiverId().equals(userId);

                        if (isUserSender || isUserReceiver) {
                            document.getReference().delete();
                        }
                    }

                    listener.onUserSuccess(true);
                });
    }
}
