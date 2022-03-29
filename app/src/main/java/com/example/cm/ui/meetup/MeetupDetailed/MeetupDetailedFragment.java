package com.example.cm.ui.meetup.MeetupDetailed;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.databinding.FragmentMeetupDetailedBinding;
import com.example.cm.ui.adapters.MeetupDetailedTabAdapter;
import com.example.cm.utils.DeleteDialog;
import com.example.cm.utils.Navigator;
import com.example.cm.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MeetupDetailedFragment extends Fragment implements DeleteDialog.OnDeleteListener, OnMapReadyCallback {

    private MeetupDetailedTabAdapter tabAdapter;
    private ViewPager2 viewPager;
    private MeetupDetailedViewModel meetupDetailedViewModel;
    private FragmentMeetupDetailedBinding binding;
    private TabLayoutMediator tabLayoutMediator;
    private Navigator navigator;
    private DeleteDialog deleteDialog;

    private GoogleMap map;
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

            initTabbar();

            String address = meetup.getLocationName();
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
            initMenu(meetup);
            initImg(meetup);
        });
    }

    private void initImg(Meetup meetup) {
        String imgString = meetup.getLocationImageString();
        if (imgString != null && !imgString.isEmpty()) {
            Bitmap img = Utils.convertBaseStringToBitmap(meetup.getLocationImageString());
            binding.ivLocation.setImageBitmap(img);
        }
    }

    private void initTabbar() {
        TabLayout tabLayout = binding.meetupDetailedTabLayout;

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.meetup_tabs_label_accepted);
            } else if (position == 1) {
                tab.setText(R.string.meetup_tabs_label_declined);
            } else if (position == 2) {
                tab.setText(R.string.meetup_tabs_label_open);
            } else if (position == 3) {
                tab.setText(R.string.meetup_tabs_label_add);
            }
        });

        tabLayoutMediator.attach();

        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);

        View tab2 = slidingTabStrip.getChildAt(Constants.PENDING_TAB_INDEX);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) tab2.getLayoutParams();
        layoutParams2.weight = Constants.PENDING_HEADER_WEIGHT;
        tab2.setLayoutParams(layoutParams2);

        View tab3 = slidingTabStrip.getChildAt(Constants.ADD_MORE_TAB_INDEX);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) tab3.getLayoutParams();
        layoutParams3.weight = Constants.ADD_HEADER_WEIGHT;
        tab3.setLayoutParams(layoutParams3);
    }

    private void initMenu(Meetup meetup) {

        String currentUserId = meetupDetailedViewModel.getCurrentUserId();

        binding.fabMenu.setClosedOnTouchOutside(true);
        binding.fabMenu.setOnMenuButtonClickListener(v -> {
            if (!binding.fabMenu.isOpened()) {
                binding.fabBackground.setVisibility(View.VISIBLE);
                binding.fabMenu.open(true);
            } else {
                closeFabMenu();
            }
        });

        // current user has accepted
        if (meetup.getConfirmedFriends() != null && meetup.getConfirmedFriends().contains(currentUserId)) {
            binding.acceptButton.setVisibility(View.GONE);
            binding.leaveButton.setVisibility(View.INVISIBLE);
            binding.declineButton.setVisibility(View.GONE);

            int titleRes = meetup.getLateFriends() != null && meetup.getLateFriends().contains(currentUserId) ? R.string.not_late : R.string.late;
            binding.lateButton.setLabelText(getString(titleRes));
            binding.lateButton.setVisibility(View.INVISIBLE);
        }

        // current user has declined
        else if (meetup.getDeclinedFriends() != null && meetup.getDeclinedFriends().contains(currentUserId)) {
            binding.acceptButton.setVisibility(View.INVISIBLE);
            binding.lateButton.setVisibility(View.GONE);
            binding.declineButton.setVisibility(View.GONE);
        }

        // current user is creator of meetup
        if (!meetup.getRequestingUser().equals(currentUserId)) {
            binding.deleteButton.setVisibility(View.GONE);
        }
    }


    private void initListeners() {
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.ivLocation.setOnClickListener(v -> onMap());

        binding.deleteButton.setOnClickListener(v -> {
            closeFabMenu();
            onDelete();
        });

        binding.declineButton.setOnClickListener(v -> {
            closeFabMenu();
            meetupDetailedViewModel.onDecline();
        });

        binding.acceptButton.setOnClickListener(v -> {
            closeFabMenu();
            meetupDetailedViewModel.onJoin();
        });

        binding.locationButton.setOnClickListener(v -> {
            closeFabMenu();
            onMap();
        });

        binding.leaveButton.setOnClickListener(v -> {
            closeFabMenu();
            meetupDetailedViewModel.onLeave();
        });

        binding.lateButton.setOnClickListener(v -> {
            closeFabMenu();
            meetupDetailedViewModel.onLate(binding.lateButton.getLabelText().equals(getString(R.string.late)));
        });

        binding.fabBackground.setOnClickListener(v -> closeFabMenu());
    }

    private void closeFabMenu() {
        binding.fabMenu.close(true);
        binding.fabBackground.setVisibility(View.INVISIBLE);
    }

    private void onMap() {
        LatLng latLng = meetupDetailedViewModel.getMeetupLocation();
        if (latLng == null) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putDouble(Constants.KEY_MEETUP_LOCATION_LAT, latLng.latitude);
        bundle.putDouble(Constants.KEY_MEETUP_LOCATION_LNG, latLng.longitude);
        navigator.getNavController().navigate(R.id.action_navigation_meetup_detailed_to_meetupLocationFragment, bundle);
    }

    private void onDelete() {
        deleteDialog = new DeleteDialog(requireActivity(), this);
        deleteDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
    }

    @Override
    public void onDeleteApproved() {
        meetupDetailedViewModel.onDelete();
        navigator.getNavController().navigate(R.id.action_global_navigate_to_meetups);
        deleteDialog.dismiss();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        setMarker(meetupDetailedViewModel.getMeetupLocation());
        map.setOnMapClickListener(latLng -> onMap());
    }

    private void setMarker(LatLng latLng) {
        map.clear();

        Marker meetupMarker = map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.create_meetup_marker_title)));
        if (meetupMarker == null) {
            return;
        }
        meetupMarker.setDraggable(false);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_MAP_ZOOM));
    }
}