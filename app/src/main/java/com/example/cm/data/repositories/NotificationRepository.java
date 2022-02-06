package com.example.cm.data.repositories;

import static com.example.cm.data.models.Notification.NotificationType.FRIEND_REQUEST;
import static com.example.cm.data.models.Notification.NotificationType.MEETUP_ACCEPTED;
import static com.example.cm.data.models.Notification.NotificationType.MEETUP_DECLINED;
import static com.example.cm.data.models.Notification.NotificationType.MEETUP_REQUEST;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.FriendsNotification;
import com.example.cm.data.models.MeetupNotification;
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

import timber.log.Timber;

public class NotificationRepository extends Repository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference notificationCollection = firestore.collection(CollectionConfig.NOTIFICATIONS.toString());
    private final MutableLiveData<List<Notification>> mutableNotifications = new MutableLiveData<>();

    public NotificationRepository() {
    }

    /**
     * Get all notifications for currently signed in user
     */
    public MutableLiveData<List<Notification>> getNotificationsForUser() {
        String userId = "";
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }

        notificationCollection.whereEqualTo("receiverId", userId).get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<Notification> notifications = snapshotToNotificationList(Objects.requireNonNull(task.getResult()));
                mutableNotifications.postValue(notifications);
            }
        });

        return mutableNotifications;
    }


    /**
     * Get all friend requests for sender
     */
    public MutableLiveData<List<Notification>> getFriendRequests() {
        if (auth.getCurrentUser() == null) {
            return mutableNotifications;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        notificationCollection.whereEqualTo("senderId", currentUserId).whereEqualTo("type", FRIEND_REQUEST)
                .get().addOnCompleteListener(executorService, task -> {
                    if (task.isSuccessful()) {
                        Timber.d("GetFriendRequests: Success");
                        List<Notification> notifications = snapshotToNotificationList(Objects.requireNonNull(task.getResult()));
                        mutableNotifications.postValue(notifications);
                    }
                });
        return mutableNotifications;
    }

    /**
     * Delete a notification
     *
     * @param receiverId Id of the receiver
     * @param type       Type of the notification
     */
    public void deleteNotification(String receiverId, Notification.NotificationType type) {
        if (auth.getCurrentUser() == null) {
            return;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        notificationCollection
                .whereEqualTo("receiverId", receiverId).whereEqualTo("senderId", currentUserId)
                .whereEqualTo("type", type).get()
                .addOnCompleteListener(executorService, task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() == null) {
                            return;
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
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

        Notification.NotificationType notType = Objects.requireNonNull(document.get("type", Notification.NotificationType.class));
        if (notType == FRIEND_REQUEST) {
            notification = new FriendsNotification();
        } else if (notType == MEETUP_REQUEST || notType == MEETUP_ACCEPTED || notType == MEETUP_DECLINED) {
            notification = new MeetupNotification(notType);
            ((MeetupNotification) notification).setMeetupId(document.getString("meetupId"));
            ((MeetupNotification) notification).setLocation(document.getString("location"));
            ((MeetupNotification) notification).setMeetupAt(document.getString("meetupAt"));
        }
        if (notification != null) {
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
    public void accept(Notification notification) {
        notificationCollection.document(notification.getId()).
                update("state", Notification.NotificationState.NOTIFICATION_ACCEPTED);
        notificationCollection.document(notification.getId()).
                update("createdAt", notification.getCreatedAt());
    }

    public void decline(Notification notification) {
        notificationCollection.document(notification.getId()).
                update("state", Notification.NotificationState.NOTIFICATION_DECLINED);
    }

    public void undo(Notification notification) {
        notificationCollection.document(notification.getId()).
                update("state", Notification.NotificationState.NOTIFICATION_PENDING);
    }
}
