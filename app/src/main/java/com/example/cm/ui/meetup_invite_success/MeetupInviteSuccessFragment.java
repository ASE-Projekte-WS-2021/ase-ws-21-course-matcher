package com.example.cm.ui.meetup_invite_success;

import static com.example.cm.Constants.KOFFETTI_COUNT;
import static com.example.cm.Constants.KOFFETTI_SIZE;
import static com.example.cm.Constants.KONFEETI_MAX_SPEED;
import static com.example.cm.Constants.KONFEETI_MIN_SPEED;
import static com.example.cm.Constants.KONFETTI_ANGLE;
import static com.example.cm.Constants.KONFETTI_DURATION;
import static com.example.cm.Constants.KONFETTI_MASS;
import static com.example.cm.Constants.KONFETTI_MASS_VARIANCE;
import static com.example.cm.Constants.KONFETTI_MAX_POSITION;
import static com.example.cm.Constants.KONFETTI_MIN_POSITION;
import static com.example.cm.Constants.KONFETTI_SPREAD;
import static com.example.cm.Constants.KONFETTI_TIME_TO_LIVE;

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

import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
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
        EmitterConfig emitterConfig = new Emitter(KONFETTI_DURATION, TimeUnit.SECONDS).perSecond(KOFFETTI_COUNT);
        Size sizes = new Size(KOFFETTI_SIZE, KONFETTI_MASS, KONFETTI_MASS_VARIANCE);
        Party party = new PartyFactory(emitterConfig)
                .angle(KONFETTI_ANGLE)
                .spread(KONFETTI_SPREAD)
                .setSpeedBetween(KONFEETI_MIN_SPEED, KONFEETI_MAX_SPEED)
                .timeToLive(KONFETTI_TIME_TO_LIVE)
                .sizes(sizes)
                .position(KONFETTI_MIN_POSITION, KONFETTI_MIN_POSITION, KONFETTI_MAX_POSITION, KONFETTI_MIN_POSITION)
                .build();

        binding.konfettiView.start(party);
    }

    private void initListener() {
        binding.btnToMeetupList.setOnClickListener(view -> {
            navigator.getNavController().navigate(R.id.action_global_navigate_to_meetups);
        });
    }
}