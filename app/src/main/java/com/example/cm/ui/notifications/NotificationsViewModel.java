package com.example.cm.ui.notifications;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendsNotification;
import com.example.cm.data.models.MeetupNotification;
import com.example.cm.data.models.Notification;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

public class NotificationsViewModel extends ViewModel {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final MeetupRepository meetupRepository;
    private final MutableLiveData<List<Notification>> notifications;
    private final MutableLiveData<User> currentUser;

    public NotificationsViewModel() {
        notificationRepository = new NotificationRepository();
        notifications = notificationRepository.getNotificationsForUser();
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();
        meetupRepository = new MeetupRepository();
    }

    public MutableLiveData<List<Notification>> getNotifications() {
        return notifications;
    }


    public void acceptRequest(Notification notification) {
        notification.setState(Notification.NotificationState.NOTIFICATION_ACCEPTED);
        notification.setCreatedAtToNow();
        notificationRepository.accept(notification);

        if (currentUser.getValue() == null) {
            return;
        }

        if (notification instanceof FriendsNotification) {
            userRepository.addFriends(notification.getSenderId(), notification.getReceiverId());
        } else if (notification instanceof MeetupNotification) {
            meetupRepository.addConfirmed(((MeetupNotification) notification).getMeetupId(), notification.getReceiverId());
            MeetupNotification notificationAccepted = new MeetupNotification(
                    ((MeetupNotification) notification).getMeetupId(),
                    currentUser.getValue().getId(),
                    currentUser.getValue().getFullName(),
                    notification.getSenderId(),
                    ((MeetupNotification) notification).getLocation(),
                    ((MeetupNotification) notification).getMeetupAt(),
                    Notification.NotificationType.MEETUP_ACCEPTED
            );
            notificationRepository.addNotification(notificationAccepted);
        }
    }

    public void declineRequest(Notification notification) {

        if (currentUser.getValue() == null) {
            return;
        }

        if (notification instanceof MeetupNotification) {
            meetupRepository.addDeclined(((MeetupNotification) notification).getMeetupId(), notification.getReceiverId());
            MeetupNotification notificationDeclined = new MeetupNotification(
                    ((MeetupNotification) notification).getMeetupId(),
                    currentUser.getValue().getId(),
                    currentUser.getValue().getFullName(),
                    notification.getSenderId(),
                    ((MeetupNotification) notification).getLocation(),
                    ((MeetupNotification) notification).getMeetupAt(),
                    Notification.NotificationType.MEETUP_DECLINED
            );
            notificationRepository.addNotification(notificationDeclined);
        }
        notification.setState(Notification.NotificationState.NOTIFICATION_DECLINED);
        notificationRepository.decline(notification);
        Objects.requireNonNull(notifications.getValue()).remove(notification);
    }

    public void undoDeclineRequest(Notification notification, int position) {
        notification.setState(Notification.NotificationState.NOTIFICATION_PENDING);
        notificationRepository.undo(notification);
        Objects.requireNonNull(notifications.getValue()).add(position, notification);
    }

    public void refresh() {
        notificationRepository.getNotificationsForUser();
    }
}