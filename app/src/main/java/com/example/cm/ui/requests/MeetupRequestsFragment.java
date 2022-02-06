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

import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.FragmentMeetupNotificationsBinding;
import com.example.cm.ui.adapters.MeetupRequestListAdapter;
import com.example.cm.ui.adapters.RequestListAdapter;

import java.util.ArrayList;

public class MeetupRequestsFragment extends Fragment implements
        RequestListAdapter.OnRequestAcceptanceListener,
        SwipeRefreshLayout.OnRefreshListener {

    private MeetupRequestsViewModel requestsViewModel;
    private MeetupRequestListAdapter requestsListAdapter;
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
        requestsListAdapter = new MeetupRequestListAdapter(this);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setHasFixedSize(true);
        binding.notificationsRecyclerView.setAdapter(requestsListAdapter);
    }

    private void initViewModel() {
        requestsViewModel = new ViewModelProvider(this).get(MeetupRequestsViewModel.class);
        requestsViewModel.getMeetupRequests().observe(getViewLifecycleOwner(), requests -> {
            if(requests == null){
                return;
            }
            ArrayList<Request> requestsToSet = new ArrayList<>();
            for(MeetupRequest request : requests) {
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
        requestsViewModel.acceptMeetupRequest((MeetupRequest) request);
    }

    @Override
    public void onDecline(Request request) {
        requestsViewModel.declineMeetupRequest((MeetupRequest) request);
    }

    @Override
    public void onUndo(Request request, int position) {
        requestsViewModel.undoDeclineMeetupRequest((MeetupRequest) request, position);
    }
}