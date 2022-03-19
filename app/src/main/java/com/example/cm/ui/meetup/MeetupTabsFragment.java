package com.example.cm.ui.meetup;

import static com.example.cm.data.models.Request.RequestState.REQUEST_PENDING;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cm.R;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.databinding.FragmentMeetupTabsBinding;
import com.example.cm.ui.adapters.MeetupTabAdapter;
import com.example.cm.ui.meetup.MeetupRequests.MeetupRequestsViewModel;
import com.example.cm.utils.Navigator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import timber.log.Timber;

public class MeetupTabsFragment extends Fragment {
    private MeetupTabAdapter meetupTabAdapter;
    private MeetupRequestsViewModel meetupRequestsViewModel;
    private ViewPager2 viewPager;
    private FragmentMeetupTabsBinding binding;
    private Navigator navigator;
    private int openRequests = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupTabsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());

        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initViewModel() {
        meetupRequestsViewModel = new ViewModelProvider(requireActivity()).get(MeetupRequestsViewModel.class);
        meetupRequestsViewModel.getMeetupRequests().observe(getViewLifecycleOwner(), meetupRequests -> {
            if (meetupRequests == null) {
                return;
            }
            countOpenRequests(meetupRequests);
            hideShowBadge();
        });
    }

    private void countOpenRequests(List<MutableLiveData<MeetupRequest>> meetupRequests) {
        // Reset the open requests counter
        openRequests = 0;

        for (int i = 0; i < meetupRequests.size(); i++) {
            MeetupRequest meetupRequest = meetupRequests.get(i).getValue();
            if (meetupRequest == null) {
                continue;
            }
            if (meetupRequest.getState() == REQUEST_PENDING) {
                openRequests++;
            }
        }
    }

    private void hideShowBadge() {
        TabLayout.Tab tab = binding.tabLayout.getTabAt(1);
        if (tab == null) {
            return;
        }
        if (openRequests > 0) {
            tab.getOrCreateBadge().setNumber(openRequests);
            tab.getOrCreateBadge().setBackgroundColor(getResources().getColor(R.color.orange500));
            tab.getOrCreateBadge().setBadgeTextColor(getResources().getColor(R.color.white));
            tab.getOrCreateBadge().setVisible(true);
        } else {
            tab.getOrCreateBadge().setVisible(false);
        }
    }

    private void initListeners() {
        binding.addMeetupFab.setOnClickListener(view -> navigator.navigateToCreateMeetup());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        meetupTabAdapter = new MeetupTabAdapter(this);
        viewPager = view.findViewById(R.id.meetup_tab_pager);
        viewPager.setAdapter(meetupTabAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.meetup_tabs_list);
            } else if (position == 1) {
                tab.setText(R.string.meetup_tabs_requests);
            }
        }).attach();
    }

}
