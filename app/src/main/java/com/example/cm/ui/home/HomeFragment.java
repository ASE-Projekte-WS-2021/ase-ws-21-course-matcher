package com.example.cm.ui.home;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.map.MarkerClusterRenderer;
import com.example.cm.data.models.MarkerClusterItem;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.PositionManager;
import com.example.cm.databinding.FragmentHomeBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import timber.log.Timber;

public class HomeFragment extends Fragment implements OnMapReadyCallback, PositionManager.PositionListener, ClusterManager.OnClusterClickListener<MarkerClusterItem>, ClusterManager.OnClusterItemClickListener<MarkerClusterItem> {

    private ActivityResultLauncher<String> locationPermissionLauncher;
    private ClusterManager<MarkerClusterItem> clusterManager;
    private PositionManager positionManager;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private GoogleMap googleMap;
    private User currentUser;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        positionManager = PositionManager.getInstance(requireActivity());
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
        binding.mapView.onResume();

        initLocationPermissionLauncher();
        initPermissionCheck();
        initViewModel();

        return binding.getRoot();
    }

    private void initPermissionCheck() {
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;

        if (hasCoarseLocationPermission && hasFineLocationPermission) {
            positionManager.requestCurrentLocation(this);
        } else {
            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION);
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
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.DEFAULT_LOCATION, 12.5f));
        setupClusterManager(googleMap);
        // Needed to animate zoom changes for markers and cluster items correctly
        googleMap.setOnCameraIdleListener(clusterManager);

        observeFriends();
    }

    private void setupClusterManager(@NonNull GoogleMap googleMap) {
        clusterManager = new ClusterManager<>(requireActivity(), googleMap);
        clusterManager.clearItems();
        clusterManager.setRenderer(new MarkerClusterRenderer<>(requireActivity(), googleMap, clusterManager));
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
    }

    private void observeFriends() {
        homeViewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            if (friends.isEmpty()) {
                return;
            }

            for (int i = 0; i < friends.size(); i++) {
                User user = friends.get(i).getValue();
                if (user == null) {
                    continue;
                }

                LatLng location = user.getLocation();
                if (location == null) {
                    continue;
                }

                addMarker(user);
                clusterManager.cluster();
            }
        });
    }

    @Override
    public void onPositionChanged(LatLng position) {
        if (googleMap == null || currentUser == null) {
            return;
        }

        homeViewModel.updateLocation(position);
        addMarker(currentUser);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    private void addMarker(User user) {
        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
            MarkerClusterItem markerClusterItem = getDefaultMarker(user);
            clusterManager.addItem(markerClusterItem);
            clusterManager.cluster();
            return;
        }
        Glide.with(requireActivity()).load(user.getProfileImageUrl()).placeholder(R.drawable.ic_profile).apply(new RequestOptions().override(150, 150)).transform(new CircleCrop()).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                MarkerClusterItem markerClusterItem = new MarkerClusterItem(user, resource);
                clusterManager.addItem(markerClusterItem);
                clusterManager.cluster();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                MarkerClusterItem markerClusterItem = new MarkerClusterItem(user, placeholder);
                clusterManager.addItem(markerClusterItem);
                clusterManager.cluster();
            }
        });
    }

    private MarkerClusterItem getDefaultMarker(User user) {
        Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_profile);
        return new MarkerClusterItem(user, drawable);
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

    @Override
    public boolean onClusterClick(Cluster<MarkerClusterItem> cluster) {
        // TODO: Show bottom info sheet with friends information (horizontal scroll view)
        for (MarkerClusterItem item : cluster.getItems()) {
            User user = item.getUser();
            if (user == null) {
                break;
            }
            Timber.d("Clicked on cluster with user: %s", user.getFullName());
        }
        return false;
    }

    @Override
    public boolean onClusterItemClick(MarkerClusterItem item) {
        // TODO: Show bottom info sheet with friends information (single card)
        User user = item.getUser();
        if (user == null) {
            return false;
        }
        Timber.d("Clicked on cluster item with user: %s", user.getFullName());

        return false;
    }
}