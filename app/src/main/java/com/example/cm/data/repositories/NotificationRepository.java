package com.example.cm.data.repositories;

import com.example.cm.config.CollectionConfig;
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
        String userId = "0egty7tFLQx39eqSgaAg";
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
        Notification notification = new Notification();
        notification.setId(document.getId());
        notification.setType(document.get("type", Notification.NotificationType.class));
        notification.setSenderId(document.getString("senderId"));
        notification.setSenderName(document.getString("senderName"));
        notification.setReceiverId(document.getString("receiverId"));
        notification.setCreatedAt(document.getDate("createdAt"));
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
