package com.example.cm.ui.meetup.MeetupDetailed;

import static com.example.cm.utils.Utils.convertToAddress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Meetup;
import com.example.cm.databinding.FragmentMeetupDetailedBinding;
import com.example.cm.ui.adapters.MeetupDetailedTabAdapter;
import com.example.cm.utils.DeleteDialog;
import com.example.cm.utils.LogoutDialog;
import com.example.cm.utils.Navigator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MeetupDetailedFragment extends Fragment implements DeleteDialog.OnDeleteListener {

    private MeetupDetailedTabAdapter tabAdapter;
    private ViewPager2 viewPager;
    private MeetupDetailedViewModel meetupDetailedViewModel;
    private FragmentMeetupDetailedBinding binding;
    private TabLayoutMediator tabLayoutMediator;
    private Navigator navigator;
    private PopupMenu popup;
    private DeleteDialog deleteDialog;

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
        });

    }

    private void initMenu(Meetup meetup) {
        popup = new PopupMenu(requireContext(), binding.meetupContextMenuBtn);
        setForceShowIcon(popup);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_context_meetup_detailed, popup.getMenu());


        String currentUserId = meetupDetailedViewModel.getCurrentUserId();
        if (meetup.getConfirmedFriends() != null && meetup.getConfirmedFriends().contains(currentUserId)) {
            popup.getMenu().findItem(R.id.menuAccept).setVisible(false);
            popup.getMenu().findItem(R.id.menuLate).setVisible(true);
        } else if (meetup.getDeclinedFriends() != null && meetup.getDeclinedFriends().contains(currentUserId)){
            popup.getMenu().findItem(R.id.menuAccept).setVisible(true);
            popup.getMenu().findItem(R.id.menuLate).setVisible(false);
            popup.getMenu().findItem(R.id.menuDecline).setVisible(false);
        } else if (meetup.getInvitedFriends() != null && meetup.getInvitedFriends().contains(currentUserId)) {
            popup.getMenu().findItem(R.id.menuLate).setVisible(false);
        }
        if (!meetup.getRequestingUser().equals(currentUserId)) {
            popup.getMenu().findItem(R.id.menuDelete).setVisible(false);
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuMap:
                    onMap();
                    break;
                case R.id.menuAccept:
                    onJoin();
                    break;
                case R.id.menuDecline:
                    onLeave();
                    break;
                case R.id.menuLate:
                    onLate();
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
                    Class<?> popupHelper = Class.forName(menuPopupHelper.getClass().getName());
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
        binding.meetupContextMenuBtn.setOnClickListener(v -> onMenuClick());
    }

    private void onMenuClick() {
        popup.show();
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

    private void onLeave() {
        meetupDetailedViewModel.onLeave();
    }

    private void onJoin() {
        meetupDetailedViewModel.onJoin();
    }

    private void onLate() {
        meetupDetailedViewModel.onLate();
    }

    private void onAddMore() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_MEETUP_ID, meetupId);
        navigator.getNavController().navigate(R.id.action_global_navigate_to_invite_friends, bundle);
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
}