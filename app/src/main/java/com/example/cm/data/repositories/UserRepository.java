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
    private MutableLiveData<List<MutableLiveData<User>>> mutableUsers = new MutableLiveData<>();
    private MutableLiveData<List<MutableLiveData<User>>> mutableUsersNotFriends = new MutableLiveData<>();
    private MutableLiveData<List<MutableLiveData<User>>> mutableUsersFriends = new MutableLiveData<>();

    private MutableLiveData<List<User>> mutableUsersFriendsNotMDL = new MutableLiveData<>();


    public UserRepository() {}

    /**
     * Get current authorized FirebaseUser
     * @return current authorized FirebaseUser
     */
    public FirebaseUser getFirebaseUser() {
        if (auth.getCurrentUser() == null) {
            return null;
        }
        return auth.getCurrentUser();
    }

    /**
     * Get the current user
     * @return MutableLiveData of current user
     */
    public MutableLiveData<User> getCurrentUser() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        String currentUserId = auth.getCurrentUser().getUid();
        userCollection.document(currentUserId).addSnapshotListener(executorService, ((value, error) -> {
            if (error != null) {
                return;
            } if (value != null && value.exists()) {
                User user = snapshotToUser(value);
                mutableUser.postValue(user);
            }
        }));
        return mutableUser;
    }

    /**
     * Create a user and add it to user collection
     */
    public void createUser(User user) {
        userCollection.document(user.getId()).set(user);
    }

    /**
     * Get a list of all users
     */
    public MutableLiveData<List<MutableLiveData<User>>> getUsers() {
        userCollection.addSnapshotListener(executorService, ((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<MutableLiveData<User>> users = snapshotToUserList(value);
                mutableUsers.postValue(users);
            }
        }));
        return mutableUsers;
    }

    /**
     * Check if the current authorized user is befriended with the given user
     * @param friendId id of user to check
     * @return MutableLiveData isBefriended
     */
    public MutableLiveData<Boolean> isUserBefriended(String friendId) {
        MutableLiveData<Boolean> isUserBefriended = new MutableLiveData<>();

        if (auth.getCurrentUser() == null) {
            return isUserBefriended;
        }
        String userId = auth.getCurrentUser().getUid();

        userCollection.document(friendId).addSnapshotListener(executorService, ((value, error) -> {
            if (error != null) {
                return;
            }
            if (value == null || !value.exists() || value.get("friends") == null) {
                isUserBefriended.postValue(false);
            } else {
                List<String> friends = Utils.castList(value.get("friends"), String.class);
                boolean isBefriended = friends.contains(userId);
                isUserBefriended.postValue(isBefriended);
            }
        }));
        return isUserBefriended;
    }

    /**
     * Get list of all users who aren't friends yet
     * @return MutableLiveData-List of not-friends
     */
    public MutableLiveData<List<MutableLiveData<User>>> getUsersNotFriends() {
        userCollection.addSnapshotListener(executorService, ((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<MutableLiveData<User>> users = new ArrayList<>();

                for (DocumentSnapshot doc : value.getDocuments()) {
                    if (doc.get("friends") == null) {
                        users.add(new MutableLiveData<>(snapshotToUser(doc)));
                    } else if (!Utils.castList(doc.get("friends"), String.class)
                            .contains(auth.getCurrentUser().getUid())) {
                        users.add(new MutableLiveData<>(snapshotToUser(doc)));
                    }
                }
                mutableUsersNotFriends.postValue(users);
            }
        }));
        return mutableUsersNotFriends;
    }

    /**
     * Get user with given id
     * @param userId
     * @return MutableLiveData of user with id
     */
    public MutableLiveData<User> getUserById(String userId) {
        userCollection.document(userId).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                User user = snapshotToUser(value);
                mutableUser.postValue(user);
            }
        });
        return mutableUser;
    }

    /**
     * Get user with given email
     * @param email
     * @return MutableLiveData of user with email
     */
    public MutableLiveData<User> getUserByEmail(String email) {
        userCollection.whereEqualTo("email", email).addSnapshotListener(executorService, ((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty() && value.getDocuments().size() <= 1) {
                User user = snapshotToUser(value.getDocuments().get(0));
                mutableUser.postValue(user);
            }
        }));
        return mutableUser;
    }

    /**
     * Get friends of current authorized user
     * @return MutableLiveData-List of friends
     */
    public MutableLiveData<List<MutableLiveData<User>>> getFriends() {
        if (auth.getCurrentUser() == null) {
            return mutableUsers;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        userCollection.document(currentUserId).collection("friends")
                .addSnapshotListener(executorService, ((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<MutableLiveData<User>> friends = snapshotToUserList(value);
                mutableUsersFriends.postValue(friends);
            }
        }));
        return mutableUsersFriends;
    }

    /**
     * Get user list by list of userIds
     * @param userIds
     * @return MutableLiveData-List of users with ids
     */
    public MutableLiveData<List<MutableLiveData<User>>> getUsersByIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            mutableUsers.postValue(new ArrayList<>());
            return mutableUsers;
        }

        userCollection.whereIn(FieldPath.documentId(), userIds).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<MutableLiveData<User>> users = snapshotToUserList(value);
                mutableUsers.postValue(users);
            }
        });
        return mutableUsers;
    }

    /**
     * Get list of users by their username
     * @param query String to search for
     * @return MutableLiveData-List of users with query matching username
     */
    public MutableLiveData<List<MutableLiveData<User>>> getUsersByUsername(String query) {
        userCollection.orderBy("username").startAt(query).endAt(query + "\uf8ff")
                .addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<MutableLiveData<User>> users = snapshotToUserList(value);
                mutableUsers.postValue(users);
            }
        });
        return mutableUsers;
    }

    /**
     * Get list of friends of a user by their username
     * @param query String to search for
     * @return MutableLiveData-List of friends with query matching username
     */
    public MutableLiveData<List<MutableLiveData<User>>> getFriendsByUsername(String query) {
        if (auth.getCurrentUser() == null) {
            return mutableUsers;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        userCollection.document(currentUserId).addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                User user = snapshotToUser(value);
                List<String> friends = user.getFriends();
                mutableUsers = getUsersByIdsAndName(friends, query);
            }
        });
        return mutableUsers;
    }

    /**
     * Get users within given list with query matching name
     * @param userIds list of users to search in
     * @param query String to search for
     * @return MutableLiveData-List of users within given list with query matching name
     */
    public MutableLiveData<List<MutableLiveData<User>>> getUsersByIdsAndName(List<String> userIds, String query) {
        userCollection.whereIn(FieldPath.documentId(), userIds).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<MutableLiveData<User>> users = snapshotToUserList(Objects.requireNonNull(task.getResult()));
                List<MutableLiveData<User>> filteredUsers = new ArrayList<>();

                for (MutableLiveData<User> user : users) {
                    boolean isQueryInUsername = user.getValue().getUsername().toLowerCase().contains(query.toLowerCase());
                    boolean isQueryInFullName = user.getValue().getFullName().toLowerCase().contains(query.toLowerCase());

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
    private List<MutableLiveData<User>> snapshotToUserList(QuerySnapshot documents) {
        List<MutableLiveData<User>> users = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            MutableLiveData<User> userMLD = new MutableLiveData<>();
            userMLD.postValue(snapshotToUser(document));
            users.add(userMLD);
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
