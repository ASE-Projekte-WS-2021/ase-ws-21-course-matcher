package com.example.cm.ui.meetup;

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
import com.example.cm.databinding.FragmentMeetupRequestsBinding;
import com.example.cm.ui.adapters.MeetupRequestListAdapter;

public class MeetupRequestsFragment extends Fragment implements
        MeetupRequestListAdapter.OnMeetupRequestAcceptanceListener,
        SwipeRefreshLayout.OnRefreshListener {

    private MeetupRequestsViewModel requestsViewModel;
    private MeetupRequestListAdapter requestsListAdapter;
    private FragmentMeetupRequestsBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupRequestsBinding.inflate(inflater, container, false);
        initUI();
        initViewModel();
        return binding.getRoot();
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
            requestsListAdapter.setRequests(requests);
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
    public void onAccept(MeetupRequest request) {
        requestsViewModel.acceptMeetupRequest(request);
    }

    @Override
    public void onDecline(MeetupRequest request) {
        requestsViewModel.declineMeetupRequest(request);
    }

    @Override
    public void onUndo(MeetupRequest request, int position) {
        requestsViewModel.undoDeclineMeetupRequest(request, position);
    }
}