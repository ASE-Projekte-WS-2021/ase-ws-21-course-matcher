package com.example.cm.ui.friends.FriendRequests;

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
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.FriendRequestDTO;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.FragmentFriendRequestsBinding;
import com.example.cm.ui.adapters.FriendRequestListAdapter;
import com.example.cm.ui.adapters.SwipeToDelete;
import com.example.cm.utils.Navigator;


public class FriendRequestsFragment extends Fragment implements
        FriendRequestListAdapter.OnFriendRequestListener {

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
        requestsListAdapter = new FriendRequestListAdapter(this);
        binding.notificationsRecyclerView.setAdapter(requestsListAdapter);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setHasFixedSize(true);
    }

    private void initViewModel() {
        requestsViewModel = new ViewModelProvider(this).get(FriendRequestsViewModel.class);
        requestsViewModel.getFriendRequests().observe(getViewLifecycleOwner(), requests -> {
            requestsListAdapter.setRequests(requests);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(requestsListAdapter));
            itemTouchHelper.attachToRecyclerView(binding.notificationsRecyclerView);
        });
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
    public void onItemDeleted(int position) {
        requestsViewModel.declineOrDeleteFriendRequest(position);
    }

    @Override
    public void onAccept(int position) {
        requestsViewModel.acceptFriendRequest(position);
    }

    @Override
    public void onDecline(int position) {
        requestsViewModel.declineOrDeleteFriendRequest(position);
    }

    @Override
    public void onUndo(int position, Request.RequestState previousState) {
        requestsViewModel.undoFriendRequest(position, previousState);
    }

}
