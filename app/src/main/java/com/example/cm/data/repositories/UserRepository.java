package com.example.cm.data.repositories;

import android.util.Log;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepository extends Repository {
    private static final String TAG = "UserRepository";

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference userCollection = firestore.collection(CollectionConfig.USERS.toString());

    private OnUserRepositoryListener listener = null;


    public UserRepository() {
    }

    public UserRepository(OnUserRepositoryListener listener) {
        this.listener = listener;
    }


    /**
     * Get a list of all users
     */
    public void getUsers() {
        userCollection.get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                snapshotToUserList(Objects.requireNonNull(task.getResult()));
            } else {
                Log.d(TAG, "getUsers: Task is NOT successful...");
            }
        });
    }

    public void getUserById(String userId) {
        Log.d(TAG, "getUserById: " + userId);
        userCollection.document(userId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                listener.onUserRetrieved(snapshotToUser(Objects.requireNonNull(task.getResult())));
            } else {
                Log.d(TAG, "getUserById: Task is NOT successful...");
            }
        });
    }

    public void getUsersByIds(List<String> userIds) {
        userCollection.whereIn(FieldPath.documentId(), userIds).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                snapshotToUserList(Objects.requireNonNull(task.getResult()));
            }
        });
    }

    /**
     * Get list of users by their username
     *
     * @param query String to search for
     */
    public void getUsersByUsername(String query) {
        userCollection.orderBy("username").startAt(query).endAt(query + "\uf8ff")
                .get().addOnCompleteListener(executorService, task -> {
                    if (task.isSuccessful()) {
                        snapshotToUserList(Objects.requireNonNull(task.getResult()));
                    } else {
                        Log.d(TAG, "getUsers: Task is NOT successful...");
                    }
                });
    }

    /**
     * Covert a list of snapshots to a list of users
     *
     * @param documents List of snapshots returned from Firestore
     */
    private void snapshotToUserList(QuerySnapshot documents) {
        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            users.add(snapshotToUser(document));
        }
        listener.onUsersRetrieved(users);
    }

    /**
     * Convert a single snapshot to a user model
     *
     * @param document Snapshot of a user returned from Firestore
     * @return Returns a user model
     */
    public User snapshotToUser(DocumentSnapshot document) {
        User user = new User();
        user.setId(document.getId());
        user.setUsername(document.getString("username"));
        user.setEmail(document.getString("email"));
        user.setFirstName(document.getString("firstName"));
        user.setLastName(document.getString("lastName"));
        user.setFriends((List<String>) document.get("friends"));

        return user;
    }

    public interface OnUserRepositoryListener {
        void onUsersRetrieved(List<User> users);

        void onUserRetrieved(User user);
    }
}

