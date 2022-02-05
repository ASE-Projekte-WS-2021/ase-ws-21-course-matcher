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

import com.example.cm.data.models.Notification;
import com.example.cm.databinding.FragmentFriendsBinding;
import com.example.cm.databinding.FragmentFriendsNotificationsBinding;
import com.example.cm.ui.adapters.FriendsNotificationListAdapter;
import com.example.cm.ui.adapters.NotificationListAdapter;

public class FriendsNotificationsFragment extends Fragment implements NotificationListAdapter.OnFriendAcceptanceListener, SwipeRefreshLayout.OnRefreshListener {

    private FriendsNotificationsViewModel notificationsViewModel;
    private FriendsNotificationListAdapter notificationListAdapter;
    private FragmentFriendsNotificationsBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsNotificationsBinding.inflate(inflater, container, false);
        initUI();
        initViewModel();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initUI() {
        swipeRefreshLayout = binding.getRoot();
        swipeRefreshLayout.setOnRefreshListener(this);
        notificationListAdapter = new FriendsNotificationListAdapter(this);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setHasFixedSize(true);
        binding.notificationsRecyclerView.setAdapter(notificationListAdapter);
    }

    private void initViewModel() {
        notificationsViewModel = new ViewModelProvider(this).get(FriendsNotificationsViewModel.class);
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

    }
}
