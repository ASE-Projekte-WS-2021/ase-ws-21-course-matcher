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

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.map.MarkerClusterRenderer;
import com.example.cm.data.models.MarkerClusterItem;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.PositionManager;
import com.example.cm.databinding.FragmentHomeBinding;
import com.example.cm.utils.PicassoCircleTransform;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class HomeFragment extends Fragment implements OnMapReadyCallback, PositionManager.PositionListener {

    private ActivityResultLauncher<String> locationPermissionLauncher;
    private ClusterManager<MarkerClusterItem> clusterManager;
    private MarkerClusterRenderer<MarkerClusterItem> markerClusterRenderer;
    private PositionManager positionManager;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private GoogleMap googleMap;
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
            if (friends.isEmpty()) {
                return;
            }
            clusterManager.clearItems();
            clusterManager.cluster();

            for (int i = 0; i < friends.size(); i++) {
                User user = friends.get(i).getValue();
                if (user == null) {
                    continue;
                }

                LatLng location = user.getLocation();
                if (location == null) {
                    continue;
                }

                loadMarkerImage(user);
            }
        });
    }

    private void loadMarkerImage(User user) {
        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
            MarkerClusterItem marker = new MarkerClusterItem(user, R.drawable.ic_profile);
            clusterManager.addItem(marker);
            clusterManager.cluster();
        } else {
            Picasso.get().load(user.getProfileImageUrl()).resize(150, 150).centerCrop().transform(new PicassoCircleTransform()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    MarkerClusterItem marker = new MarkerClusterItem(user, bitmap);
                    clusterManager.addItem(marker);
                    clusterManager.cluster();
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.DEFAULT_LOCATION, 12.5f));
        clusterManager = new ClusterManager<>(requireActivity(), googleMap);
        markerClusterRenderer = new MarkerClusterRenderer<>(requireActivity(), googleMap, clusterManager);
        clusterManager.setRenderer(markerClusterRenderer);

        // Needed to animate zoom changes for markers and cluster items correctly
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        observeFriends();
    }

    @Override
    public void onPositionChanged(LatLng position) {
        if (googleMap == null || currentUser == null) {
            return;
        }

        homeViewModel.updateLocation(position);
        loadMarkerImage(currentUser);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12.5f));
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