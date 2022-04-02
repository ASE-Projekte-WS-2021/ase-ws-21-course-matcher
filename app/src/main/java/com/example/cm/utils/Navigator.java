package com.example.cm.utils;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;

import com.example.cm.R;

public class Navigator {

    private NavController navController;

    public Navigator(FragmentActivity activity) {
        this.navController = Utils.findNavController(activity);
    }

    public void navigateToSelectFriends() {
        navController.navigate(R.id.navigateToSelectFriends);
    }

    public void navigateToCreateMeetup() {
        navController.navigate(R.id.navigateToMeetups);
    }

    public NavController getNavController() {
        return navController;
    }
}