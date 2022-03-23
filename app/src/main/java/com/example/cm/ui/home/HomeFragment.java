package com.example.cm.ui.home;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.cm.Constants.DEFAULT_CARD_OFFSET;
import static com.example.cm.Constants.DEFAULT_LOCATION;
import static com.example.cm.Constants.DEFAULT_MAP_ZOOM;
import static com.example.cm.Constants.FINAL_CARD_ALPHA;
import static com.example.cm.Constants.INITIAL_CARD_ALPHA;
import static com.example.cm.Constants.MAP_CARD_ANIMATION_DURATION;
import static com.example.cm.Constants.MARKER_SIZE;
import static com.example.cm.Constants.MAX_CLUSTER_ITEM_DISTANCE;
import static com.example.cm.Constants.ON_SNAPPED_MAP_ZOOM;
import static com.example.cm.Constants.PREFS_SETTINGS_KEY;
import static com.example.cm.Constants.PREFS_SHARE_LOCATION_KEY;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.listener.MeetupListener;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.map.MarkerClusterRenderer;
import com.example.cm.data.map.MeetupClusterRenderer;
import com.example.cm.data.map.SnapPagerScrollListener;
import com.example.cm.data.models.MarkerClusterItem;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupClusterItem;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.PositionManager;
import com.example.cm.databinding.FragmentHomeBinding;
import com.example.cm.ui.adapters.MapUserAdapter;
import com.example.cm.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.collections.MarkerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import timber.log.Timber;

public class HomeFragment extends Fragment implements OnMapReadyCallback, PositionManager.PositionListener, MapUserAdapter.OnItemClickListener, SnapPagerScrollListener.OnChangeListener, GoogleMap.OnMapClickListener {

    private ActivityResultLauncher<String> locationPermissionLauncher;
    private ClusterManager<MarkerClusterItem> userClusterManager;
    private ClusterManager<MeetupClusterItem> meetupClusterManager;
    private PositionManager positionManager;
    private FragmentHomeBinding binding;
    private MapUserAdapter mapUserAdapter;
    private HomeViewModel homeViewModel;
    private MapView mapView;
    private GoogleMap googleMap;
    private User currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        positionManager = PositionManager.getInstance(requireActivity());
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initRecyclerView();
        initLocationPermissionLauncher();
        initPermissionCheck();
        initGoogleMap(savedInstanceState);
        initViewModel();
        initListeners();

        return binding.getRoot();
    }

    private void initListeners() {
        binding.btnCenterOnUser.setOnClickListener(v -> {
            if (currentUser != null) {
                LatLng currentPosition = currentUser.getLocation();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentPosition));
            }
        });
        binding.btnShowLocation.setOnClickListener(v -> {
            binding.btnShowLocation.setVisibility(View.GONE);
            binding.btnHideLocation.setVisibility(View.VISIBLE);
            onShareLocationClicked(false);
        });
        binding.btnHideLocation.setOnClickListener(v -> {
            binding.btnHideLocation.setVisibility(View.GONE);
            binding.btnShowLocation.setVisibility(View.VISIBLE);
            onShareLocationClicked(true);
        });
    }

    private void onShareLocationClicked(boolean isChecked) {
        currentUser.setIsSharingLocation(isChecked);
        int snackbarText = isChecked ? R.string.snackbar_share_location : R.string.snackbar_hide_location;
        Snackbar.make(binding.getRoot(), snackbarText, Snackbar.LENGTH_LONG).show();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_SETTINGS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;

        if (!hasCoarseLocationPermission || !hasFineLocationPermission) {
            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION);
            return;
        }

        homeViewModel.updateLocationSharing(isChecked, new UserListener<Boolean>() {
            @Override
            public void onUserSuccess(Boolean isChecked) {
                editor.putBoolean(PREFS_SHARE_LOCATION_KEY, isChecked);
                editor.apply();
            }

            @Override
            public void onUserError(Exception error) {
                if (error.getMessage() != null) {
                    Snackbar.make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                error.printStackTrace();
            }
        });
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e) {
            Timber.e(e);
        }

        mapView.getMapAsync(this);
    }

    private void initRecyclerView() {
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        mapUserAdapter = new MapUserAdapter(this);

        binding.rvUserCards.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvUserCards.setHasFixedSize(true);
        binding.rvUserCards.setAdapter(mapUserAdapter);
        binding.rvUserCards.addOnScrollListener(new SnapPagerScrollListener(snapHelper, SnapPagerScrollListener.ON_SCROLL, false, this));
        snapHelper.attachToRecyclerView(binding.rvUserCards);
        binding.rvUserCards.setAlpha(0f);
        binding.rvUserCards.setTranslationY(binding.rvUserCards.getHeight());
    }

    private void initPermissionCheck() {
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;

        if (hasCoarseLocationPermission && hasFineLocationPermission) {
            positionManager.requestCurrentLocation(this);
        } else if (!hasCoarseLocationPermission && !hasFineLocationPermission) {
            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION);
        } else {
            positionManager.requestCurrentLocation(this);
        }
    }

    private void initLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                request -> {
                    if (request) {
                        positionManager.requestCurrentLocation(this);
                        homeViewModel.updateLocationSharing(true);
                    }
                }
        );
    }

    private void initViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        observeCurrentUser();
    }

    private void observeCurrentUser() {
        homeViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }

            this.currentUser = currentUser;
            if (currentUser.getIsSharingLocation()) {
                binding.btnShowLocation.setVisibility(View.VISIBLE);
            } else {
                binding.btnHideLocation.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_MAP_ZOOM));
        googleMap.setOnMapClickListener(this);

        MarkerManager markerManager = new MarkerManager(googleMap);
        setupUserClusterManager(googleMap, markerManager);
        setupMeetupClusterManager(googleMap, markerManager);
        // Needed to animate zoom changes for markers and cluster items correctly
        googleMap.setOnCameraIdleListener(() -> {
            userClusterManager.onCameraIdle();
            meetupClusterManager.onCameraIdle();
        });

    }

    private void setupUserClusterManager(GoogleMap googleMap, MarkerManager markerManager) {
        userClusterManager = new ClusterManager<>(requireActivity(), googleMap, markerManager);
        userClusterManager.setRenderer(new MarkerClusterRenderer(requireActivity(), googleMap, userClusterManager));
        userClusterManager.setOnClusterClickListener(cluster -> {
            Collection<MarkerClusterItem> clusterItems = cluster.getItems();
            List<MarkerClusterItem> users = new ArrayList<>(clusterItems);
            User user = users.get(0).getUser();
            showUserCards(user);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));

            return false;
        });
        userClusterManager.setOnClusterItemClickListener(item -> {
            boolean isCurrentUser = item.isCurrentUser();

            if (!isCurrentUser) {
                showUserCards(item.getUser());
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(item.getUser().getLocation()));
            return false;
        });

        NonHierarchicalDistanceBasedAlgorithm<MarkerClusterItem> clusterAlgorithm = new NonHierarchicalDistanceBasedAlgorithm<>();
        clusterAlgorithm.setMaxDistanceBetweenClusteredItems(MAX_CLUSTER_ITEM_DISTANCE);
        userClusterManager.setAlgorithm(clusterAlgorithm);
    }

    private void setupMeetupClusterManager(GoogleMap googleMap, MarkerManager markerManager) {
        meetupClusterManager = new ClusterManager<>(requireActivity(), googleMap, markerManager);
        meetupClusterManager.setRenderer(new MeetupClusterRenderer(requireActivity(), googleMap, meetupClusterManager));
        meetupClusterManager.setOnClusterItemClickListener(item -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_MEETUP_ID, item.getMeetup().getId());
            Utils.findNavController(requireActivity()).navigate(R.id.action_navigation_home_to_navigation_meetup_detailed, bundle);
            return false;
        });
    }

    private void observeFriends() {
        homeViewModel.getFriends(new UserListener<List<User>>() {
            @Override
            public void onUserSuccess(List<User> users) {
                if (userClusterManager == null || mapUserAdapter == null) {
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    userClusterManager.clearItems();
                });

                for (User user : users) {
                    if (user.getIsSharingLocation() && user.getLocation() != null) {
                        mapUserAdapter.addUser(user);
                        addMarker(user, false);
                    }
                }
                // Set initial position of user cards offset of screen
                if (binding != null) {
                    binding.rvUserCards.animate().translationY(binding.rvUserCards.getHeight()).alpha(INITIAL_CARD_ALPHA).setDuration(MAP_CARD_ANIMATION_DURATION);
                }
            }

            @Override
            public void onUserError(Exception error) {
                Timber.e(error);
            }
        });
    }

    private void observeMeetups() {
        homeViewModel.getCurrentMeetups(new MeetupListener<List<Meetup>>() {

            @Override
            public void onMeetupSuccess(List<Meetup> meetups) {
                if (userClusterManager == null || meetups.isEmpty()) {
                    return;
                }
                for (Meetup meetup : meetups) {
                    addMeetupMarker(meetup);
                }
            }

            @Override
            public void onMeetupError(Exception error) {
                // Don't display any meetups
            }
        });
    }

    @Override
    public void onPositionChanged(LatLng position) {
        if (googleMap == null || currentUser == null) {
            return;
        }

        currentUser.setLocation(position);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_MAP_ZOOM));
        homeViewModel.updateLocation(position);
        addMarker(currentUser, true);
        userClusterManager.cluster();
    }

    private void addMeetupMarker(Meetup meetup) {
        MeetupClusterItem meetupMarker = getMeetupMarker(meetup);
        requireActivity().runOnUiThread(() -> {
            meetupClusterManager.addItem(meetupMarker);
            meetupClusterManager.cluster();
        });
    }

    private void addMarker(User user, boolean isCurrentUser) {
        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
            MarkerClusterItem markerClusterItem = getDefaultMarker(user, isCurrentUser);
            requireActivity().runOnUiThread(() -> {
                userClusterManager.addItem(markerClusterItem);
                userClusterManager.cluster();
            });
            return;
        }
        Glide.with(requireActivity()).load(user.getProfileImageUrl()).placeholder(R.drawable.ic_profile).apply(new RequestOptions().override(MARKER_SIZE, MARKER_SIZE)).transform(new CircleCrop()).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                MarkerClusterItem markerClusterItem = new MarkerClusterItem(user, resource, isCurrentUser);
                requireActivity().runOnUiThread(() -> {
                    userClusterManager.addItem(markerClusterItem);
                    userClusterManager.cluster();
                });
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                MarkerClusterItem markerClusterItem = new MarkerClusterItem(user, placeholder, isCurrentUser);
                requireActivity().runOnUiThread(() -> {
                    userClusterManager.addItem(markerClusterItem);
                    userClusterManager.cluster();
                });
            }
        });
    }

    private MeetupClusterItem getMeetupMarker(Meetup meetup) {
        return new MeetupClusterItem(meetup);
    }

    private MarkerClusterItem getDefaultMarker(User user, boolean isCurrentUser) {
        Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_profile);
        return new MarkerClusterItem(user, drawable, isCurrentUser);
    }

    @Override
    public void onItemClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_ID, id);

        Utils.findNavController(requireActivity()).navigate(R.id.action_navigation_home_to_navigation_other_profile, bundle);
    }

    @Override
    public void onMeetUserClicked(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_ID, id);
        Utils.findNavController(requireActivity()).navigate(R.id.action_navigation_home_to_navigation_meetup, bundle);
    }

    /**
     * On snap, animate to the selected user
     *
     * @param position Position in the list of users
     */
    @Override
    public void onSnapped(int position) {
        User user = mapUserAdapter.getUserAt(position);
        if (user == null) {
            return;
        }
        LatLng latLng = user.getLocation();
        if (latLng == null) {
            return;
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ON_SNAPPED_MAP_ZOOM));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showUserCards(User user) {
        String userId = user.getId();
        int position = mapUserAdapter.getPositionBy(userId);

        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        // Have to differentiate here since card does not show up correctly on first click
        if (binding.rvUserCards.getAlpha() == INITIAL_CARD_ALPHA) {
            binding.rvUserCards.scrollToPosition(position);
        } else {
            binding.rvUserCards.smoothScrollToPosition(position);
        }

        binding.rvUserCards.animate().translationY(DEFAULT_CARD_OFFSET).alpha(FINAL_CARD_ALPHA).setDuration(MAP_CARD_ANIMATION_DURATION);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // Hide user cards
        binding.rvUserCards.animate().translationY(binding.rvUserCards.getHeight()).alpha(INITIAL_CARD_ALPHA).setDuration(MAP_CARD_ANIMATION_DURATION);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
            observeFriends();
            observeMeetups();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}