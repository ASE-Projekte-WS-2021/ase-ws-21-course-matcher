package com.example.cm.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Notification;
import com.example.cm.data.models.User;
import com.example.cm.data.repositiories.NotificationRepository;
import com.example.cm.data.repositiories.NotificationRepository.OnNotificationRepositoryListener;

import java.util.List;

public class NotificationsViewModel extends ViewModel implements OnNotificationRepositoryListener {

    private final NotificationRepository notificationRepository;
    private MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();

    public NotificationsViewModel() {
        notificationRepository = new NotificationRepository(this);
        notificationRepository.getNotificationsForUser();
    }

    public MutableLiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notifications) {
        this.notifications.postValue(notifications);
    }
}