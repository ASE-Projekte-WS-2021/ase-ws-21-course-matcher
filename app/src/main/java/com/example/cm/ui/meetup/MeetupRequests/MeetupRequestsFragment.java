package com.example.cm.ui.meetup.MeetupRequests;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.FragmentMeetupRequestsBinding;
import com.example.cm.ui.adapters.MeetupRequestListAdapter;
import com.example.cm.ui.adapters.SwipeToDelete;
import com.example.cm.utils.Navigator;

public class MeetupRequestsFragment extends Fragment implements
        MeetupRequestListAdapter.OnMeetupRequestListener,
        SwipeRefreshLayout.OnRefreshListener {

    private MeetupRequestsViewModel requestsViewModel;
    private MeetupRequestListAdapter requestsListAdapter;
    private FragmentMeetupRequestsBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Navigator navigator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupRequestsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        initUI();
        initViewModel();
        return binding.getRoot();
    }

    private void initUI() {
        swipeRefreshLayout = binding.getRoot();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initViewModel() {
        requestsViewModel = new ViewModelProvider(this).get(MeetupRequestsViewModel.class);
        requestsViewModel.getMeetupRequests().observe(getViewLifecycleOwner(), requests -> {
            requestsListAdapter = new MeetupRequestListAdapter(requests, this);
            binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.notificationsRecyclerView.setHasFixedSize(true);
            binding.notificationsRecyclerView.setAdapter(requestsListAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(requestsListAdapter));
            itemTouchHelper.attachToRecyclerView(binding.notificationsRecyclerView);
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
        /*requestsViewModel.refresh();
        requestsListAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 100);*/
    }

    @Override
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_MEETUP_ID, id);
        navigator.getNavController().navigate(R.id.navigateToMeetupDetailed, bundle);
    }

    @Override
    public void onItemDeleted(MeetupRequest request) {
        requestsViewModel.deleteMeetupRequest(request);
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
    public void onUndoDecline(MeetupRequest request, int position) {
        requestsViewModel.undoDeclineMeetupRequest(request, position);
    }

    @Override
    public void onUndoDelete(MeetupRequest request, int position, Request.RequestState previousState) {
        requestsViewModel.undoDeleteMeetupRequest(request, position, previousState);
    }
}