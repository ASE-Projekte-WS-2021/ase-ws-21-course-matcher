package com.example.cm.ui.meetup.MeetupLocation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MeetupLocationFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMeetupLocationBinding binding;
    private Bundle bundle;
    private GoogleMap map;

    public MeetupLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupLocationBinding.inflate(inflater, container, false);
        bundle = getArguments();

        initMap(savedInstanceState);
        initUI();
        initListeners();

        return binding.getRoot();
    }

    private void initMap(Bundle savedInstanceState) {
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
        binding.mapView.onResume();
    }

    private void initUI() {
        binding.actionBar.tvTitle.setText(getString(R.string.title_meetup_location));
    }

    private void initListeners() {
        binding.actionBar.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

        if (bundle.containsKey(Constants.KEY_MEETUP_LOCATION_LAT)) {
            double meetupLat = bundle.getDouble(Constants.KEY_MEETUP_LOCATION_LAT);
            double meetupLng = bundle.getDouble(Constants.KEY_MEETUP_LOCATION_LNG);

            LatLng meetupLatLng = new LatLng(meetupLat, meetupLng);
            setMarker(meetupLatLng, Constants.DEFAULT_MAP_ZOOM);
        }
    }

    private void setMarker(LatLng latLng, float zoomLevel) {
        map.clear();

        Marker meetupMarker = map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.create_meetup_marker_title)));
        if (meetupMarker == null) {
            return;
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }
}