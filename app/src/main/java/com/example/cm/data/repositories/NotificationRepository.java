package com.example.cm.data.repositories;

import android.util.Log;

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
        if (auth.getCurrentUser() == null) {
            return;
        }
        String userId = auth.getCurrentUser().getUid();

        // Utilizing snapshotListener to listen to real time changes
        notificationCollection.whereEqualTo("userId", userId).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
            }
            if (queryDocumentSnapshots == null) {
                return;
            }
            snapshotToNotificationList(queryDocumentSnapshots);
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
     * Covert a list of snapshots to a list of notifications
     *
     * @param documents List of notifications returned from Firestore
     */
    private void snapshotToNotificationList(QuerySnapshot documents) {
        List<Notification> notifications = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            notifications.add(snapshotToNotification(document));
        }
        listener.onNotificationsRetrieved(notifications);
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
        notification.setTitle(document.getString("title"));
        notification.setContent(document.getString("content"));
        notification.setType(document.get("type", Notification.NotificationType.class));
        notification.setUserId(document.getString("userId"));
        notification.setCreatedAt(document.getDate("createdAt"));

        return notification;
    }

    public interface OnNotificationRepositoryListener {
        void onNotificationsRetrieved(List<Notification> notification);
    }

}
