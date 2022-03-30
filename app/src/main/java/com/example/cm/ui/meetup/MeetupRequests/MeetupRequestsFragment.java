package com.example.cm.ui.meetup.MeetupRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.FragmentMeetupRequestsBinding;
import com.example.cm.ui.adapters.MeetupRequestListAdapter;
import com.example.cm.ui.adapters.SwipeToDelete;
import com.example.cm.utils.Navigator;

import java.util.ArrayList;
import java.util.List;

public class MeetupRequestsFragment extends Fragment implements
        MeetupRequestListAdapter.OnMeetupRequestListener {

    private MeetupRequestsViewModel requestsViewModel;
    private MeetupRequestListAdapter requestsListAdapter;
    private FragmentMeetupRequestsBinding binding;
    private Navigator navigator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupRequestsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        initAdapter();
        initViewModel();
        return binding.getRoot();
    }

    private void initAdapter() {
        requestsListAdapter = new MeetupRequestListAdapter(this);
        binding.notificationsRecyclerView.setAdapter(requestsListAdapter);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setHasFixedSize(true);
    }

    private void initViewModel() {
        requestsViewModel = new ViewModelProvider(this).get(MeetupRequestsViewModel.class);
        requestsViewModel.getMeetupRequests().observe(getViewLifecycleOwner(), requests -> {
            List<String> userIds = getUserIds(requests);
            List<String> meetupIds = getMeetupIds(requests);
            requestsViewModel.setUserIds(userIds);

            requestsViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
                requestsViewModel.setMeetupIds(meetupIds);

                requestsViewModel.getMeetups().observe(getViewLifecycleOwner(), meetups -> {
                    initAdapter();
                    if (users.size() == 0) {
                        binding.loadingCircle.setVisibility(View.GONE);
                        binding.tvNoRequestsFound.setVisibility(View.VISIBLE);
                        return;
                    }
                    binding.loadingCircle.setVisibility(View.GONE);
                    binding.tvNoRequestsFound.setVisibility(View.GONE);
                    binding.notificationsRecyclerView.setVisibility(View.VISIBLE);
                    requestsListAdapter.setRequests(requests, users, meetups);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(requestsListAdapter));
                    itemTouchHelper.attachToRecyclerView(binding.notificationsRecyclerView);
                });
            });
        });
    }

    private List<String> getMeetupIds(List<MeetupRequest> requests) {
        List<String> ids = new ArrayList<>();
        for (MeetupRequest request : requests) {
            if (request != null) {
                ids.add(request.getMeetupId());
            }
        }
        return ids;
    }

    private List<String> getUserIds(List<MeetupRequest> requests) {
        List<String> ids = new ArrayList<>();
        for (MeetupRequest request : requests) {
            if (request != null) {
                ids.add(request.getSenderId());
            }
        }
        return ids;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_MEETUP_ID, id);
        navigator.getNavController().navigate(R.id.navigateToMeetupDetailed, bundle);
    }

    @Override
    public void onUsernameClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_ID, id);
        navigator.getNavController().navigate(R.id.fromMeetupRequestToProfile, bundle);
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