package com.example.cm.ui.home;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
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
import static com.example.cm.utils.Utils.hasLocationPermission;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.example.cm.data.map.UserClusterRenderer;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.collections.MarkerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import timber.log.Timber;

public class HomeFragment extends Fragment implements OnMapReadyCallback, MapUserAdapter.OnItemClickListener, SnapPagerScrollListener.OnChangeListener {

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
        initLocationPermissionLauncher();
        initGoogleMap(savedInstanceState);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPermissionCheck();
        initViewModel();
        initListeners();
        initRecyclerView();
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

        if (!hasLocationPermission(requireActivity(), ACCESS_COARSE_LOCATION) || !hasLocationPermission(requireActivity(), ACCESS_FINE_LOCATION)) {
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
        binding.rvUserCards.bringToFront();
        binding.rvUserCards.setVisibility(View.VISIBLE);
        binding.rvUserCards.setAlpha(1f);
        binding.rvUserCards.setTranslationY(binding.rvUserCards.getHeight());
    }

    private void initPermissionCheck() {
        if (hasLocationPermission(requireActivity(), ACCESS_COARSE_LOCATION) && hasLocationPermission(requireActivity(), ACCESS_FINE_LOCATION)) {
            positionManager.requestCurrentLocation(position -> {
                onPositionChanged(position);
            });
        } else {
            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION);
        }
    }

    private void initLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                request -> {
                    if (request) {
                        positionManager.requestCurrentLocation(position -> {
                            onPositionChanged(position);
                        });
                        homeViewModel.updateLocationSharing(true);
                    }
                }
        );
    }

    private void initViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void observeCurrentUser() {
        homeViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            this.currentUser = currentUser;
            updateShareLocationButton(currentUser);
            observeFriends();
        });
    }

    private void updateShareLocationButton(User currentUser) {
        if (currentUser.getIsSharingLocation()) {
            binding.btnShowLocation.setVisibility(View.VISIBLE);
        } else {
            binding.btnHideLocation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_MAP_ZOOM));
        googleMap.setOnMapClickListener(latLng -> {
            binding.rvUserCards.animate().translationY(binding.rvUserCards.getHeight()).alpha(INITIAL_CARD_ALPHA).setDuration(MAP_CARD_ANIMATION_DURATION);
        });

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
        userClusterManager.setRenderer(new UserClusterRenderer(requireActivity(), googleMap, userClusterManager));

        userClusterManager.setOnClusterClickListener(cluster -> {
            Collection<MarkerClusterItem> clusterItems = cluster.getItems();
            List<MarkerClusterItem> users = new ArrayList<>(clusterItems);

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i).getUser();
                boolean isCurrentUser = user.getId().equals(currentUser.getId());

                if (isCurrentUser) {
                    continue;
                }
                showUserCards(user);
                break;
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));
            return true;
        });
        userClusterManager.setOnClusterItemClickListener(item -> {
            boolean isCurrentUser = item.isCurrentUser();

            if (!isCurrentUser) {
                showUserCards(item.getUser());
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(item.getUser().getLocation()));
            return true;
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
                    userClusterManager.cluster();

                    // Set initial position of user cards offset of screen
                    if (binding != null) {
                        binding.rvUserCards.animate().translationY(binding.rvUserCards.getHeight()).alpha(INITIAL_CARD_ALPHA).setDuration(MAP_CARD_ANIMATION_DURATION);
                    }
                });

                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    if (user == null) {
                        continue;
                    }

                    if (user.getIsSharingLocation() && user.getLocation() != null) {
                        mapUserAdapter.addUser(user);
                        addMarker(user, false);
                    }
                }

                if (currentUser.getLocation() != null) {
                    addMarker(currentUser, true);
                }

                requireActivity().runOnUiThread(() -> {
                    userClusterManager.cluster();
                });
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
                if (meetupClusterManager == null || meetups.isEmpty()) {
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    meetupClusterManager.clearItems();
                    meetupClusterManager.cluster();
                });

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

    private void onPositionChanged(LatLng position) {
        if (googleMap == null || currentUser == null || homeViewModel == null || binding == null) {
            return;
        }

        currentUser.setLocation(position);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_MAP_ZOOM));
        homeViewModel.updateLocation(position);
        observeCurrentUser();
    }

    private void addMeetupMarker(Meetup meetup) {
        MeetupClusterItem meetupMarker = getMeetupMarker(meetup);
        requireActivity().runOnUiThread(() -> {
            meetupClusterManager.addItem(meetupMarker);
            meetupClusterManager.cluster();
        });
    }

    private void addMarker(User user, boolean isCurrentUser) {
        if (user.getProfileImageString() == null || user.getProfileImageString().isEmpty()) {
            MarkerClusterItem markerClusterItem = getDefaultMarker(user, isCurrentUser);
            requireActivity().runOnUiThread(() -> {
                userClusterManager.addItem(markerClusterItem);
                userClusterManager.cluster();
            });
            return;
        }
        Bitmap img = Utils.convertBaseStringToBitmap(user.getProfileImageString());
        Glide.with(requireActivity()).load(img).placeholder(R.drawable.ic_profile).apply(new RequestOptions().override(MARKER_SIZE, MARKER_SIZE)).transform(new CircleCrop()).into(new CustomTarget<Drawable>() {
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
        Drawable drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_default_user_marker);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        homeViewModel.getCurrentUser().removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
            //initPermissionCheck();
            observeMeetups();
            observeCurrentUser();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        homeViewModel.resetUserList();
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