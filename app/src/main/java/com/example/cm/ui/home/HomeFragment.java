package com.example.cm.ui.home;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.cm.Constants;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.PositionManager;
import com.example.cm.databinding.FragmentHomeBinding;
import com.example.cm.utils.PicassoCircleTransform;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import timber.log.Timber;

public class HomeFragment extends Fragment implements OnMapReadyCallback, PositionManager.PositionListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private GoogleMap googleMap;
    private PositionManager positionManager;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private User currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
        binding.mapView.onResume();

        positionManager = PositionManager.getInstance(requireActivity());
        initLocationPermissionLauncher();
        initPermissionCheck();
        initViewModel();

        return binding.getRoot();
    }

    private void initPermissionCheck() {
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;

        if (hasCoarseLocationPermission && hasFineLocationPermission) {
            positionManager.requestCurrentLocation(this);
        } else {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void initLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                request -> {
                    if (request) {
                        positionManager.requestCurrentLocation(this);
                    }
                }
        );
    }

    private void initViewModel() {
        homeViewModel = new HomeViewModel();
        observeFriends();
        observeCurrentUser();
    }

    private void observeCurrentUser() {
        homeViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
            this.currentUser = currentUser;
        });
    }

    private void observeFriends() {
        homeViewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            if (googleMap == null || friends.isEmpty()) {
                return;
            }
            // Clear previously set markers
            googleMap.clear();

            for (MutableLiveData<User> friend : friends) {
                User user = friend.getValue();
                if (user == null) {
                    continue;
                }

                LatLng location = user.getLocation();
                if (location == null) {
                    continue;
                }

                setUserMarker(user, location);
            }
        });
    }

    private void setUserMarker(User user, LatLng location) {
        Picasso.get().load(user.getProfileImageUrl()).resize(150, 150).transform(new PicassoCircleTransform()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Add marker with image loaded from Picasso
                googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title(user.getFullName()));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Timber.d("Bitmap failed for user %s", user.getUsername());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Timber.d("Preparing bitmap for user %s", user.getUsername());
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.DEFAULT_LOCATION, 15));
    }

    @Override
    public void onPositionChanged(LatLng position) {
        if (googleMap == null || currentUser == null) {
            return;
        }

        Timber.d("Position changed: %s", position);

        setUserMarker(currentUser, position);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        homeViewModel.updateLocation(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding.mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}