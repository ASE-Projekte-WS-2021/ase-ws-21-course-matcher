package com.example.cm.utils;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cm.R;

public class Utils {

    public static void hideKeyboard(Context context, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static NavController findNavController(FragmentActivity activity) {
        return Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
    }

}
