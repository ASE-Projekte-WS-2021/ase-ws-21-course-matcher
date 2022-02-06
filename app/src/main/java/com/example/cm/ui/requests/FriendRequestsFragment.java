package com.example.cm.ui.requests;

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

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.FragmentFriendsNotificationsBinding;
import com.example.cm.ui.adapters.FriendRequestListAdapter;
import com.example.cm.ui.adapters.RequestListAdapter;

import java.util.ArrayList;

public class FriendRequestsFragment extends Fragment implements
        RequestListAdapter.OnRequestAcceptanceListener,
        SwipeRefreshLayout.OnRefreshListener {

    private FriendRequestsViewModel requestsViewModel;
    private FriendRequestListAdapter requestsListAdapter;
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
        requestsListAdapter = new FriendRequestListAdapter(this);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setHasFixedSize(true);
        binding.notificationsRecyclerView.setAdapter(requestsListAdapter);
    }

    private void initViewModel() {
        requestsViewModel = new ViewModelProvider(this).get(FriendRequestsViewModel.class);
        requestsViewModel.getFriendRequests().observe(getViewLifecycleOwner(), requests -> {
            if(requests == null){
                return;
            }
            ArrayList<Request> requestsToSet = new ArrayList<>();
            for(FriendRequest request : requests) {
                requestsToSet.add((Request) request);
            }
            requestsListAdapter.setNotifications(requestsToSet);
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
        requestsViewModel.refresh();
        requestsListAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 100);
    }

    @Override
    public void onAccept(Request request) {
        requestsViewModel.acceptFriendRequest((FriendRequest) request);
    }

    @Override
    public void onDecline(Request request) {
        requestsViewModel.declineFriendRequest((FriendRequest) request);
    }

    public void onUndo(Request request, int position) {

    }
}
