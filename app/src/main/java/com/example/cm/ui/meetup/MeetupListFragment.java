package com.example.cm.ui.meetup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.cm.R;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.databinding.FragmentMeetupBinding;
import com.example.cm.databinding.FragmentMeetupListBinding;
import com.example.cm.ui.adapters.MeetupListAdapter;
import com.example.cm.ui.dashboard.DashboardViewModel;
import com.example.cm.ui.profile.ProfileViewModel;
import com.example.cm.utils.Navigator;

import timber.log.Timber;

public class MeetupListFragment extends Fragment implements MeetupListAdapter.OnMeetupClickedListener {

    private FragmentMeetupListBinding binding;
    private MeetupListViewModel meetupListViewModel;
    private Navigator navigator;
    private MeetupListAdapter meetupListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMeetupListBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        setHasOptionsMenu(true);
        initUi();
        initViewModel();

        return binding.getRoot();
    }

    private void initViewModel() {
        meetupListViewModel = new ViewModelProvider(this).get(MeetupListViewModel.class);
        meetupListViewModel.getMeetups().observe(getViewLifecycleOwner(), meetups -> {
            if(meetups == null){
                return;
            }
            meetupListAdapter.setMeetups(meetups);
        });

    }

    private void initUi() {
        meetupListAdapter = new MeetupListAdapter(this);
        binding.meetupListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.meetupListRecyclerView.setHasFixedSize(true);
        binding.meetupListRecyclerView.setAdapter(meetupListAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_meetup_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_meetup) {
            navigator.navigateToInviteFriends();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}