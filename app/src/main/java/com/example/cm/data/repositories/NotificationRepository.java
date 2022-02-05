package com.example.cm.data.repositories;

import com.example.cm.data.models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class NotificationRepository extends Repository {

    protected final FirebaseAuth auth = FirebaseAuth.getInstance();
    protected final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    protected CollectionReference notificationCollection;

    protected final OnNotificationRepositoryListener listener;

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

    /**
     * Delete a notification
     *
     * @param receiverId Id of the receiver
     * @param senderId   Id of the sender
     */
    public void deleteNotification(String receiverId, String senderId) {
        notificationCollection
                .whereEqualTo("receiverId", receiverId).whereEqualTo("senderId", senderId)
                .get().addOnCompleteListener(executorService, task -> {
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
    protected List<Notification> snapshotToNotificationList(QuerySnapshot documents) {return null;}

    /**
     * Convert a single snapshot to a notification model
     *
     * @param document Snapshot of a notification returned from Firestore
     * @return Returns a notification
     */
    protected Notification snapshotToNotification(DocumentSnapshot document, Notification notification) {
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

    public interface OnNotificationRepositoryListener {
        void onNotificationsRetrieved(List<Notification> notifications);
    }

}
