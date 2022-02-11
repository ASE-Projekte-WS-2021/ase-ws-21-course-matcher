package com.example.cm.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendRequestRepository extends Repository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference friendRequestCollection = firestore.collection(CollectionConfig.FRIEND_REQUESTS.toString());;

    private MutableLiveData<List<FriendRequest>> mutableReceivedRequestList = new MutableLiveData<>();
    private MutableLiveData<List<FriendRequest>> mutableSentRequestList = new MutableLiveData<>();

    public FriendRequestRepository() {}

    /**
     * Add a new Friend Request to collection
     *
     * @param request Requests to be stored
     */
    public void addFriendRequest(FriendRequest request) {
        friendRequestCollection.add(request);
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
     * Get all friend requests for currently signed in user
     */
    public MutableLiveData<List<FriendRequest>> getFriendRequestsForUser() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        String userId = auth.getCurrentUser().getUid();
        friendRequestCollection.whereEqualTo("receiverId", userId)
                .get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<FriendRequest> requests = snapshotToFriendRequestList(Objects.requireNonNull(task.getResult()));
                mutableReceivedRequestList.postValue(requests);
            }
        });

        return mutableReceivedRequestList;
    }

    /**
     * Get all friend requests for sender
     * @param senderId Id of the sender
     */
    public MutableLiveData<List<FriendRequest>> getFriendRequestsSentBy(String senderId) {
        friendRequestCollection.whereEqualTo("senderId", senderId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<FriendRequest> requests = snapshotToFriendRequestList(Objects.requireNonNull(task.getResult()));
                mutableSentRequestList.postValue(requests);
            }
        });

        return mutableSentRequestList;
    }

    private List<FriendRequest> snapshotToFriendRequestList(QuerySnapshot documents) {
        List<FriendRequest> notifications = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            notifications.add(snapshotToFriendRequest(document));
        }
        return notifications;
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
                update("state", Request.RequestState.REQUEST_DECLINED);
    }

    public void undo(FriendRequest request) {
        friendRequestCollection.document(request.getId()).
                update("state", Request.RequestState.REQUEST_PENDING);
    }


    public interface OnFriendRequestRepositoryListener {
        void onFriendRequestsRetrieved(List<FriendRequest> requests);
    }
}
