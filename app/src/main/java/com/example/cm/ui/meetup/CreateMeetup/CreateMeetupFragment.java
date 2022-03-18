package com.example.cm.ui.meetup.CreateMeetup;

import static com.example.cm.utils.Utils.convertToAddress;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.repositories.StorageManager;
import com.example.cm.databinding.FragmentMeetupBinding;
import com.example.cm.ui.settings.edit_profile.EditProfileViewModel;
import com.example.cm.ui.settings.edit_profile.EditProfileViewModelFactory;
import com.example.cm.utils.Navigator;
import com.example.cm.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class CreateMeetupFragment extends Fragment implements OnMapReadyCallback, StorageManager.Callback {

    private final Calendar calendarMeetup = Calendar.getInstance();
    private final Calendar calendarNow = Calendar.getInstance();
    int sMin, sHour;
    private ArrayAdapter<CharSequence> adapter;
    private CreateMeetupViewModel createMeetupViewModel;
    private FragmentMeetupBinding binding;
    private Navigator navigator;
    private GoogleMap map;
    private LatLng latLng;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        binding = FragmentMeetupBinding.inflate(inflater, container, false);
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
        binding.mapView.onResume();

        navigator = new Navigator(requireActivity());
        setTodaysDate();
        initUI();
        initViewModel();
        initListener();
        readBundle(bundle);

        return binding.getRoot();
    }

    private void readBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (bundle.containsKey(Constants.KEY_USER_ID)) {
            String profileId = bundle.getString(Constants.KEY_USER_ID);
            createMeetupViewModel.toggleSelectUser(profileId);
        }
    }

    private void setTodaysDate() {
        calendarNow.setTime(new Date());
        calendarMeetup.set(Calendar.DATE, calendarNow.get(Calendar.DATE));
        calendarMeetup.set(Calendar.MONTH, calendarNow.get(Calendar.MONTH));
        calendarMeetup.set(Calendar.YEAR, calendarNow.get(Calendar.YEAR));
    }

    private void initUI() {
        showCurrentTime();
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.meetup_locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.actionBar.tvTitle.setText(R.string.meetup_header);
        binding.actionBar.btnBack.bringToFront();
    }

    private void initListener() {
        binding.meetupTimeText.setOnClickListener(v -> onTimePickerDialogClicked());
        binding.meetupInfoBtn.setOnClickListener(v -> checkTime());
        binding.actionBar.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
    }

    @SuppressLint("DefaultLocale")
    private void showCurrentTime() {
        int localHour = calendarMeetup.get(Calendar.HOUR_OF_DAY);
        int localMin = calendarMeetup.get(Calendar.MINUTE);

        String formattedMin = String.format("%02d", localMin);
        String formattedHour = String.format("%02d", localHour);
        String currentTime = formattedHour + ":" + formattedMin;

        binding.meetupTimeText.setText(currentTime);
    }

    private void onTimePickerDialogClicked() {
        binding.meetupInfoBtn.setEnabled(true);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, selectedHour, selectedMinute) -> {
            sHour = selectedHour;
            sMin = selectedMinute;
            binding.meetupTimeText.setText(String.format(Locale.getDefault(), "%02d:%02d", sHour, sMin));
            calendarMeetup.set(Calendar.HOUR_OF_DAY, sHour);
            calendarMeetup.set(Calendar.MINUTE, sMin);
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, sHour, sMin, true);
        timePickerDialog.updateTime(sHour, sMin);
        timePickerDialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    private void initViewModel() {
        createMeetupViewModel = new ViewModelProvider(this, new CreateMeetupFactory(requireContext())).get(CreateMeetupViewModel.class);
        createMeetupViewModel.getMeetupIsPrivate().observe(getViewLifecycleOwner(), isPrivate -> binding.meetupPrivateCheckBox.setChecked(isPrivate));
        createMeetupViewModel.getMeetupTimestamp().observe(getViewLifecycleOwner(), timestamp -> new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(calendarMeetup.getTime()));
        createMeetupViewModel.getMeetupLatLng().observe(getViewLifecycleOwner(), latLng -> {
            if (latLng == null || map == null) {
                return;
            }
            Marker marker = map.addMarker(new MarkerOptions().position(latLng));
            if (marker == null) {
                return;
            }
            marker.setDraggable(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });
    }

    private void checkTime() {
        int localHour = calendarNow.get(Calendar.HOUR_OF_DAY);
        int localMin = calendarNow.get(Calendar.MINUTE);

        String time = binding.meetupTimeText.getText().toString();
        String[] timeArray = time.split(":");
        int selectedHour = Integer.parseInt(timeArray[0]);
        int selectedMin = Integer.parseInt(timeArray[1]);

        if (localHour > selectedHour) {
            binding.meetupInfoBtn.setEnabled(false);
            Snackbar.make(binding.getRoot(), R.string.meetup_time_in_past, Snackbar.LENGTH_LONG).show();
        } else if (localHour == selectedHour && localMin > selectedMin) {
            binding.meetupInfoBtn.setEnabled(false);
            Snackbar.make(binding.getRoot(), R.string.meetup_time_in_past, Snackbar.LENGTH_LONG).show();

        } else {
            onMeetupInfoBtnClicked();
        }
    }

    private void onMeetupInfoBtnClicked() {
        binding.meetupInfoBtn.setEnabled(false);
        binding.meetupInfoBtn.setText(R.string.meetup_info_submit_btn_loading);
        Drawable buttonDrawable = DrawableCompat.wrap(binding.meetupInfoBtn.getBackground());
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.outgreyed));
        binding.meetupInfoBtn.setBackground(buttonDrawable);

        Utils.setMapViewSize(binding.mapView.getWidth(), binding.mapView.getHeight());
        map.snapshot(bitmap -> createMeetupViewModel.setMeetupImg(bitmap, this));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //createMeetupViewModel = new ViewModelProvider(this, new CreateMeetupFactory(requireContext())).get(CreateMeetupViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        setMarker(Constants.DEFAULT_LOCATION, Constants.DEFAULT_MAP_ZOOM);

        map.setOnMapClickListener(latLng -> {
            float zoomLevel = map.getCameraPosition().zoom;
            setMarker(latLng, zoomLevel);
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg) {
            }

            @Override
            public void onMarkerDragEnd(Marker arg) {
                float zoomLevel = map.getCameraPosition().zoom;
                setMarker(arg.getPosition(), zoomLevel);
            }

            @Override
            public void onMarkerDrag(Marker arg) {
            }
        });
    }

    private void setMarker(LatLng latLng, float zoomLevel) {
        map.clear();

        Marker meetupMarker = map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.create_meetup_marker_title)));
        if (meetupMarker == null) {
            return;
        }
        meetupMarker.setDraggable(true);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        createMeetupViewModel.setMeetupLatLng(latLng);
        geocodeLatLng(latLng);
    }

    private void geocodeLatLng(LatLng latLng) {
        String address = convertToAddress(requireActivity(), latLng);
        if (!address.isEmpty()) {
            createMeetupViewModel.setMeetupLocation(address);
        } else {
            createMeetupViewModel.setMeetupLocation(getString(R.string.meetup_location_not_found));
        }
    }

    @Override
    public void onSuccess(String url) {
        setMeetupInfo(url);
        deleteLocalImg();
        navigateToInviteFriends();
    }

    private void setMeetupInfo(String url) {
        //TODO den boolean wieder benutzen wenn gefiltert werden kann
        //Boolean isPrivate = binding.meetupPrivateCheckBox.isChecked();

        Boolean isPrivate = true;

        createMeetupViewModel.setIsPrivate(isPrivate);
        createMeetupViewModel.setMeetupTimestamp(calendarMeetup.getTime());
        createMeetupViewModel.setUrl(url);
    }

    private void deleteLocalImg() {
        createMeetupViewModel.deleteLocalImg();
    }

    private void navigateToInviteFriends() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_CREATE_MEETUP_VM, createMeetupViewModel);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInviteFriends, bundle);
    }

    @Override
    public void onError(Exception e) {
        Snackbar.make(binding.getRoot(), R.string.meetup_create_error, Snackbar.LENGTH_LONG).show();
    }
}