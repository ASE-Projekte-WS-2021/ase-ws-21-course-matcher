package com.example.cm.ui.notifications;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendsNotification;
import com.example.cm.data.models.MeetupNotification;
import com.example.cm.data.models.Notification;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.NotificationRepository.OnNotificationRepositoryListener;
import com.example.cm.data.repositories.UserRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationsViewModel extends ViewModel implements OnNotificationRepositoryListener, UserRepository.OnUserRepositoryListener {

    protected NotificationRepository notificationRepository;
    protected final UserRepository userRepository;
    protected final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    protected User currentUser;

    public NotificationsViewModel() {
        userRepository = new UserRepository(this);
        userRepository.getUserById(userRepository.getCurrentUser().getUid());
    }

    public MutableLiveData<List<Notification>> getNotifications() {
        return notifications;
    }


    public void acceptRequest(Notification notification){
        notification.setState(Notification.NotificationState.NOTIFICATION_ACCEPTED);
        notification.setCreatedAtToNow();
        notificationRepository.accept(notification);
    }

    public void declineRequest(Notification notification){
        notification.setState(Notification.NotificationState.NOTIFICATION_DECLINED);
        notificationRepository.decline(notification);
        Objects.requireNonNull(notifications.getValue()).remove(notification);
    }

    public void refresh() {
        notificationRepository.getNotificationsForUser();
    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notifications) {
        ArrayList<Notification> notsToDisplay = new ArrayList<>();
        for (Notification notification : notifications) {
            if (notification.getState() != Notification.NotificationState.NOTIFICATION_DECLINED ||
                    notification.getState() != Notification.NotificationState.NOTIFICATION_ANSWERED) {
                notsToDisplay.add(notification);
            }
        }
        this.notifications.postValue(notsToDisplay);
    }

    @Override
    public void onUserRetrieved(User user) {
        currentUser = user;
    }

    @Override
    public void onUsersRetrieved(List<User> users) {

    }
}