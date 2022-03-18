package com.example.cm.ui.meetup.CreateMeetup;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_REQUEST;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Status;
import com.example.cm.data.models.StatusFlag;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.StorageManager;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CreateMeetupViewModel extends ViewModel implements Serializable {

    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser;
    private final MeetupRepository meetupRepository;
    private final StorageManager storageRepository;
    public MutableLiveData<Status> status = new MutableLiveData<>();
    private final MutableLiveData<LatLng> meetupLatLng = new MutableLiveData<>();
    private final MutableLiveData<String> meetupLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> meetupIsPrivate = new MutableLiveData<>();
    private final MutableLiveData<Date> meetupTimestamp = new MutableLiveData<>();
    private final MeetupRequestRepository meetupRequestRepository;
    private MutableLiveData<List<MutableLiveData<User>>> users;
    private final MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();
    private String url;
    private String meetupId;

    public CreateMeetupViewModel(Context context) {
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();
        users = userRepository.getFriends();

        meetupRepository = new MeetupRepository();
        meetupRequestRepository = new MeetupRequestRepository();

        storageRepository = new StorageManager(context);
    }

    public MutableLiveData<List<MutableLiveData<User>>> getUsers() {
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

    public LiveData<LatLng> getMeetupLatLng() {
        return meetupLatLng;
    }

    public void setMeetupLatLng(LatLng latLng) {
        meetupLatLng.postValue(latLng);
    }

    public void setMeetupLocation(String location) {
        meetupLocation.postValue(location);
    }

    public LiveData<Boolean> getMeetupIsPrivate() {
        return meetupIsPrivate;
    }

    public LiveData<Date> getMeetupTimestamp() {
        return meetupTimestamp;
    }

    public void setMeetupTimestamp(Date timestamp) {
        meetupTimestamp.postValue(timestamp);
    }

    public void setIsPrivate(Boolean isPrivate) {
        meetupIsPrivate.postValue(isPrivate);
    }

    public void setMeetupImg(Bitmap bitmap, StorageManager.Callback callback) {
        meetupId = UUID.randomUUID().toString();
        String filename = Constants.PATH_TO_MEETUP_IMG;
        File file = new File(filename);
        file.getParentFile().mkdirs();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, Constants.QUALITY_MEETUP_IMG, out);
            out.flush();
            out.close();

            Uri uri = Uri.fromFile(file);
            if (uri == null) {
                status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_general_error));
                return;
            }
            storageRepository.uploadImage(uri, meetupId, callback, Constants.ImageType.MEETUP_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteLocalImg() {
        String filename = Constants.PATH_TO_MEETUP_IMG;
        File file = new File(filename);
        file.delete();
    }

    public void createMeetup() {
        Objects.requireNonNull(selectedUsers.getValue());

        Meetup meetupToAdd = new Meetup(
                meetupId,
                userRepository.getFirebaseUser().getUid(),
                meetupLatLng.getValue(),
                meetupTimestamp.getValue(),
                Boolean.TRUE.equals(meetupIsPrivate.getValue()),
                selectedUsers.getValue(), url);

        meetupRepository.addMeetup(meetupToAdd);
        sendMeetupRequests();
    }

    public void sendMeetupRequests() {
        // Create notifications for each invited user
        if (selectedUsers.getValue() != null && currentUser.getValue() != null) {
            for (String invitedFriendId : selectedUsers.getValue()) {
                MeetupRequest request = new MeetupRequest(
                        meetupId,
                        userRepository.getFirebaseUser().getUid(),
                        currentUser.getValue().getFullName(),
                        invitedFriendId,
                        meetupLocation.getValue(),
                        meetupTimestamp.getValue(),
                        url,
                        MEETUP_REQUEST);
                meetupRequestRepository.addMeetupRequest(request);
            }
            selectedUsers.getValue().clear();
        }
    }

    public void searchUsers(String query) {
        if (users.getValue() != null) {
            users.getValue().clear();
            users = userRepository.getFriendsByUsername(query);
        }
    }

    public void clearSelectedUsers() {
        if (selectedUsers.getValue() != null) {
            selectedUsers.getValue().clear();
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }
}