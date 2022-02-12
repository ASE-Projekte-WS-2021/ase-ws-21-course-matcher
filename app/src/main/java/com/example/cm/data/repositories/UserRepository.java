package com.example.cm.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.User;
import com.example.cm.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
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
    private final MutableLiveData<User> mutableUser = new MutableLiveData<>();
    private MutableLiveData<List<User>> mutableUsers = new MutableLiveData<>();


    public UserRepository() {
    }

    public FirebaseUser getFirebaseUser() {
        if (auth.getCurrentUser() == null) {
            return null;
        }
        return auth.getCurrentUser();
    }

    public FirebaseUser getCurrentAuthUser() {
        return auth.getCurrentUser();
    }

    public MutableLiveData<User> getCurrentUser() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        String currentUserId = auth.getCurrentUser().getUid();
        userCollection.document(currentUserId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                User user = snapshotToUser(Objects.requireNonNull(task.getResult()));
                mutableUser.postValue(user);
            }
        });

        return mutableUser;
    }

    /**
     * Create a user
     */
    public void createUser(User user) {
        userCollection.document(user.getId()).set(user);
    }

    /**
     * Get a list of all users
     */
    public MutableLiveData<List<User>> getUsers() {
        userCollection.get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<User> users = snapshotToUserList(Objects.requireNonNull(task.getResult()));
                mutableUsers.postValue(users);
            }
        });

        return mutableUsers;
    }

    /**
     * Get list of all users who aren't friends yet
     */
    public MutableLiveData<List<User>> getUsersNotFriends() {
        userCollection.get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<User> users = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    if (doc.get("friends") == null) {
                        users.add(snapshotToUser(doc));
                    } else if (!Utils.castList(doc.get("friends"), String.class)
                            .contains(auth.getCurrentUser().getUid())) {
                        users.add(snapshotToUser(doc));
                    }
                }
                mutableUsers.postValue(users);
            }
        });
        return mutableUsers;
    }

    public MutableLiveData<User> getUserById(String userId) {
        userCollection.document(userId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                User user = snapshotToUser(Objects.requireNonNull(task.getResult()));
                mutableUser.postValue(user);
            }
        });
        return mutableUser;
    }

    public MutableLiveData<User> getUserByEmail(String email) {
        userCollection.whereEqualTo("email", email).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                User user = snapshotToUser(Objects.requireNonNull(task.getResult()).getDocuments().get(0));
                mutableUser.postValue(user);
            }
        });
        return mutableUser;
    }

    public MutableLiveData<List<User>> getFriends() {
        if (auth.getCurrentUser() == null) {
            return mutableUsers;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        userCollection.document(currentUserId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                User user = snapshotToUser(Objects.requireNonNull(task.getResult()));
                List<String> friends = user.getFriends();
                mutableUsers = getUsersByIds(friends);
            }
        });

        return mutableUsers;
    }

    public MutableLiveData<List<User>> getUsersByIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            mutableUsers.postValue(new ArrayList<>());
            return mutableUsers;
        }

        userCollection.whereIn(FieldPath.documentId(), userIds).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<User> users = snapshotToUserList(Objects.requireNonNull(task.getResult()));
                mutableUsers.postValue(users);
            }
        });

        return mutableUsers;
    }

    /**
     * Get list of users by their username
     *
     * @param query String to search for
     */
    public MutableLiveData<List<User>> getUsersByUsername(String query) {
        userCollection.orderBy("username").startAt(query).endAt(query + "\uf8ff")
                .get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<User> users = snapshotToUserList(Objects.requireNonNull(task.getResult()));
                mutableUsers.postValue(users);
            }
        });
        return mutableUsers;
    }

    /**
     * Get list of friends of a user by their username
     *
     * @param query String to search for
     */
    public MutableLiveData<List<User>> getFriendsByUsername(String query) {
        if (auth.getCurrentUser() == null) {
            return mutableUsers;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        userCollection.document(currentUserId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                User user = snapshotToUser(Objects.requireNonNull(task.getResult()));
                List<String> friends = user.getFriends();

                mutableUsers = getUsersByIdsAndName(friends, query);
            }
        });

        return mutableUsers;
    }

    public MutableLiveData<List<User>> getUsersByIdsAndName(List<String> userIds, String query) {
        userCollection.whereIn(FieldPath.documentId(), userIds).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<User> users = snapshotToUserList(Objects.requireNonNull(task.getResult()));
                List<User> filteredUsers = new ArrayList<>();

                for (User user : users) {
                    boolean isQueryInUsername = user.getUsername().toLowerCase().contains(query.toLowerCase());
                    boolean isQueryInFullName = user.getFullName().toLowerCase().contains(query.toLowerCase());

                    if (isQueryInUsername || isQueryInFullName) {
                        filteredUsers.add(user);
                    }
                }

                mutableUsers.postValue(filteredUsers);
            }
        });
        return mutableUsers;
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

    public void addFriends(String friend1Id, String friend2Id) {
        userCollection.document(friend1Id).update("friends", FieldValue.arrayUnion(friend2Id));
        userCollection.document(friend2Id).update("friends", FieldValue.arrayUnion(friend1Id));
    }
}
