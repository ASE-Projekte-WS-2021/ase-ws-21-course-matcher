package com.example.cm.ui.notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cm.data.models.MeetupNotification;
import com.example.cm.data.models.Notification;
import com.example.cm.databinding.FragmentMeetupNotificationsBinding;
import com.example.cm.ui.adapters.MeetupNotificationListAdapter;
import com.example.cm.ui.adapters.NotificationListAdapter;

public class MeetupNotificationsFragment extends Fragment implements NotificationListAdapter.OnFriendAcceptanceListener, SwipeRefreshLayout.OnRefreshListener {

    private MeetupNotificationsViewModel notificationsViewModel;
    private MeetupNotificationListAdapter notificationListAdapter;
    private FragmentMeetupNotificationsBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupNotificationsBinding.inflate(inflater, container, false);
        initUI();
        initViewModel();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initUI() {
        swipeRefreshLayout = binding.getRoot();
        swipeRefreshLayout.setOnRefreshListener(this);
        notificationListAdapter = new MeetupNotificationListAdapter(this);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setHasFixedSize(true);
        binding.notificationsRecyclerView.setAdapter(notificationListAdapter);
    }

    private void initViewModel() {
        notificationsViewModel = new ViewModelProvider(this).get(MeetupNotificationsViewModel.class);
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh() {
        notificationsViewModel.refresh();
        notificationListAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 100);
    }

    @Override
    public void onAccept(Notification notification) {
        notificationsViewModel.acceptRequest(notification);
    }

    @Override
    public void onDecline(Notification notification) {
        notificationsViewModel.declineRequest(notification);
    }

    @Override
    public void onUndo(Notification notification, int position) {
        notificationsViewModel.undoDeclineRequest((MeetupNotification) notification, position);
    }
}