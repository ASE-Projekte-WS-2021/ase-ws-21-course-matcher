package com.example.cm.ui.meetup.MeetupDetailed;

import static com.example.cm.utils.Utils.convertToAddress;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.databinding.FragmentMeetupDetailedBinding;
import com.example.cm.ui.adapters.MeetupDetailedTabAdapter;
import com.example.cm.utils.Navigator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MeetupDetailedFragment extends Fragment {

    private MeetupDetailedTabAdapter tabAdapter;
    private ViewPager2 viewPager;
    private FragmentMeetupDetailedBinding binding;
    private TabLayoutMediator tabLayoutMediator;
    private MeetupDetailedViewModel meetupDetailedViewModel;
    private Navigator navigator;

    private String meetupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meetupId = getArguments().getString(Constants.KEY_MEETUP_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupDetailedBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        initUIAndViewModel();
        initListeners();
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initUIAndViewModel() {
        meetupDetailedViewModel = new ViewModelProvider(this, new MeetupDetailedFactory(meetupId)).get(MeetupDetailedViewModel.class);
        meetupDetailedViewModel.getMeetup().observe(getViewLifecycleOwner(), meetup -> {
            tabAdapter = new MeetupDetailedTabAdapter(this, meetup);
            viewPager = binding.meetupDetailedTabPager;
            viewPager.setAdapter(tabAdapter);

            TabLayout tabLayout = binding.meetupDetailedTabLayout;

            tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                if (position == 0) {
                    tab.setText(R.string.meetup_tabs_label_accepted);
                } else if (position == 1) {
                    tab.setText(R.string.meetup_tabs_label_declined);
                } else if (position == 2) {
                    tab.setText(R.string.meetup_tabs_label_open);
                }
            });

            tabLayoutMediator.attach();

            String address = convertToAddress(requireActivity(), meetup.getLocation());
            binding.meetupDetailedLocation.setText(address);

            switch (meetup.getPhase()) {
                case MEETUP_UPCOMING:
                    binding.meetupDetailedTime.setText(meetup.getFormattedTime());
                    break;
                case MEETUP_ACTIVE:
                    binding.meetupDetailedTime.setText(getString(R.string.meetup_active_text, meetup.getFormattedTime()));
                    break;
                case MEETUP_ENDED:
                    binding.meetupDetailedTime.setText(R.string.meetup_ended_text);
                    break;
            }

            initButtons(meetup);
        });
    }

    private void initButtons(Meetup meetup) {
        if (meetup.getConfirmedFriends().contains(meetupDetailedViewModel.getCurrentUserId())) {
            binding.meetupJoinBtn.setVisibility(View.GONE);
            binding.meetupLateBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.meetupLeaveBtn.setOnClickListener(v -> onLeave());
        binding.meetupJoinBtn.setOnClickListener(v -> onJoin());
        binding.meetupLateBtn.setOnClickListener(v -> onLate());
    }

    private void onLeave() {
        meetupDetailedViewModel.onLeave();
    }

    private void onJoin() {
        meetupDetailedViewModel.onJoin();
    }

    private void onLate() {
        meetupDetailedViewModel.onLate();
    }
}