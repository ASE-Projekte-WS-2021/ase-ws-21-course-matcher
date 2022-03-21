package com.example.cm.utils;

import static com.example.cm.data.models.Request.RequestState.REQUEST_PENDING;

import android.content.Context;
import android.content.res.Resources;
import android.location.Geocoder;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;

import com.example.cm.R;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Utils {

    private static int mapViewWidth, mapViewHeight;

    /**
     * Hides the keyboard
     *
     * @param context Context of the activity
     * @param view    View to hide the keyboard from
     */
    public static void hideKeyboard(Context context, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Find the NavController for a given fragment
     *
     * @param activity Host activity of the fragment
     * @return NavController for the fragment
     */
    public static NavController findNavController(FragmentActivity activity) {
        return Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
    }

    /**
     * Cast a list of objects to a list of a specific type
     * Resolves unchecked cast warning
     * From: https://debugah.com/convert-object-to-list-avoiding-unchecked-cast-java-lang-object-to-java-util-list-2888/
     *
     * @param obj        List of objects
     * @param typeOfList Type to which the list should be casted
     * @param <T>        Type of the list
     * @return List of type T
     */
    public static <T> List<T> castList(Object obj, Class<T> typeOfList) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(typeOfList.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * Calculate the difference between two lists and return the result
     * Also used to animate the changes
     * From https://stackoverflow.com/questions/49588377/how-to-set-adapter-in-mvvm-using-databinding
     *
     * @param oldUsers The old list of mutable users
     * @param newUsers The new list of mutable users
     * @return The result of the calculation
     */
    public static DiffUtil.DiffResult calculateDiff(List<User> oldUsers, List<User> newUsers) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldUsers.size();
            }

            @Override
            public int getNewListSize() {
                return newUsers.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(Objects.requireNonNull(oldUsers.get(oldItemPosition)).getId(),
                        Objects.requireNonNull(newUsers.get(newItemPosition)).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                User newUser = newUsers.get(newItemPosition);
                User oldUser = oldUsers.get(oldItemPosition);

                return Objects.equals(Objects.requireNonNull(newUser).getId(), Objects.requireNonNull(oldUser).getId())
                        && Objects.equals(newUser.getFirstName(), oldUser.getFirstName())
                        && Objects.equals(newUser.getLastName(), oldUser.getLastName())
                        && Objects.equals(newUser.getUsername(), oldUser.getUsername());
            }
        });
    }

    /**
     * Convert a latitude and longitude to a human readable address
     *
     * @param context Context the method is called from
     * @param latLng  Latitude and longitude
     * @return Human readable address
     */
    public static String convertToAddress(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.GERMANY);
        String address = "";
        try {
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    /**
     * Returns the current date
     *
     * @return The current date
     */
    public static Date getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    /**
     * Displays a badge in the Requests tab when open requests are available
     *
     * @param tab          The tab to display the badge in
     * @param openRequests The number of open requests
     * @param resources    The resources to get the badge color from
     */
    public static void hideShowBadge(TabLayout.Tab tab, int openRequests, Resources resources) {
        if (tab == null) {
            return;
        }
        if (openRequests > 0) {
            tab.getOrCreateBadge().setNumber(openRequests);
            tab.getOrCreateBadge().setBackgroundColor(resources.getColor(R.color.orange500));
            tab.getOrCreateBadge().setBadgeTextColor(resources.getColor(R.color.white));
            tab.getOrCreateBadge().setVisible(true);
        } else {
            tab.getOrCreateBadge().setVisible(false);
        }
    }

    /**
     * Returns the amount of open requests
     *
     * @param requests The list of requests
     * @param <T>      The type of the requests
     * @return The amount of open requests
     */
    public static <T extends Request> int getOpenRequestCount(List<T> requests) {
        int openRequests = 0;

        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            if (request == null) {
                continue;
            }
            if (request.getState() == REQUEST_PENDING) {
                openRequests++;
            }
        }
        return openRequests;
    }
}
