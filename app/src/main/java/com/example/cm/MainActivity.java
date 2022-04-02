package com.example.cm;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cm.data.models.User;
import com.example.cm.databinding.ActivityMainBinding;
import com.example.cm.ui.auth.AuthViewModel;
import com.example.cm.utils.Utils;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initTimberLogging();
        initViewModel();
        setupBottomNavigationBar();
    }

    private void initTimberLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initViewModel() {
        AuthViewModel authViewModel = new ViewModelProvider(MainActivity.this).get(AuthViewModel.class);
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                Timber.d("Logged in with email: %s", firebaseUser.getEmail());
            }
        });
        authViewModel.getUser().observe(this, user -> {
            setProfileImage(user);
        });
    }

    private void setProfileImage(User user) {
        if (user.getProfileImageString() == null) {
            return;
        }

        Bitmap img = Utils.convertBaseStringToBitmap(user.getProfileImageString());
        Glide.with(this).load(img).placeholder(R.drawable.ic_profile).transform(new CircleCrop()).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                binding.navView.setItemIconTintList(null);
                binding.navView.getMenu().findItem(R.id.navigation_profile).setIcon(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    private void setupBottomNavigationBar() {
        binding.navView.setOnItemReselectedListener(item -> {
            // Prevents reselection of the current item and thus unwanted re-rendering of the fragment
        });
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}