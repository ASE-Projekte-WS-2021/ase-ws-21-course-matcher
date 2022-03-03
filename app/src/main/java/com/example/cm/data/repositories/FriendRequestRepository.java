package com.example.cm.data.repositories;

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

import static com.example.cm.data.models.Request.RequestState.REQUEST_DECLINED;

public class FriendRequestRepository extends Repository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference friendRequestCollection = firestore.collection(CollectionConfig.FRIEND_REQUESTS.toString());
    private final MutableLiveData<List<MutableLiveData<FriendRequest>>> mutableRequestList = new MutableLiveData<>();

    public FriendRequestRepository() {
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
                        List<MutableLiveData<FriendRequest>> requestsToReturn = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Request.RequestState currentState = snapshot.get("state", Request.RequestState.class);
                            if (currentState != REQUEST_DECLINED) {
                                requestsToReturn.add(new MutableLiveData<>(snapshotToFriendRequest(snapshot)));
                            }
                        }
                        mutableRequestList.postValue(requestsToReturn);
                    }
                }));
        return mutableRequestList;
    }

    /**
     * Get all friend requests for sender
     * @param senderId Id of the sender
     */
    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getFriendRequestsSentBy(String senderId) {
        friendRequestCollection.whereEqualTo("senderId", senderId)
                .addSnapshotListener(executorService, ((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if(value != null && value.isEmpty()) {
                        mutableRequestList.postValue(new ArrayList<>());
                    }
                    if (value != null && !value.isEmpty()) {
                        List<MutableLiveData<FriendRequest>> requests = snapshotToMutableFriendRequestList(value);
                        mutableRequestList.postValue(requests);
                    }
                }));
        return mutableRequestList;
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
        request.setSenderName(document.getString("senderName"));
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
