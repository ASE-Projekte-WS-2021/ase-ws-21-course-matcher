package com.example.cm.ui.meetup.MeetupDetailed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupDetailedBinding;
import com.example.cm.ui.adapters.MeetupDetailedTabAdapter;
import com.example.cm.ui.adapters.MeetupTabAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MeetupDetailedFragment extends Fragment {

    private MeetupDetailedTabAdapter tabAdapter;
    private ViewPager2 viewPager;
    private FragmentMeetupDetailedBinding binding;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMeetupDetailedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tabAdapter = new MeetupDetailedTabAdapter(this);
        viewPager = view.findViewById(R.id.meetup_detailed_tab_pager);
        viewPager.setAdapter(tabAdapter);

        TabLayout tabLayout = view.findViewById(R.id.meetup_detailed_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Zugesagt");
            } else if (position == 1) {
                tab.setText("Abgesagt");
            } else if (position == 2) {
                tab.setText("Offen");
            }
        }).attach();
    }
}