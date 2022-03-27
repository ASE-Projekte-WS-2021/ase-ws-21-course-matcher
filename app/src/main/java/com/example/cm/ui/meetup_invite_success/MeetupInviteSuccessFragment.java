package com.example.cm.ui.meetup_invite_success;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupInviteSuccessBinding;
import com.example.cm.utils.Navigator;

import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;


public class MeetupInviteSuccessFragment extends Fragment {
    private FragmentMeetupInviteSuccessBinding binding;
    private Navigator navigator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupInviteSuccessBinding.inflate(inflater, container, false);
        navigator = new Navigator(requireActivity());
        initListener();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initKonefetti();
    }

    private void initKonefetti() {
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(50);
        Size sizes = new Size(12, 5f, 0.2f);
        Party party = new PartyFactory(emitterConfig)
                .angle(270)
                .spread(90)
                .setSpeedBetween(1f, 5f)
                .timeToLive(2000L)
                .sizes(sizes)
                .position(0.0, 0.0, 1.0, 0.0)
                .build();
        binding.konfettiView.start(party);
    }

    private void initListener() {
        binding.btnToMeetupList.setOnClickListener(view -> {
            navigator.getNavController().navigate(R.id.action_global_navigate_to_meetups);
        });
    }
}