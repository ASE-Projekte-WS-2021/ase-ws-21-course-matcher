package com.example.cm.utils;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;

import com.example.cm.R;

public class Navigator {

    NavController navController;
    //TODO hier alle navigator rein damit übersichtlicher & einheitlicher

    public Navigator(FragmentActivity activity) {
        this.navController = Utils.findNavController(activity);
    }

    public void navigateToSelectFriends() {
        navController.navigate(R.id.navigateToSelectFriends);
    }

    public void navigateToFriends() {
        navController.navigate(R.id.navigateToFriends);
    }

    public void globalNavigateToProfile() {
        navController.navigate(R.id.fromFriendsToProfile);
    }

    public NavController getNavController() {
        return navController;
    }
}