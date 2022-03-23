package com.example.cm.ui.meetup.MeetupDetailed;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class MeetupDetailedFragment extends Fragment implements DeleteDialog.OnDeleteListener, OnMapReadyCallback {

    private MeetupDetailedTabAdapter tabAdapter;
    private ViewPager2 viewPager;
    private MeetupDetailedViewModel meetupDetailedViewModel;
    private FragmentMeetupDetailedBinding binding;
    private TabLayoutMediator tabLayoutMediator;
    private Navigator navigator;
    private PopupMenu popup;
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
        Bitmap img = Utils.convertBaseStringToBitmap(meetup.getLocationImageString());
        binding.ivLocation.setImageBitmap(img);
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

        ViewGroup slidingTabStrip = (ViewGroup)tabLayout.getChildAt(0);

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
        popup = new PopupMenu(requireContext(), binding.meetupContextMenuBtn);
        setForceShowIcon(popup);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_context_meetup_detailed, popup.getMenu());

        String currentUserId = meetupDetailedViewModel.getCurrentUserId();

        // current user has accepted
        if (meetup.getConfirmedFriends() != null && meetup.getConfirmedFriends().contains(currentUserId)) {
            popup.getMenu().findItem(R.id.menuAccept).setVisible(false);
            popup.getMenu().findItem(R.id.menuLeave).setVisible(true);
            popup.getMenu().findItem(R.id.menuDecline).setVisible(false);

            int titleRes = meetup.getLateFriends() != null && meetup.getLateFriends().contains(currentUserId) ? R.string.not_late : R.string.late;
            popup.getMenu().findItem(R.id.menuLate).setTitle(titleRes);
            popup.getMenu().findItem(R.id.menuLate).setVisible(true);
        }
        // current user has declined
        else if (meetup.getDeclinedFriends() != null && meetup.getDeclinedFriends().contains(currentUserId)){
            popup.getMenu().findItem(R.id.menuAccept).setVisible(true);
            popup.getMenu().findItem(R.id.menuLate).setVisible(false);
            popup.getMenu().findItem(R.id.menuDecline).setVisible(false);
        }

        // current user is creator of meetup
        if (!meetup.getRequestingUser().equals(currentUserId)) {
            popup.getMenu().findItem(R.id.menuDelete).setVisible(false);
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuAccept:
                    meetupDetailedViewModel.onJoin();
                    break;
                case R.id.menuDecline:
                    meetupDetailedViewModel.onDecline();
                    break;
                case R.id.menuLeave:
                    meetupDetailedViewModel.onLeave();
                    break;
                case R.id.menuLate:
                    meetupDetailedViewModel.onLate(item.getTitle().equals(getString(R.string.late)));
                    break;
                case R.id.menuMap:
                    onMap();
                    break;
                case R.id.menuDelete:
                    onDelete();
                    break;
            }
            return true;
        });
    }

    // https://stackoverflow.com/questions/20836385/popup-menu-with-icon-on-android
    private void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] mFields = popupMenu.getClass().getDeclaredFields();
            for (Field field : mFields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> popupHelper = Class.forName(Objects.requireNonNull(menuPopupHelper).getClass().getName());
                    Method mMethods = popupHelper.getMethod("setForceShowIcon", boolean.class);
                    mMethods.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.meetupContextMenuBtn.setOnClickListener(v -> popup.show());
        binding.ivLocation.setOnClickListener(v -> onMap());
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
        map.setOnMapClickListener(latLng -> {
            onMap();
        });
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