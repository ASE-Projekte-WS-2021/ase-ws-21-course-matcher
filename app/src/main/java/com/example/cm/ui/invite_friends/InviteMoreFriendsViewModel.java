package com.example.cm.ui.invite_friends;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_REQUEST;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class InviteMoreFriendsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser;
    private final MeetupRepository meetupRepository;
    private final MeetupRequestRepository meetupRequestRepository;
    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();
    public MutableLiveData<Meetup> meetup;

    public InviteMoreFriendsViewModel(String meetupId) {
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();

        meetupRepository = new MeetupRepository();
        meetup = meetupRepository.getMeetup(meetupId);

        meetupRequestRepository = new MeetupRequestRepository();
    }

    public MutableLiveData<List<User>> getUsers(List<String> usersAlreadyInMeetup) {
        users = userRepository.getFriendsExcept(usersAlreadyInMeetup);
        return users;
    }

    public MutableLiveData<List<String>> getSelectedUsers() {
        return selectedUsers;
    }

    public void toggleSelectUser(String id) {
        List<String> currentlySelectedUsers = new ArrayList<>();

        if (selectedUsers.getValue() != null) {
            currentlySelectedUsers = selectedUsers.getValue();
        }

        if (currentlySelectedUsers.contains(id)) {
            currentlySelectedUsers.remove(id);
        } else {
            currentlySelectedUsers.add(id);
        }
        selectedUsers.postValue(currentlySelectedUsers);
    }

    public void sendMeetupRequests() {
        if (selectedUsers.getValue() != null && currentUser.getValue() != null) {
            Meetup mtp = meetup.getValue();
            for (String invitedFriendId : selectedUsers.getValue()) {
                if (mtp != null) {
                    meetupRepository.addInvited(mtp.getId(), invitedFriendId);
                    MeetupRequest request = new MeetupRequest(
                            mtp.getId(),
                            userRepository.getFirebaseUser().getUid(),
                            invitedFriendId,
                            MEETUP_REQUEST);
                    meetupRequestRepository.addMeetupRequest(request);
                }

            }
            selectedUsers.getValue().clear();
        }
    }

    public List<User> getFilteredUsers(String query) {
        if(users.getValue() == null) {
            return null;
        }

        List<User> filteredUsers = new ArrayList<>();
        for(User user : users.getValue()) {
            boolean isQueryInUsername = user.getUsername().toLowerCase().contains(query.toLowerCase());
            boolean isQueryInFullName = user.getDisplayName().toLowerCase().contains(query.toLowerCase());

            if(isQueryInUsername || isQueryInFullName) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }


    public void clearSelectedUsers() {
        if (selectedUsers.getValue() != null) {
            selectedUsers.getValue().clear();
        }
    }
}