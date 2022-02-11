package com.example.cm.ui.meetup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupTabsBinding;
import com.example.cm.ui.adapters.MeetupTabAdapter;
import com.example.cm.utils.Navigator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MeetupTabsFragment extends Fragment {

    MeetupTabAdapter meetupTabAdapter;
    ViewPager2 viewPager;
    private FragmentMeetupTabsBinding binding;
    private Navigator navigator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMeetupTabsBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());

        binding.addMeetupFab.setOnClickListener(view -> {
            navigator.navigateToCreateMeetup();
        });
        return binding.getRoot();
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
