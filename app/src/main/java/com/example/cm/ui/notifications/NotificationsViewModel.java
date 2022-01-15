package com.example.cm.ui.notifications;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Notification;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.NotificationRepository.OnNotificationRepositoryListener;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

public class NotificationsViewModel extends ViewModel implements OnNotificationRepositoryListener {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();

    public NotificationsViewModel() {
        notificationRepository = new NotificationRepository(this);
        notificationRepository.getNotificationsForUser();
        userRepository = new UserRepository();
    }

    public MutableLiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public void acceptFriendFromRequest(Notification notification){
        notification.setState(Notification.NotificationState.NOTIFICATION_ACCEPTED);
        notification.setCreatedAtToNow();
        notificationRepository.accept(notification);
        userRepository.addFriends(notification.getSenderId(), notification.getReceiverId());
    }

    public void declineFriendFromRequest(Notification notification){
        notification.setState(Notification.NotificationState.NOTIFICATION_DECLINED);
        notificationRepository.decline(notification);
        Objects.requireNonNull(notifications.getValue()).remove(notification);
    }

    public void undoDeclineFriendFromRequest(Notification notification, int position) {
        notification.setState(Notification.NotificationState.NOTIFICATION_PENDING);
        notificationRepository.undo(notification);
        Objects.requireNonNull(notifications.getValue()).add(position, notification);
    }

    public void refresh() {
        notificationRepository.getNotificationsForUser();
    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notifications) {
        this.notifications.postValue(notifications);
    }
}