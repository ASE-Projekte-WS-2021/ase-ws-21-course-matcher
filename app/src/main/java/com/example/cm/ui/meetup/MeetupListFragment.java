package com.example.cm.ui.meetup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupListBinding;
import com.example.cm.ui.adapters.MeetupListAdapter;
import com.example.cm.utils.Navigator;

public class MeetupListFragment extends Fragment {

    private FragmentMeetupListBinding binding;
    private MeetupListViewModel meetupListViewModel;
    private Navigator navigator;
    private MeetupListAdapter meetupListAdapter;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMeetupListBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        setHasOptionsMenu(true);

        initUi();
        initViewModel();

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initViewModel() {
        meetupListViewModel = new ViewModelProvider(this).get(MeetupListViewModel.class);
        meetupListViewModel.getLiveMeetupData().observe(getViewLifecycleOwner(), meetups -> {
            meetupListAdapter = new MeetupListAdapter(meetups);
            binding.meetupListRecyclerView.setAdapter(meetupListAdapter);
            System.out.println(meetupListAdapter.getItemCount());
        });

    }

    private void initUi() {
        binding.meetupListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.meetupListRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_meetup_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_meetup) {
            navigator.navigateToCreateMeetup();
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