package com.example.cm.ui.notifications;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cm.databinding.FragmentNotificationsBinding;
import com.example.cm.data.models.User;
import com.example.cm.data.models.Notification;


import java.util.ArrayList;
import java.util.Random;

public class NotificationsFragment extends Fragment implements NotificationListAdapter.OnFriendAcceptanceListener, SwipeRefreshLayout.OnRefreshListener {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private NotificationListAdapter notificationListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        initUI();
        initViewModel();
        return binding.getRoot();
    }

    private void initUI() {
        swipeRefreshLayout = binding.getRoot();
        swipeRefreshLayout.setOnRefreshListener(this);
        notificationListAdapter = new NotificationListAdapter();
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setHasFixedSize(true);
        binding.notificationsRecyclerView.setAdapter(notificationListAdapter);
    }

    private void initViewModel() {
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if(notifications == null){
                return;
            }
            notificationListAdapter.setNotifications(notifications);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAccept(Notification notification) {
        notificationsViewModel.acceptFriendFromRequest(notification);
    }

    @Override
    public void onDecline(Notification notification) {
        notificationsViewModel.declineFriendFromRequest(notification);
    }

    @Override
    public void onUndo(Notification notification, int position) {
        notificationsViewModel.undoDeclineFriendFromRequest(notification, position);
    }

    @Override
    public void onRefresh() {
        notificationsViewModel.refresh();
        notificationListAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 100);
    }
}