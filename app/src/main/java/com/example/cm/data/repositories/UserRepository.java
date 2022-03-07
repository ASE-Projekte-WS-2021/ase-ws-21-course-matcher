package com.example.cm.data.repositories;

import android.net.Uri;

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

    /**
     * Get current authorized FirebaseUser
     *
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
     *
     * @return MutableLiveData of current user
     */
    public MutableLiveData<User> getCurrentUser() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        String currentUserId = auth.getCurrentUser().getUid();
        userCollection.document(currentUserId).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            } if (value != null && value.exists()) {
                User user = snapshotToUser(value);
                mutableUser.postValue(user);
            }
        });
        return mutableUser;
    }

    public String getCurrentAuthUserId() {
        if (auth.getCurrentUser() == null) {
            return null;
        }
        return auth.getCurrentUser().getUid();
    }

    /**
     * Create a user and add it to user collection
     */
    public void createUser(User user) {
        userCollection.document(user.getId()).set(user);
    }

    public void updateField(String field, Object value, Callback callback) {
        try {
            userCollection.document(getFirebaseUser().getUid()).update(field, value)
                    .addOnSuccessListener(task -> {
                        callback.onSuccess(value);
                    })
                    .addOnFailureListener(task -> {
                        callback.onError(false);
                    });
        } catch (Exception e) {
            callback.onError(e);
            e.printStackTrace();
        }
    }

    /**
     * Get a MutableLiveData-List of all mutable users
     */
    public MutableLiveData<List<User>> getUsers() {
        userCollection.addSnapshotListener(executorService, ((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<User> users = snapshotToMutableUserList(value);
                mutableUsers.postValue(users);
            }
        }));
        return mutableUsers;
    }

    /**
     * Check if the current authorized user is befriended with the given user
     *
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
                boolean isBefriended = Objects.requireNonNull(friends).contains(userId);
                isUserBefriended.postValue(isBefriended);
            }
        }));
        return isUserBefriended;
    }

    /**
     * Get list of all users who aren't friends yet
     *
     * @return MutableLiveData-List of mutable not-friends
     */
    public MutableLiveData<List<User>> getUsersNotFriends() {
        userCollection.addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<User> users = new ArrayList<>();

                for (DocumentSnapshot doc : value.getDocuments()) {
                    if (!doc.getId().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())) {

                        if (doc.get("friends") == null) {
                            users.add(snapshotToUser(doc));
                        } else if (!Objects.requireNonNull(Utils.castList(doc.get("friends"), String.class))
                                .contains(auth.getCurrentUser().getUid())) {
                            users.add(snapshotToUser(doc));
                        }
                    }
                }
                mutableUsers.postValue(users);
            }
        });
        return mutableUsers;
    }
  

  /**
     * Get list of not-friends by their username
     *
     * @param query String to search for
     * @return MutableLiveData-List of mutable users with query matching username
     */
    public MutableLiveData<List<User>> getUsersNotFriendsByQuery(String query) {
        userCollection.addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {

                List<User> users = new ArrayList<>();
                String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                for (int i = 0; i < value.getDocuments().size(); i++) {
                    DocumentSnapshot doc = value.getDocuments().get(i);
                    User user = snapshotToUser(doc);
                    boolean isQueryInUsername = user.getUsername().toLowerCase().contains(query.toLowerCase());
                    boolean isQueryInFullName = user.getFullName().toLowerCase().contains(query.toLowerCase());

                    if (!isQueryInUsername && !isQueryInFullName) {
                        continue;
                    }

                    if (doc.get("friends") == null) {
                        users.add(user);
                    } else {
                        List<String> friends = Utils.castList(doc.get("friends"), String.class);
                        if (friends == null) {
                            continue;
                        }
                        if (!friends.contains(currentUserId)) {
                            users.add(user);
                        }
                    }
                }
                mutableUsers.postValue(users);
            }
        });
        return mutableUsers;
    }

  /**
     * Get user with given id
     *
     * @param userId id of user to retrieve
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
     *
     * @param email email of user to retrieve
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
     *
     * @return MutableLiveData-List of mutable friends
     */
    public MutableLiveData<List<User>> getFriends() {
        if (auth.getCurrentUser() == null) {
            return mutableUsers;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        userCollection.document(currentUserId).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                User user = snapshotToUser(value);
                List<String> friends = user.getFriends();
                mutableUsers = getUsersByIds(friends);
            }
        });
        return mutableUsers;
    }

    /**
     * Get user list by list of userIds
     *
     * @param userIds IDs of users to retrieve
     * @return MutableLiveData-List of mutable users with ids
     */
    public MutableLiveData<List<User>> getUsersByIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            mutableUsers.postValue(new ArrayList<>());
            return mutableUsers;
        }

        userCollection.whereIn(FieldPath.documentId(), userIds).addSnapshotListener(executorService, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<User> users = snapshotToMutableUserList(value);
                mutableUsers.postValue(users);
            }
        });
        return mutableUsers;
    }

    /**
     * Get list of friends of a user by their username
     *
     * @param query String to search for
     * @return MutableLiveData-List of mutable friends with query matching username
     */
    public MutableLiveData<List<User>> getFriendsByUsername(String query) {
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
     *
     * @param userIds list of users to search in
     * @param query String to search for
     * @return MutableLiveData-List of mutable users within given list with query matching name
     */
    public MutableLiveData<List<User>> getUsersByIdsAndName(List<String> userIds, String query) {
        userCollection.whereIn(FieldPath.documentId(), userIds).addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<User> users = snapshotToUserList(value);
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
     * Convert a list of snapshots to a list of mutable users
     *
     * @param documents List of snapshots returned from Firestore
     * @return List of mutable users
     */
    private List<User> snapshotToMutableUserList(QuerySnapshot documents) {
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
        user.setBio(document.getString("bio"));
        user.setProfileImageUrl(document.getString("profileImageUrl"));
        return user;
    }

    /**
     * Add each other to friends list
     *
     * @param friend1Id ID of friend to add to other's friend list
     * @param friend2Id ID of friend to add to other's friend list
     */
    public void addFriends(String friend1Id, String friend2Id) {
        userCollection.document(friend1Id).update("friends", FieldValue.arrayUnion(friend2Id));
        userCollection.document(friend2Id).update("friends", FieldValue.arrayUnion(friend1Id));
    }

    public void unfriend(String friendIdToUnfriend) {
        if (auth.getCurrentUser() == null) {
            return;
        }
        String ownId = auth.getCurrentUser().getUid();
        userCollection.document(ownId).update("friends", FieldValue.arrayRemove(friendIdToUnfriend));
        userCollection.document(friendIdToUnfriend).update("friends", FieldValue.arrayRemove(ownId));
    }
}
