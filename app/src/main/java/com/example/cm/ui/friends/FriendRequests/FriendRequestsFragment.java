package com.example.cm.ui.friends.FriendRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.FragmentFriendRequestsBinding;
import com.example.cm.ui.adapters.FriendRequestListAdapter;
import com.example.cm.ui.adapters.SwipeToDelete;
import com.example.cm.utils.Navigator;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsFragment extends Fragment implements FriendRequestListAdapter.OnFriendRequestListener {

    private FriendRequestsViewModel requestsViewModel;
    private FriendRequestListAdapter requestsListAdapter;
    private FragmentFriendRequestsBinding binding;
    private Navigator navigator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendRequestsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        initUI();
        initViewModel();
        return binding.getRoot();
    }

    private void initUI() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);

        if (AppCompatResources.getDrawable(requireContext(), R.drawable.divider_horizontal) != null) {
            dividerItemDecoration.setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider_horizontal));
        }
        binding.notificationsRecyclerView.addItemDecoration(dividerItemDecoration);
        initAdapter();
    }

    private void initViewModel() {
        requestsViewModel = new ViewModelProvider(this).get(FriendRequestsViewModel.class);
        requestsViewModel.getFriendRequests().observe(getViewLifecycleOwner(), requests -> {
            List<String> userIds = getUserIds(requests);
            requestsViewModel.setUserIds(userIds);

            requestsViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
                binding.loadingCircle.setVisibility(View.GONE);
                if (users.isEmpty()) {
                    binding.tvNoRequestsFound.setVisibility(View.VISIBLE);
                    return;
                }
                binding.tvNoRequestsFound.setVisibility(View.GONE);
                binding.notificationsRecyclerView.setVisibility(View.VISIBLE);

                initAdapter();
                requestsListAdapter.setRequests(requests, users);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(requestsListAdapter));
                itemTouchHelper.attachToRecyclerView(binding.notificationsRecyclerView);
            });
        });
    }

    private void initAdapter() {
        requestsListAdapter = new FriendRequestListAdapter(this);
        binding.notificationsRecyclerView.setAdapter(requestsListAdapter);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.notificationsRecyclerView.setHasFixedSize(true);
    }

    private List<String> getUserIds(List<FriendRequest> requests) {
        List<String> ids = new ArrayList<>();
        for (FriendRequest request : requests) {
            if (request != null) {
                ids.add(request.getSenderId());
                ids.add(request.getReceiverId());
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
        bundle.putString(Constants.KEY_USER_ID, id);
        navigator.getNavController().navigate(R.id.fromFriendsToProfile, bundle);
    }

    @Override
    public void onItemDeleted(FriendRequest request) {
        requestsViewModel.declineOrDeleteFriendRequest(request);
    }

    @Override
    public void onAccept(FriendRequest request) {
        requestsViewModel.acceptFriendRequest(request);
    }

    @Override
    public void onDecline(FriendRequest request) {
        requestsViewModel.declineOrDeleteFriendRequest(request);
    }

    @Override
    public void onUndo(FriendRequest request, int position, Request.RequestState previousState) {
        requestsViewModel.undoFriendRequest(request, position, previousState);
    }
}
