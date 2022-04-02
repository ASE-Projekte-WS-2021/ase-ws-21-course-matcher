package com.example.cm.data.repositories;

import static com.example.cm.Constants.FIELD_FRIENDS;
import static com.example.cm.Constants.FIELD_LOCATION;
import static com.example.cm.Constants.FIELD_PROFILE_IMAGE_STRING;
import static com.example.cm.Constants.MAX_QUERY_LENGTH;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.listener.Callback;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.User;
import com.example.cm.data.models.UserPOJO;
import com.example.cm.utils.Utils;
import com.google.common.collect.Lists;
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
import java.util.HashSet;
import java.util.List;

public class UserRepository extends Repository {

    private static UserRepository instance;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference userCollection = firestore.collection(CollectionConfig.USERS.toString());
    private final MutableLiveData<User> mutableUser = new MutableLiveData<>();
    private MutableLiveData<List<User>> mutableUsers = new MutableLiveData<>();
    private MutableLiveData<List<String>> mutableUsernames = new MutableLiveData<>();

    public UserRepository() {
    }

    /**
     * Returns the UserRepository
     *
     * @return instance of the UserRepository
     */
    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
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
     * Get the current user.
     * Used to live update the current users' data on changes
     *
     * @return MutableLiveData of current user
     */
    public MutableLiveData<User> getCurrentUser() {
        if (auth.getCurrentUser() == null) {
            return mutableUser;
        }

        String currentUserId = auth.getCurrentUser().getUid();
        userCollection.document(currentUserId).addSnapshotListener(executorService, (value, error) -> {
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
     * Get the current user just once
     *
     * @return MutableLiveData of current user
     */
    public MutableLiveData<User> getStaticCurrentUser() {
        if (auth.getCurrentUser() == null) {
            return mutableUser;
        }

        String currentUserId = auth.getCurrentUser().getUid();
        userCollection.document(currentUserId).get()
                .addOnSuccessListener(executorService, (value) -> {
                    if (value != null && value.exists()) {
                        User user = snapshotToUser(value);
                        mutableUser.postValue(user);
                    }
                });
        return mutableUser;
    }

    /**
     * Get the id of the currently logged in user
     *
     * @return id of the currently logged in user
     */
    public String getCurrentAuthUserId() {
        if (auth.getCurrentUser() == null) {
            return null;
        }
        return auth.getCurrentUser().getUid();
    }

    /**
     * Create a user and add it to user collection
     *
     * @param user user to add
     */
    public void createUser(User user) {
        userCollection.document(user.getId()).set(user);
    }

    /**
     * Update the current user
     *
     * @param field    field to update
     * @param value    value to update
     * @param callback callback when update is done
     */
    public void updateField(String field, Object value, Callback callback) {
        try {
            userCollection.document(getFirebaseUser().getUid()).update(field, value)
                    .addOnSuccessListener(task -> callback.onSuccess(value))
                    .addOnFailureListener(task -> callback.onError(false));
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
            if (value == null || !value.exists() || value.get(FIELD_FRIENDS) == null) {
                isUserBefriended.postValue(false);
            } else {
                List<String> friends = Utils.castList(value.get(FIELD_FRIENDS), String.class);
                if (friends == null) {
                    return;
                }

                boolean isBefriended = friends.contains(userId);
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
            if (error != null || auth.getCurrentUser() == null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<User> users = new ArrayList<>();

                for (DocumentSnapshot doc : value.getDocuments()) {
                    String userId = auth.getCurrentUser().getUid();
                    if (!doc.getId().equals(userId)) {
                        List<String> friends = Utils.castList(doc.get(FIELD_FRIENDS), String.class);
                        if (friends == null) {
                            users.add(snapshotToUser(doc));
                        } else if (!friends.contains(userId)) {
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
            if (value != null && !value.isEmpty() && auth.getCurrentUser() != null) {
                List<User> users = new ArrayList<>();
                String currentUserId = auth.getCurrentUser().getUid();

                for (int i = 0; i < value.getDocuments().size(); i++) {
                    DocumentSnapshot doc = value.getDocuments().get(i);
                    User user = snapshotToUser(doc);

                    boolean isCurrentUser = doc.getId().equals(currentUserId);
                    boolean isQueryInUsername = user.getUsername().toLowerCase().contains(query.toLowerCase());
                    boolean isQueryInFullName = user.getDisplayName().toLowerCase().contains(query.toLowerCase());

                    if (isCurrentUser || (!isQueryInUsername && !isQueryInFullName)) {
                        continue;
                    }

                    if (doc.get(FIELD_FRIENDS) == null) {
                        users.add(user);
                    } else {
                        List<String> friends = Utils.castList(doc.get(FIELD_FRIENDS), String.class);
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
     * Get friends of current authorized user except friends with given ids
     *
     * @param userIds IDs of users you don't want to return
     * @return MutableLiveData-List of mutable friends
     */
    public MutableLiveData<List<User>> getFriendsExcept(List<String> userIds) {
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
                List<String> friendsToReturn = user.getFriends();
                if (userIds != null && !userIds.isEmpty()) {
                    friendsToReturn.removeAll(userIds);
                }
                mutableUsers = getUsersByIds(friendsToReturn);
            }
        });
        return mutableUsers;
    }

    /**
     * Get static friends list of current authorized user
     *
     * @param listener listener to be called when friends are received
     */
    public void getStaticFriends(UserListener<List<User>> listener) {
        if (auth.getCurrentUser() == null) {
            return;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        userCollection.document(currentUserId).get()
                .addOnFailureListener(executorService, listener::onUserError)
                .addOnSuccessListener(executorService, (value) -> {
                    if (value != null && value.exists()) {
                        User user = snapshotToUser(value);
                        List<String> friends = user.getFriends();

                        // Handle case when user does not have friends
                        if (friends == null || friends.isEmpty()) {
                            listener.onUserSuccess(new ArrayList<>());
                            return;
                        }
                        getStaticUsersByIds(friends, listener);
                    }
                });
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

        List<String> userIdsNoDuplicates = new ArrayList<>(new HashSet<>(userIds));

        List<List<String>> subLists = Lists.partition(userIdsNoDuplicates, MAX_QUERY_LENGTH);
        for (List<String> subList : subLists) {
            userCollection.whereIn(FieldPath.documentId(), subList).addSnapshotListener(executorService,
                    (value, error) -> {
                        if (error != null) {
                            return;
                        }
                        if (value != null && !value.isEmpty()) {
                            List<User> users = snapshotToMutableUserList(value);
                            mutableUsers.postValue(users);
                        }
                    });
        }
        return mutableUsers;
    }

    /**
     * Get static user list by list of userIds
     *
     * @param userIds  IDs of users to retrieve
     * @param listener UserListener to return result
     */
    public void getStaticUsersByIds(List<String> userIds, UserListener<List<User>> listener) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        List<List<String>> batches = Lists.partition(userIds, MAX_QUERY_LENGTH);
        for (List<String> batch : batches) {
            userCollection.whereIn(FieldPath.documentId(), batch).get()
                    .addOnFailureListener(executorService, listener::onUserError)
                    .addOnSuccessListener(executorService, (value) -> {
                        if (value != null && !value.isEmpty()) {
                            List<User> users = snapshotToUserList(value);
                            listener.onUserSuccess(users);
                        }
                    });
        }
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
                if (friends == null || friends.isEmpty()) {
                    return;
                }
                mutableUsers = getUsersByIdsAndName(friends, query);
            }
        });
        return mutableUsers;
    }

    /**
     * Get list of friends of a user by their username except friends with given ids
     *
     * @param query         String to search for
     * @param userIdsExcept IDs of users you don't want to return
     * @return MutableLiveData-List of mutable friends with query matching username
     */
    public MutableLiveData<List<User>> getFriendsByUsernameExcept(String query, List<String> userIdsExcept) {
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
                if (friends == null || friends.isEmpty()) {
                    return;
                }
                if (userIdsExcept != null && !userIdsExcept.isEmpty()) {
                    friends.removeAll(userIdsExcept);
                }

                mutableUsers = getUsersByIdsAndName(friends, query);
            }
        });
        return mutableUsers;
    }

    /**
     * Get users within given list with query matching name
     *
     * @param userIds list of users to search in
     * @param query   String to search for
     * @return MutableLiveData-List of mutable users within given list with query
     * matching name
     */
    public MutableLiveData<List<User>> getUsersByIdsAndName(List<String> userIds, String query) {
        if (userIds == null || userIds.isEmpty()) {
            return mutableUsers;
        }

        userCollection.whereIn(FieldPath.documentId(), userIds).addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && !value.isEmpty()) {
                List<User> users = snapshotToUserList(value);
                List<User> filteredUsers = new ArrayList<>();

                for (User user : users) {
                    boolean isQueryInUsername = user.getUsername().toLowerCase().contains(query.toLowerCase());
                    boolean isQueryInFullName = user.getDisplayName().toLowerCase().contains(query.toLowerCase());

                    if (isQueryInUsername || isQueryInFullName) {
                        filteredUsers.add(user);
                    }
                }
                mutableUsers.postValue(filteredUsers);
            }
        });
        return mutableUsers;
    }

    public void getUsernames(UsernamesRetrievedCallback callback) {
        List<String> usernames = new ArrayList<>();
        userCollection.get().addOnCompleteListener(executorService, task -> {
            if (task.isComplete() && FirebaseAuth.getInstance().getCurrentUser() != null) {
                QuerySnapshot result = task.getResult();
                List<User> users = snapshotToUserList(result);

                for (User user : users) {
                    usernames.add(user.getUsername());
                }

                if (getCurrentUser().getValue() != null) {
                    callback.onUsernamesRetrieved(usernames, getCurrentUser().getValue().getUsername());
                }
            }
        });
    }

    /**
     * Updates the user's profile picture
     *
     * @param profileImageString Base64 string of the image
     * @param userId             ID of the user
     */
    public void updateProfileImage(String profileImageString, String userId) {
        userCollection.document(userId).update(FIELD_PROFILE_IMAGE_STRING, profileImageString);
    }

    /**
     * Add each other to friends list
     *
     * @param friend1Id ID of friend to add to other's friend list
     * @param friend2Id ID of friend to add to other's friend list
     */
    public void addFriends(String friend1Id, String friend2Id) {
        userCollection.document(friend1Id).update(FIELD_FRIENDS, FieldValue.arrayUnion(friend2Id));
        userCollection.document(friend2Id).update(FIELD_FRIENDS, FieldValue.arrayUnion(friend1Id));
    }

    /**
     * Remove each other from friends list
     *
     * @param friendIdToUnfriend ID of friend to remove from friend list
     */
    public void unfriend(String friendIdToUnfriend) {
        if (auth.getCurrentUser() == null) {
            return;
        }
        String ownId = auth.getCurrentUser().getUid();
        userCollection.document(ownId).update(FIELD_FRIENDS, FieldValue.arrayRemove(friendIdToUnfriend));
        userCollection.document(friendIdToUnfriend).update(FIELD_FRIENDS, FieldValue.arrayRemove(ownId));
    }

    /**
     * Delete a user from the friends list
     *
     * @param userId   ID of user to delete from friends list
     * @param listener listener to be notified when the user is deleted
     */
    public void deleteUserFromFriendsLists(String userId, UserListener<Boolean> listener) {
        userCollection.get()
                .addOnFailureListener(executorService, listener::onUserError)
                .addOnSuccessListener(executorService, documentSnapshot -> {
                    for (DocumentSnapshot document : documentSnapshot.getDocuments()) {
                        User user = snapshotToUser(document);
                        if (user.getFriends() == null) {
                            continue;
                        }

                        if (!user.getFriends().contains(userId)) {
                            continue;
                        }
                        userCollection.document(user.getId()).update(FIELD_FRIENDS, FieldValue.arrayRemove(userId));
                    }
                    listener.onUserSuccess(true);
                });
    }

    /**
     * Deletes a user from the database
     *
     * @param userId   ID of user to delete
     * @param listener Listener to notify when user is deleted
     */
    public void deleteUser(String userId, UserListener<Boolean> listener) {
        userCollection.document(userId).delete()
                .addOnFailureListener(executorService, listener::onUserError)
                .addOnSuccessListener(executorService, documentSnapshot -> {
                    listener.onUserSuccess(true);
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
        UserPOJO userPOJO = document.toObject(UserPOJO.class);
        assert userPOJO != null;
        userPOJO.setId(document.getId());
        userPOJO.setLocation((List<Double>) document.get(FIELD_LOCATION));

        return userPOJO.toObject();
    }

    public interface UsernamesRetrievedCallback {
        void onUsernamesRetrieved(List<String> usernames, String currentAuthUserId);
    }
}