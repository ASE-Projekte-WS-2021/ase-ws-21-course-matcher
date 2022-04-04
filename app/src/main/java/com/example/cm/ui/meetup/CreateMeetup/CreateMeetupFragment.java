package com.example.cm.ui.meetup.CreateMeetup;

import static com.example.cm.utils.Utils.convertToAddress;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.databinding.FragmentCreateMeetupBinding;
import com.example.cm.utils.Navigator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CreateMeetupFragment extends Fragment implements OnMapReadyCallback {

    private final Calendar calendarMeetup = Calendar.getInstance();
    private final Calendar calendarNow = Calendar.getInstance();
    private int sMin, sHour;
    private CreateMeetupViewModel createMeetupViewModel;
    private FragmentCreateMeetupBinding binding;
    private Navigator navigator;
    private GoogleMap map;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        navigator = new Navigator(requireActivity());
        binding = FragmentCreateMeetupBinding.inflate(inflater, container, false);

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
        binding.mapView.onResume();

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
        binding.actionBar.tvTitle.setText(R.string.meetup_header);
        binding.actionBar.btnBack.bringToFront();
    }

    private void initListener() {
        binding.meetupTimeText.setOnClickListener(v -> onTimePickerDialogClicked());
        binding.meetupInfoBtn.setOnClickListener(v -> {
            if (binding.locationMeetup.getText() == null) {
                return;
            }

            String inputAddress = binding.locationMeetup.getText().toString();

            checkTime();
            if (!inputAddress.isEmpty()) {
                createMeetupViewModel.setMeetupLocation(inputAddress);
                createMeetupViewModel.setMeetupLocationName(inputAddress);
            } else {
                createMeetupViewModel.setMeetupLocation(getString(R.string.meetup_location_not_found));
            }
        });
        binding.actionBar.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
        binding.locationMeetup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void showCurrentTime() {
        int localHour = calendarMeetup.get(Calendar.HOUR_OF_DAY);
        int localMin = calendarMeetup.get(Calendar.MINUTE);

        String formattedMin = String.format("%02d", localMin);
        String formattedHour = String.format("%02d", localHour);
        String currentTime = requireContext().getString(R.string.meetup_formatted_time, formattedHour, formattedMin);

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

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), onTimeSetListener, sHour, sMin, true);
        timePickerDialog.updateTime(sHour, sMin);
        timePickerDialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    private void initViewModel() {
        createMeetupViewModel = new ViewModelProvider(this).get(CreateMeetupViewModel.class);
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
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.CREATE_MEETUP_ZOOM_LEVEL));
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
            onInviteFriendsClicked();
        }
    }

    private void onInviteFriendsClicked() {
        if (binding.locationMeetup.getText().toString().isEmpty()) {
            binding.textInputLayout.setError(getString(R.string.location_hint_empty));
            return;
        }

        map.snapshot(bitmap -> createMeetupViewModel.setMeetupImg(bitmap));
        createMeetupViewModel.setMeetupTimestamp(calendarMeetup.getTime());

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_CREATE_MEETUP_VM, createMeetupViewModel);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInviteFriends, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

        createMeetupViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            setInitialMarker(user.getLocation());
        });

        map.setOnMapClickListener(latLng -> {
            float zoomLevel = map.getCameraPosition().zoom;
            setMarker(latLng, zoomLevel);
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg) {
                // Required override
            }

            @Override
            public void onMarkerDragEnd(Marker arg) {
                float zoomLevel = map.getCameraPosition().zoom;
                setMarker(arg.getPosition(), zoomLevel);
            }

            @Override
            public void onMarkerDrag(Marker arg) {
                // Required override
            }
        });
    }

    private void setInitialMarker(LatLng currentLocation) {
        if (currentLocation != null) {
            setMarker(currentLocation, Constants.CREATE_MEETUP_ZOOM_LEVEL);
        } else {
            setMarker(Constants.DEFAULT_LOCATION, Constants.CREATE_MEETUP_ZOOM_LEVEL);
        }
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
        binding.locationMeetup.setText(address);
    }
}