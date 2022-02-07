package com.example.cm.ui.add_friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;
import timber.log.Timber;

public class AddFriendsViewModel extends ViewModel {

    public class AddFriendsViewModel extends ViewModel
            implements FriendRequestRepository.OnFriendRequestRepositoryListener {

        private User currentUser;
        private final UserRepository userRepository;
        private final NotificationRepository notificationRepository;
        public MutableLiveData<List<User>> users;
        public MutableLiveData<List<Notification>> sentFriendRequests;
        public MutableLiveData<User> currentUser;
        public OnNotificationSentListener listener;

        public AddFriendsViewModel() {
            userRepository = new UserRepository();
            notificationRepository = new NotificationRepository();

            users = userRepository.getUsersNotFriends();
            currentUser = userRepository.getCurrentUser();
            sentFriendRequests = notificationRepository.getFriendRequests();
        }

        public MutableLiveData<List<User>> getUsers() {
            return users;
        }

        public MutableLiveData<List<FriendRequest>> getSentFriendRequests() {
            return sentFriendRequests;
        }

        public void setOnRequestSentListener(OnRequestSentListener listener) {
            this.listener = listener;
        }

        /**
         * Search a user by their username
         *
         * @param query the username to search for
         */
        public void searchUsers(String query) {
            if (query.isEmpty()) {
                users = userRepository.getUsersNotFriends();
                return;
            }
            users = userRepository.getUsersByUsername(query);
        }

        /**
         * Add a friend request if the user has not sent one to the receiver
         * Otherwise delete the friend request
         *
         * @param receiverId the id of the receiver
         */
        public void sendOrDeleteFriendRequest(String receiverId) {
            if (sentFriendRequests.getValue() == null) {
                return;
            }

            if (hasReceivedFriendRequest(sentFriendRequests.getValue(), receiverId)) {
                onFriendRequestExists(receiverId);
            } else {
                onFriendRequestDoesNotExist(receiverId);
            }
            requestRepository.getFriendRequestsSentBy(currentUser.getId(), requests -> {
                if (requests == null) {
                    return;
                }

                if (hasReceivedFriendRequest(requests, receiverId)) {
                    onFriendRequestExists(receiverId);
                } else {
                    onFriendRequestDoesNotExist(receiverId);
                }

                sentFriendRequests.postValue(requests);
            });
        }

        /**
         * Add a friend request if the user has not sent one to the receiver
         *
         * @param receiverId the id of the receiver
         */
        private void onFriendRequestDoesNotExist(String receiverId) {
            Timber.d("Sending friend request to %s", receiverId);
            FriendRequest request = new FriendRequest(currentUser.getId(), currentUser.getFullName(), receiverId);
            requestRepository.addFriendRequest(request);
            listener.onRequestAdded();
        }

        /**
         * Delete a friend request if the user has sent one to the receiver
         *
         * @param receiverId the id of the receiver
         */
        private void onFriendRequestExists(String receiverId) {
            Timber.d("Deleting friend request to %s", receiverId);
            requestRepository.deleteFriendRequest(receiverId, currentUser.getId());
            listener.onRequestDeleted();
        }

        private Boolean hasReceivedFriendRequest(List<FriendRequest> requests, String receiverId) {
            for (FriendRequest request : requests) {
                if (request.getReceiverId().equals(receiverId) && request.getSenderId().equals(currentUser.getId())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onFriendRequestsRetrieved(List<FriendRequest> requests) {
            this.sentFriendRequests.postValue(requests);
        }

        public interface OnRequestSentListener {
            void onRequestAdded();

            void onRequestDeleted();
        }
    }
}
