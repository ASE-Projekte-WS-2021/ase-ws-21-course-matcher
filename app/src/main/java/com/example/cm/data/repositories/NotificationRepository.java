package com.example.cm.data.repositories;

import android.util.Log;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.FriendsNotification;
import com.example.cm.data.models.MeetupAcceptedNotification;
import com.example.cm.data.models.MeetupDeclinedNotification;
import com.example.cm.data.models.MeetupNotification;
import com.example.cm.data.models.MeetupRequestNotification;
import com.example.cm.data.models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.cm.data.models.Notification.NotificationType.FRIEND_REQUEST;

public class NotificationRepository extends Repository {

    private final static String TAG = "NotificationRepository";

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference notificationCollection = firestore.collection(CollectionConfig.NOTIFICATIONS.toString());

    private final OnNotificationRepositoryListener listener;

    public NotificationRepository(NotificationRepository.OnNotificationRepositoryListener listener) {
        this.listener = listener;
    }

    /**
     * Get all notifications for currently signed in user
     */
    public void getNotificationsForUser() {
        String userId = "";
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }

        notificationCollection.whereEqualTo("receiverId", userId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<Notification> notifications = snapshotToNotificationList(Objects.requireNonNull(task.getResult()));
                listener.onNotificationsRetrieved(notifications);
            }
        });
    }

    public void getFriendRequestsOfSender(String senderId) {
        notificationCollection.whereEqualTo("senderId", senderId).whereEqualTo("type", FRIEND_REQUEST).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<Notification> notifications = snapshotToNotificationList(Objects.requireNonNull(task.getResult()));
                listener.onNotificationsRetrieved(notifications);
            }
        });
    }

    public void deleteNotification(String receiverId, String senderId, Notification.NotificationType type) {
        notificationCollection
                .whereEqualTo("receiverId", receiverId).whereEqualTo("senderId", senderId)
                .whereEqualTo("type", type).get()
                .addOnCompleteListener(executorService, task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() == null || task.getResult().getDocuments().get(0) == null) {
                            return;
                        }
                        task.getResult().getDocuments().get(0).getReference().delete();
                    }
                });
    }

    /**
     * Create a new notification
     *
     * @param notification Notification to be stored
     */
    public void addNotification(Notification notification) {
        notificationCollection.add(notification);
    }

    /**
     * Create multiple notifications
     *
     * @param notifications List of notifications to be stored
     */
    public void addNotifications(List<Notification> notifications) {
        notificationCollection.add(notifications);
    }

    /**
     * Convert a list of snapshots to a list of notifications
     *
     * @param documents List of notifications returned from Firestore
     * @return Returns a list of notifications
     */
    private List<Notification> snapshotToNotificationList(QuerySnapshot documents) {
        List<Notification> notifications = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            notifications.add(snapshotToNotification(document));
        }
        return notifications;
    }

    /**
     * Convert a single snapshot to a notification model
     *
     * @param document Snapshot of a notification returned from Firestore
     * @return Returns a notification
     */
    public Notification snapshotToNotification(DocumentSnapshot document) {
        Notification notification = null;

        switch(Objects.requireNonNull(document.get("type", Notification.NotificationType.class))){
            case FRIEND_REQUEST:
                notification = new FriendsNotification();
                break;
            case MEETUP_REQUEST:
                notification = new MeetupRequestNotification();
                ((MeetupRequestNotification) notification).setMeetupId(document.getString("meetupId"));
                ((MeetupRequestNotification) notification).setLocation(document.getString("location"));
                ((MeetupRequestNotification) notification).setMeetupAt(document.getString("meetupAt"));
                break;
            case MEETUP_ACCEPTED:
                notification = new MeetupAcceptedNotification();
                ((MeetupAcceptedNotification) notification).setMeetupId(document.getString("meetupId"));
                ((MeetupAcceptedNotification) notification).setLocation(document.getString("location"));
                ((MeetupAcceptedNotification) notification).setMeetupAt(document.getString("meetupAt"));
                break;
            case MEETUP_DECLINED:
                notification = new MeetupDeclinedNotification();
                ((MeetupDeclinedNotification) notification).setMeetupId(document.getString("meetupId"));
                ((MeetupDeclinedNotification) notification).setLocation(document.getString("location"));
                ((MeetupDeclinedNotification) notification).setMeetupAt(document.getString("meetupAt"));
                break;
        }
        if (notification != null){
            notification.setId(document.getId());
            notification.setSenderId(document.getString("senderId"));
            notification.setSenderName(document.getString("senderName"));
            notification.setReceiverId(document.getString("receiverId"));
            notification.setCreatedAt(document.getDate("createdAt"));
            notification.setState(document.get("state", Notification.NotificationState.class));
        }
        return notification;
    }


    /**
     * Set state of notification
     *
     * @param notification notification to accept/decline/undo decline
     */
    public void accept(Notification notification){
        notificationCollection.document(notification.getId()).
                update("state", Notification.NotificationState.NOTIFICATION_ACCEPTED);
        notificationCollection.document(notification.getId()).
                update("createdAt", notification.getCreatedAt());
    }

    public void decline(Notification notification){
        notificationCollection.document(notification.getId()).
                update("state", Notification.NotificationState.NOTIFICATION_DECLINED);
    }

    public void undo(Notification notification){
        notificationCollection.document(notification.getId()).
                update("state", Notification.NotificationState.NOTIFICATION_PENDING);
    }

    public interface OnNotificationRepositoryListener {
        void onNotificationsRetrieved(List<Notification> notification);
    }

}
