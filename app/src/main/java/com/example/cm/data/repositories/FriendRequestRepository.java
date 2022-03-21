package com.example.cm.data.repositories;

import static com.example.cm.data.models.Request.RequestState.REQUEST_DECLINED;
import static com.example.cm.data.models.Request.RequestState.REQUEST_PENDING;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.FriendRequest;
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

public class FriendRequestRepository extends Repository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference friendRequestCollection = firestore.collection(CollectionConfig.FRIEND_REQUESTS.toString());
    private final MutableLiveData<List<MutableLiveData<FriendRequest>>> mutableRequestList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPending = new MutableLiveData<>();

    public FriendRequestRepository() {
    }

    /**
     * Get all friend requests
     */
    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getFriendRequests() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        String userId = auth.getCurrentUser().getUid();
        friendRequestCollection.whereEqualTo("receiverId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(executorService, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        List<MutableLiveData<FriendRequest>> requests = snapshotToMutableFriendRequestList(Objects.requireNonNull(value));
                        mutableRequestList.postValue(requests);
                    }
                });
        return mutableRequestList;
    }

    /**
     * Get all friend requests for currently signed in user
     */
    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getFriendRequestsForUser() {
        if (auth.getCurrentUser() == null) {
            return mutableRequestList;
        }

        String userId = auth.getCurrentUser().getUid();
        friendRequestCollection.whereEqualTo("receiverId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(executorService, ((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        List<MutableLiveData<FriendRequest>> requests = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Request.RequestState currentState = snapshot.get("state", Request.RequestState.class);
                            if (currentState != REQUEST_DECLINED) {
                                requests.add(new MutableLiveData<>(snapshotToFriendRequest(snapshot)));
                            }
                        }
                        mutableRequestList.postValue(requests);
                    }
                }));
        return mutableRequestList;
    }

    /**
     * Get received and sent friend requests for currently signed in user
     */
    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getReceivedAndSentRequestsForUser() {
        if (auth.getCurrentUser() == null) {
            return mutableRequestList;
        }

        String userId = auth.getCurrentUser().getUid();
        friendRequestCollection.whereEqualTo("receiverId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(executorService, ((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        List<MutableLiveData<FriendRequest>> requests = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Request.RequestState currentState = snapshot.get("state", Request.RequestState.class);
                            if (currentState != REQUEST_DECLINED) {
                                requests.add(new MutableLiveData<>(snapshotToFriendRequest(snapshot)));
                            }
                        }
                        addSentRequests(requests, userId);
                    }
                }));
        return mutableRequestList;
    }

    private void addSentRequests(List<MutableLiveData<FriendRequest>> requests, String userId) {
        friendRequestCollection.whereEqualTo("senderId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(executorService, (val, err) -> {
                    if (err == null) {
                        if (val != null && !val.isEmpty()) {
                            for (DocumentSnapshot snapshot : val.getDocuments()) {
                                requests.add(new MutableLiveData<>(snapshotToFriendRequest(snapshot)));
                            }
                        }
                    }
                    mutableRequestList.postValue(requests);
                });
    }

    /**
     * Get all sent requests for sender
     *
     * @param senderId Id of the sender
     * @return mutable list of sent friend requests
     */
    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getFriendRequestsSentBy(String senderId) {
        friendRequestCollection.whereEqualTo("senderId", senderId)
                .addSnapshotListener(executorService, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.isEmpty()) {
                        mutableRequestList.postValue(new ArrayList<>());
                    }
                    if (value != null && !value.isEmpty()) {
                        List<MutableLiveData<FriendRequest>> requests = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Request.RequestState currentState = snapshot.get("state", Request.RequestState.class);
                            if (currentState == REQUEST_PENDING) {
                                requests.add(new MutableLiveData<>(snapshotToFriendRequest(snapshot)));
                            }
                        }
                        mutableRequestList.postValue(requests);
                    }
                });
        return mutableRequestList;
    }

    /**
     * Get all received friend requests for receiver
     *
     * @param receiverId Id of the receiver
     * @return mutable list of received friend requests
     */
    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getFriendRequestsReceived(String receiverId) {
        friendRequestCollection.whereEqualTo("receiverId", receiverId)
                .addSnapshotListener(executorService, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        List<MutableLiveData<FriendRequest>> requests = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Request.RequestState currentState = snapshot.get("state", Request.RequestState.class);
                            if (currentState == REQUEST_PENDING) {
                                requests.add(new MutableLiveData<>(snapshotToFriendRequest(snapshot)));
                            }
                        }
                        mutableRequestList.postValue(requests);
                    }
                });
        return mutableRequestList;
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

        friendRequestCollection.whereEqualTo("state", REQUEST_PENDING).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                boolean isReqPending = false;
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    String receiverId = snapshot.getString("receiverId");
                    String senderId = snapshot.getString("senderId");
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
     * Convert a list of snapshots to a list of mutable friend requests
     *
     * @param documents List of snapshots returned from Firestore
     * @return List of mutable friend requests
     */
    private List<MutableLiveData<FriendRequest>> snapshotToMutableFriendRequestList(QuerySnapshot documents) {
        List<MutableLiveData<FriendRequest>> requests = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            requests.add(new MutableLiveData<>(snapshotToFriendRequest(document)));
        }
        return requests;
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
        request.setSenderId(document.getString("senderId"));
        request.setReceiverId(document.getString("receiverId"));
        request.setCreatedAt(document.getDate("createdAt"));
        request.setState(document.get("state", Request.RequestState.class));

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
                task.getResult().update("id", task.getResult().getId());
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
                .whereEqualTo("receiverId", receiverId).whereEqualTo("senderId", senderId)
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
        friendRequestCollection.document(request.getId()).
                update("state", Request.RequestState.REQUEST_ACCEPTED);
        friendRequestCollection.document(request.getId()).
                update("createdAt", request.getCreatedAt());
    }

    public void decline(FriendRequest request) {
        friendRequestCollection.document(request.getId()).
                update("state", REQUEST_DECLINED);
        deleteFriendRequest(request);
    }

    public void undo(FriendRequest request) {
        friendRequestCollection.document(request.getId()).
                update("state", request.getState());
    }
}
