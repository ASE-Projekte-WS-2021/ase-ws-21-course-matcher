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
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.map.MarkerClusterRenderer;
import com.example.cm.data.map.SnapPagerScrollListener;
import com.example.cm.data.models.MarkerClusterItem;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.PositionManager;
import com.example.cm.databinding.FragmentHomeBinding;
import com.example.cm.ui.adapters.MapUserAdapter;
import com.example.cm.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import timber.log.Timber;

public class HomeFragment extends Fragment implements OnMapReadyCallback, PositionManager.PositionListener, MapUserAdapter.OnItemClickListener, SnapPagerScrollListener.OnChangeListener, ClusterManager.OnClusterItemClickListener<MarkerClusterItem>, ClusterManager.OnClusterClickListener<MarkerClusterItem>, GoogleMap.OnMapClickListener {

    private ActivityResultLauncher<String> locationPermissionLauncher;
    private ClusterManager<MarkerClusterItem> clusterManager;
    private PositionManager positionManager;
    private FragmentHomeBinding binding;
    private MapUserAdapter mapUserAdapter;
    private HomeViewModel homeViewModel;
    private GoogleMap googleMap;
    private User currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        positionManager = PositionManager.getInstance(requireActivity());
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initRecyclerView();
        initLocationPermissionLauncher();
        initPermissionCheck();
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
    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initRecyclerView() {
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        mapUserAdapter = new MapUserAdapter(this);

        binding.rvUserCards.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvUserCards.setHasFixedSize(true);
        binding.rvUserCards.setAdapter(mapUserAdapter);
        binding.rvUserCards.addOnScrollListener(new SnapPagerScrollListener(snapHelper, SnapPagerScrollListener.ON_SCROLL, false, this));
        snapHelper.attachToRecyclerView(binding.rvUserCards);
    }

    private void initPermissionCheck() {
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(requireActivity(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;

        if (hasCoarseLocationPermission && hasFineLocationPermission) {
            positionManager.requestCurrentLocation(this);
            initGoogleMap();
        } else if (!hasCoarseLocationPermission && !hasFineLocationPermission) {
            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION);
        } else {
            positionManager.requestCurrentLocation(this);
            initGoogleMap();
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
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_MAP_ZOOM));
        googleMap.setOnMapClickListener(this);
        setupClusterManager(googleMap);
        // Needed to animate zoom changes for markers and cluster items correctly
        googleMap.setOnCameraIdleListener(clusterManager);
    }

    private void setupClusterManager(GoogleMap googleMap) {
        clusterManager = new ClusterManager<>(requireActivity(), googleMap);
        clusterManager.setRenderer(new MarkerClusterRenderer<>(requireActivity(), googleMap, clusterManager));
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);

        NonHierarchicalDistanceBasedAlgorithm<MarkerClusterItem> clusterAlgorithm = new NonHierarchicalDistanceBasedAlgorithm<>();
        clusterAlgorithm.setMaxDistanceBetweenClusteredItems(MAX_CLUSTER_ITEM_DISTANCE);
        clusterManager.setAlgorithm(clusterAlgorithm);
        observeCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        observeFriends();
    }

    private void observeFriends() {
        homeViewModel.getFriends(new UserListener<List<User>>() {
            @Override
            public void onUserSuccess(List<User> users) {
                if (clusterManager == null || mapUserAdapter == null) {
                    return;
                }

                clusterManager.clearItems();
                for (User user : users) {
                    if (user.getLocation() != null) {
                        mapUserAdapter.addUser(user);
                        requireActivity().runOnUiThread(() -> {
                            addMarker(user, true);
                            clusterManager.cluster();
                        });
                    }
                }
                // Set initial position of user cards offset of screen
                binding.rvUserCards.animate().translationY(binding.rvUserCards.getHeight()).alpha(INITIAL_CARD_ALPHA).setDuration(MAP_CARD_ANIMATION_DURATION);
            }

            @Override
            public void onUserError(Exception error) {
                Timber.e(error);
            }
        });
    }


    @Override
    public void onPositionChanged(LatLng position) {
        if (googleMap == null || currentUser == null || clusterManager == null) {
            return;
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_MAP_ZOOM));
        homeViewModel.updateLocation(position);
        requireActivity().runOnUiThread(() -> {
            addMarker(currentUser, true);
            clusterManager.cluster();
        });
    }

    private void addMarker(User user, boolean isCurrentUser) {
        if (clusterManager == null) {
            return;
        }
        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
            MarkerClusterItem markerClusterItem = getDefaultMarker(user, isCurrentUser);
            clusterManager.addItem(markerClusterItem);
            clusterManager.cluster();
            return;
        }
        Glide.with(requireActivity()).load(user.getProfileImageUrl()).placeholder(R.drawable.ic_profile).apply(new RequestOptions().override(MARKER_SIZE, MARKER_SIZE)).transform(new CircleCrop()).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                MarkerClusterItem markerClusterItem = new MarkerClusterItem(user, resource, isCurrentUser);
                clusterManager.addItem(markerClusterItem);
                clusterManager.cluster();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                MarkerClusterItem markerClusterItem = new MarkerClusterItem(user, placeholder, isCurrentUser);
                clusterManager.addItem(markerClusterItem);
                clusterManager.cluster();
            }
        });
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

    @Override
    public boolean onClusterItemClick(MarkerClusterItem item) {
        boolean isCurrentUser = item.isCurrentUser();

        if (!isCurrentUser) {
            showUserCards(item.getUser());
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(item.getUser().getLocation()));
        return false;
    }

    @Override
    public boolean onClusterClick(Cluster<MarkerClusterItem> cluster) {
        Collection<MarkerClusterItem> clusterItems = cluster.getItems();
        List<MarkerClusterItem> users = new ArrayList<>(clusterItems);
        User user = users.get(0).getUser();
        showUserCards(user);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));

        return false;
    }

    private void showUserCards(User user) {
        binding.rvUserCards.animate().translationY(DEFAULT_CARD_OFFSET).alpha(FINAL_CARD_ALPHA).setDuration(MAP_CARD_ANIMATION_DURATION);
        String userId = user.getId();
        int position = mapUserAdapter.getPositionBy(userId);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        binding.rvUserCards.smoothScrollToPosition(position);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // Hide user cards
        binding.rvUserCards.animate().translationY(binding.rvUserCards.getHeight()).alpha(INITIAL_CARD_ALPHA).setDuration(MAP_CARD_ANIMATION_DURATION);
    }
}