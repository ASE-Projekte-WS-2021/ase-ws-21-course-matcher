package com.example.cm.data.repositories;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.User;
import com.example.cm.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference userCollection = firestore.collection(CollectionConfig.USERS.toString());

    private OnUserRepositoryListener listener = null;

    public UserRepository() {
    }

    public UserRepository(OnUserRepositoryListener listener) {
        this.listener = listener;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /**
     * Create a user
     */
    public void createUser(User user){
        userCollection.document(user.getId()).set(user);
    }

    /**
     * Get a list of all users
     */
    public void getUsers() {
        userCollection.get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<User> users = snapshotToUserList(Objects.requireNonNull(task.getResult()));
                listener.onUsersRetrieved(users);
            }
        });
    }

    public void getUserById(String userId) {
        userCollection.document(userId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                User user = snapshotToUser(Objects.requireNonNull(task.getResult()));
                listener.onUserRetrieved(user);
            }
        });
    }

    public void getUserByEmail(String email) {
        userCollection.whereEqualTo("email", email).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                User user = snapshotToUser(Objects.requireNonNull(task.getResult()).getDocuments().get(0));
                listener.onUserRetrieved(user);
            }
        });
    }

    public void getUsersByIds(List<String> userIds) {
        userCollection.whereIn(FieldPath.documentId(), userIds).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<User> users = snapshotToUserList(Objects.requireNonNull(task.getResult()));
                listener.onUsersRetrieved(users);
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
                        List<User> users = snapshotToUserList(Objects.requireNonNull(task.getResult()));
                        listener.onUsersRetrieved(users);
                    }
                });
    }

    /**
     * Get list of friends of a user by their username
     *
     * @param friendsIds List of ids of friends
     * @param query      String to search for
     */
    public void getFriendsByUsername(List<String> friendsIds, String query) {
        userCollection.orderBy("username").startAt(query).endAt(query + "\uf8ff")
                .get().addOnCompleteListener(executorService, task -> {
                    if (task.isSuccessful()) {
                        List<User> users = snapshotToUserList(Objects.requireNonNull(task.getResult()));
                        List<User> friends = new ArrayList<>();
                        for (User user : users) {
                            if (friendsIds.contains(user.getId())) {
                                friends.add(user);
                            }
                        }
                        listener.onUsersRetrieved(friends);
                    }
                });
    }

    /**
     * Convert a list of snapshots to a list of users
     *
     * @param documents List of snapshots returned from Firestore
     * @return List of users
     */
    private List<User> snapshotToUserList(QuerySnapshot documents) {
        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            users.add(snapshotToUser(document));
        }
        return users;
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
        user.setFriends(Utils.castList(document.get("friends"), String.class));

        return user;
    }

    public interface OnUserRepositoryListener {
        void onUsersRetrieved(List<User> users);

        void onUserRetrieved(User user);
    }
}

