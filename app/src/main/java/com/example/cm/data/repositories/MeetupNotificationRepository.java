package com.example.cm.data.repositories;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.FriendsNotification;
import com.example.cm.data.models.MeetupNotification;
import com.example.cm.data.models.Notification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeetupNotificationRepository extends NotificationRepository {

    public MeetupNotificationRepository(OnNotificationRepositoryListener listener) {
        super(listener);
        notificationCollection = firestore.collection(CollectionConfig.MEETUP_NOTIFICATIONS.toString());
    }

    @Override
    protected List<Notification> snapshotToNotificationList(QuerySnapshot documents) {
        List<Notification> notifications = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Notification.NotificationType notType = Objects.requireNonNull(document.get("type", Notification.NotificationType.class));
            notifications.add(snapshotToNotification(document, new MeetupNotification(notType)));
        }
        return notifications;
    }

    @Override
    protected Notification snapshotToNotification(DocumentSnapshot document, Notification notification) {
        ((MeetupNotification) notification).setMeetupId(document.getString("meetupId"));
        ((MeetupNotification) notification).setLocation(document.getString("location"));
        ((MeetupNotification) notification).setMeetupAt(document.getString("meetupAt"));
        return super.snapshotToNotification(document, notification);
    }

}
