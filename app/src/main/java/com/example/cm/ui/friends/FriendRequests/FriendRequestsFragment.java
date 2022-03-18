package com.example.cm.ui.friends.FriendRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
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
import java.util.Objects;


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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.divider_horizontal)));
        binding.notificationsRecyclerView.addItemDecoration(dividerItemDecoration);
        binding.notificationsRecyclerView.setAdapter(requestsListAdapter);
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setHasFixedSize(true);
    }

    private void initViewModel() {
        requestsViewModel = new ViewModelProvider(this).get(FriendRequestsViewModel.class);
        requestsViewModel.getFriendRequests().observe(getViewLifecycleOwner(), requests -> {
            List<String> userIds = getUserIds(requests);
            requestsViewModel.setUserIds(userIds);

            requestsViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
                requestsListAdapter.setRequests(requests, users);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(requestsListAdapter));
                itemTouchHelper.attachToRecyclerView(binding.notificationsRecyclerView);
            });
        });
    }

    private List<String> getUserIds(List<MutableLiveData<FriendRequest>> requests) {
        List<String> ids = new ArrayList<>();
        for (MutableLiveData<FriendRequest> requestLiveData : requests){
            FriendRequest request = requestLiveData.getValue();
            if(request != null){
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
