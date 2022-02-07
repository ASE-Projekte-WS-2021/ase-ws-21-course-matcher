package com.example.cm.ui.meetup.MeetupDetailed;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cm.Constants;
import com.example.cm.databinding.FragmentMeetupDetailedBinding;
import com.example.cm.ui.adapters.MeetupDetailedTabAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MeetupDetailedFragment extends Fragment {

    private MeetupDetailedTabAdapter tabAdapter;
    private ViewPager2 viewPager;
    private FragmentMeetupDetailedBinding binding;
    private TabLayoutMediator tabLayoutMediator;

    private String meetupId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meetupId = getArguments().getString(Constants.KEY_MEETUP_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMeetupDetailedBinding.inflate(inflater, container, false);


        initViewModel();

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initViewModel() {
        MeetupDetailedViewModel meetupDetailedViewModel = new ViewModelProvider(this, new MeetupDetailedFactory(meetupId)).get(MeetupDetailedViewModel.class);
        meetupDetailedViewModel.getMeetup().observe(getViewLifecycleOwner(), meetup -> {
            tabAdapter = new MeetupDetailedTabAdapter(this, meetup);
            viewPager = binding.meetupDetailedTabPager;
            viewPager.setAdapter(tabAdapter);

            TabLayout tabLayout = binding.meetupDetailedTabLayout;

            tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                if (position == 0) {
                    tab.setText("Zugesagt");
                } else if (position == 1) {
                    tab.setText("Abgesagt");
                } else if (position == 2) {
                    tab.setText("Offen");
                }
            });

            tabLayoutMediator.attach();
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


    }
}