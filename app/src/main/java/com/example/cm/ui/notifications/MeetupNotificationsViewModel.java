package com.example.cm.ui.notifications;

import com.example.cm.data.models.MeetupNotification;
import com.example.cm.data.models.Notification;
import com.example.cm.data.repositories.MeetupNotificationRepository;
import com.example.cm.data.repositories.MeetupRepository;

import java.util.Objects;

public class MeetupNotificationsViewModel extends NotificationsViewModel {

    private final MeetupRepository meetupRepository;

    public MeetupNotificationsViewModel() {
        super();
        meetupRepository = new MeetupRepository();
        notificationRepository = new MeetupNotificationRepository(this);
        notificationRepository.getNotificationsForUser();
    }

    @Override
    public void acceptRequest(Notification notification) {
        super.acceptRequest(notification);
        meetupRepository.addConfirmed(((MeetupNotification) notification).getMeetupId(), notification.getReceiverId());
        MeetupNotification notificationAccepted = new MeetupNotification(
                ((MeetupNotification) notification).getMeetupId(),
                currentUser.getId(),
                currentUser.getFullName(),
                notification.getSenderId(),
                ((MeetupNotification) notification).getLocation(),
                ((MeetupNotification) notification).getMeetupAt(),
                MeetupNotification.NotificationType.MEETUP_ACCEPTED
        );
        notificationRepository.addNotification(notificationAccepted);
    }

    @Override
    public void declineRequest(Notification notification) {
        meetupRepository.addDeclined(((MeetupNotification) notification).getMeetupId(), notification.getReceiverId());
        MeetupNotification notificationDeclined = new MeetupNotification(
                ((MeetupNotification) notification).getMeetupId(),
                currentUser.getId(),
                currentUser.getFullName(),
                notification.getSenderId(),
                ((MeetupNotification) notification).getLocation(),
                ((MeetupNotification) notification).getMeetupAt(),
                MeetupNotification.NotificationType.MEETUP_DECLINED
        );
        notificationRepository.addNotification(notificationDeclined);
        super.declineRequest(notification);
    }

    public void undoDeclineRequest(MeetupNotification notification, int position) {
        notification.setState(Notification.NotificationState.NOTIFICATION_PENDING);
        meetupRepository.addPending(notification.getMeetupId(), notification.getReceiverId());
        notificationRepository.undo(notification);
        Objects.requireNonNull(notifications.getValue()).add(position, notification);
    }
}
