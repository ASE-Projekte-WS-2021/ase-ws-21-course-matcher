package com.example.cm.data.repositories;

import android.util.Log;

import com.example.cm.config.CollectionConfig;
import com.example.cm.data.models.FriendsNotification;
import com.example.cm.data.models.Notification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendsNotificationRepository extends NotificationRepository {

    public FriendsNotificationRepository(OnNotificationRepositoryListener listener) {
        super(listener);
        notificationCollection = firestore.collection(CollectionConfig.FRIENDS_NOTIFICATIONS.toString());
    }

    /**
     * Get all friend requests for sender
     *
     * @param senderId Id of the sender
     * @param listener Callback to be called when the request is completed
     */
    public void getFriendRequestsSentBy(String senderId, OnNotificationRepositoryListener listener) {
        Log.e("COLLECTION", notificationCollection.getPath());
        Log.e("COLLECTION-OWN", senderId);

        notificationCollection.whereEqualTo("senderId", senderId)
                .get().addOnCompleteListener(executorService, task -> {
            if (task.isSuccessful()) {
                List<Notification> notifications = snapshotToNotificationList(Objects.requireNonNull(task.getResult()));
                Log.e("COLLECTION", notifications.toString());
                listener.onNotificationsRetrieved(notifications);
            }
        });
    }

    @Override
    protected List<Notification> snapshotToNotificationList(QuerySnapshot documents) {
        List<Notification> notifications = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            notifications.add(snapshotToNotification(document, new FriendsNotification()));
        }
        return notifications;
    }
}
