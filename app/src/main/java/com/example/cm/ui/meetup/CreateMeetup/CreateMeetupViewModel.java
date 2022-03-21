package com.example.cm.ui.meetup.CreateMeetup;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_REQUEST;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.Constants;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Status;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.StorageManager;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
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
    private final MutableLiveData<String> meetupLocationName = new MutableLiveData<>();
    private final MeetupRequestRepository meetupRequestRepository;
    public MutableLiveData<List<User>> users;
    private final MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();
    private String url;
    private String meetupId;

    public CreateMeetupViewModel(Context context) {
        userRepository = UserRepository.getInstance();
        currentUser = userRepository.getCurrentUser();
        users = userRepository.getFriends();
        storageRepository = new StorageManager(context);

        meetupRepository = MeetupRepository.getInstance();
        meetupRequestRepository = MeetupRequestRepository.getInstance();
    }

    public MutableLiveData<List<User>> getUsers() {
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

    public void setMeetupLocationName(String locationName) {
        meetupLocationName.postValue((locationName));
    }

    public LiveData<String> getMeetupLocationName() {
        return meetupLocationName;
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
        storageRepository.uploadImage(bitmap, meetupId, callback, Constants.ImageType.MEETUP_IMAGE);
    }

    public void deleteLocalImg(Uri localUri, Context context) {
        String path = "";

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(localUri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            path = picturePath;
        }

        if (!path.isEmpty()) {
            File fileToDelete = new File(path);
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
    }

    public void createMeetup() {
        Objects.requireNonNull(selectedUsers.getValue());

        Meetup meetupToAdd = new Meetup(
                meetupId,
                userRepository.getFirebaseUser().getUid(),
                meetupLatLng.getValue(),
                meetupLocationName.getValue(),
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