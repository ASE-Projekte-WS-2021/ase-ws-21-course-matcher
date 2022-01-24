package com.example.cm.ui.meetup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupBinding;
import com.example.cm.databinding.FragmentMeetupListBinding;
import com.example.cm.ui.dashboard.DashboardViewModel;
import com.example.cm.ui.profile.ProfileViewModel;
import com.example.cm.utils.Navigator;

import timber.log.Timber;

public class MeetupListFragment extends Fragment {

    private FragmentMeetupListBinding binding;
    private MeetupListViewModel meetupListViewModel;
    private Navigator navigator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMeetupListBinding.inflate(inflater, container, false);
        meetupListViewModel = new ViewModelProvider(this).get(MeetupListViewModel.class);
        navigator = new Navigator(requireActivity());
        setHasOptionsMenu(true);

        return binding.getRoot();
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