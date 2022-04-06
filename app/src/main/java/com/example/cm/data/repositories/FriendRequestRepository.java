package com.example.cm.data.repositories;

import static com.example.cm.Constants.FIELD_CREATED_AT;
import static com.example.cm.Constants.FIELD_ID;
import static com.example.cm.Constants.FIELD_RECEIVER_ID;
import static com.example.cm.Constants.FIELD_SENDER_ID;
import static com.example.cm.Constants.FIELD_STATE;
import static com.example.cm.data.models.Request.RequestState.REQUEST_DECLINED;
import static com.example.cm.data.models.Request.RequestState.REQUEST_PENDING;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.listener.RequestListener;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class FriendRequestRepository extends Repository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference friendRequestCollection = firestore.collection(CollectionConfig.FRIEND_REQUESTS.toString());
    private final MutableLiveData<List<FriendRequest>> mutableRequestList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPending = new MutableLiveData<>();

    public FriendRequestRepository() {
    }

    /**
     * Get all friend requests for currently signed in user
     */
    public MutableLiveData<List<FriendRequest>> getFriendRequestsForUser() {
        if (auth.getCurrentUser() == null) {
            return mutableRequestList;
        }

        String userId = auth.getCurrentUser().getUid();
        friendRequestCollection.whereEqualTo(FIELD_RECEIVER_ID, userId)
                .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
                .addSnapshotListener(executorService, ((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.isEmpty()) {
                        mutableRequestList.postValue(new ArrayList<>());
                    }
                    if (value != null && !value.isEmpty()) {
                        List<FriendRequest> requests = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Request.RequestState currentState = snapshot.get(FIELD_STATE, Request.RequestState.class);
                            if (currentState != REQUEST_DECLINED) {
                                requests.add(snapshotToFriendRequest(snapshot));
                            }
                        }
                        mutableRequestList.postValue(requests);
                    }
                }));
        return mutableRequestList;
    }

    /**
     * Get all sent requests for sender
     *
     * @param senderId Id of the sender
     */
    public void getFriendRequestsSentBy(String senderId, RequestListener<List<FriendRequest>> listener) {
        friendRequestCollection.whereEqualTo(FIELD_SENDER_ID, senderId)
                .addSnapshotListener(executorService, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.isEmpty()) {
                        mutableRequestList.postValue(new ArrayList<>());
                    }
                    if (value != null && !value.isEmpty()) {
                        List<FriendRequest> requests = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Request.RequestState currentState = snapshot.get(FIELD_STATE, Request.RequestState.class);
                            if (currentState == REQUEST_PENDING) {
                                requests.add(snapshotToFriendRequest(snapshot));
                            }
                        }
                        listener.onRequestSuccess(requests);
                    }
                });
    }

    /**
     * Get all received friend requests for receiver
     *
     * @param receiverId Id of the receiver
     */
    public void getFriendRequestsReceived(String receiverId, RequestListener<List<FriendRequest>> listener) {
        friendRequestCollection.whereEqualTo(FIELD_RECEIVER_ID, receiverId)
                .addSnapshotListener(executorService, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        List<FriendRequest> requests = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Request.RequestState currentState = snapshot.get(FIELD_STATE, Request.RequestState.class);
                            if (currentState == REQUEST_PENDING) {
                                requests.add(snapshotToFriendRequest(snapshot));
                            }
                        }
                        listener.onRequestSuccess(requests);
                    }
                });
    }

    /**
     * check if a friend request is pending for given user
     *
     * @param userId id of given user
     * @return is there a friend request pending
     */
    public MutableLiveData<Boolean> isFriendRequestPendingFor(String userId) {
        if (auth.getCurrentUser() == null) {
            return null;
        }
        String ownUserId = auth.getCurrentUser().getUid();

        friendRequestCollection.whereEqualTo(FIELD_STATE, REQUEST_PENDING).addSnapshotListener(executorService,
                (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        boolean isReqPending = false;
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            String receiverId = snapshot.getString(FIELD_RECEIVER_ID);
                            String senderId = snapshot.getString(FIELD_SENDER_ID);
                            if (receiverId != null && senderId != null) {
                                if (((receiverId.equals(ownUserId) && senderId.equals(userId)) ||
                                        (senderId.equals(ownUserId) && receiverId.equals(userId)))) {
                                    isReqPending = true;
                                    break;
                                }
                            }
                        }
                        isPending.postValue(isReqPending);
                    }
                });
        return isPending;
    }

    /**
     * Convert a single snapshot to a friend request model
     *
     * @param document Snapshot of a friend request returned from Firestore
     * @return Returns a friend request built from firestore document
     */
    private FriendRequest snapshotToFriendRequest(DocumentSnapshot document) {
        FriendRequest request = new FriendRequest();

        request.setId(document.getId());
        request.setSenderId(document.getString(FIELD_SENDER_ID));
        request.setReceiverId(document.getString(FIELD_RECEIVER_ID));
        request.setCreatedAt(document.getDate(FIELD_CREATED_AT));
        request.setState(document.get(FIELD_STATE, Request.RequestState.class));

        return request;
    }

    /**
     * Add a new Friend Request to collection
     *
     * @param request Requests to be stored
     */
    public void addFriendRequest(FriendRequest request) {
        friendRequestCollection.add(request).addOnCompleteListener(task -> {
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
     * Delete a Friend Request from collection
     *
     * @param receiverId Id of the receiver
     * @param senderId   Id of the sender
     */
    public void deleteFriendRequest(String receiverId, String senderId) {
        friendRequestCollection
                .whereEqualTo(FIELD_RECEIVER_ID, receiverId).whereEqualTo(FIELD_SENDER_ID, senderId)
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
     * Delete a Friend Request from collection
     *
     * @param request Request to be deleted
     */
    public void deleteFriendRequest(FriendRequest request) {
        friendRequestCollection.document(request.getId()).delete();
    }

    /**
     * Set state of request
     *
     * @param request to accept/decline/undo decline
     */
    public void accept(FriendRequest request) {
        friendRequestCollection.document(request.getId()).update(FIELD_STATE, Request.RequestState.REQUEST_ACCEPTED);
        friendRequestCollection.document(request.getId()).update(FIELD_CREATED_AT, request.getCreatedAt());
    }

    /**
     * Decline a friend request
     *
     * @param request The request to decline
     */
    public void decline(FriendRequest request) {
        friendRequestCollection.document(request.getId()).update(FIELD_STATE, REQUEST_DECLINED);
        deleteFriendRequest(request);
    }

    /**
     * Delete all requests for a user
     *
     * @param userId   Id of the user
     * @param listener Listener to be notified when deletion is complete
     */
    public void deleteRequestsForUser(String userId, UserListener<Boolean> listener) {
        friendRequestCollection.get()
                .addOnFailureListener(executorService, listener::onUserError)
                .addOnSuccessListener(executorService, queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        FriendRequest request = snapshotToFriendRequest(document);
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
