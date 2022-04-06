package com.example.cm.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Base64;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;

import com.example.cm.R;
import com.example.cm.data.models.MeetupPhase;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.cm.data.models.Request.RequestState.REQUEST_PENDING;

public class Utils {

    private static final Calendar calendarNow = GregorianCalendar.getInstance();
    private static final Calendar calendarMeetup = GregorianCalendar.getInstance();

    /**
     * Find the NavController for a given fragment
     *
     * @param activity Host activity of the fragment
     * @return NavController for the fragment
     */
    public static NavController findNavController(FragmentActivity activity) {
        NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment != null) {
            return navHostFragment.getNavController();
        }
        return null;
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
                        && Objects.equals(newUser.getDisplayName(), oldUser.getDisplayName())
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

    public static MeetupPhase getPhaseByTimestamp(Date timestamp) {
        MeetupPhase phase;
        Date now = new Date();
        calendarNow.setTime(now);
        calendarMeetup.setTime(timestamp);
        // is today?
        if (calendarNow.get(Calendar.YEAR) == calendarMeetup.get(Calendar.YEAR)
                && calendarNow.get(Calendar.MONTH) == calendarMeetup.get(Calendar.MONTH)
                && calendarNow.get(Calendar.DAY_OF_MONTH) == calendarMeetup.get(Calendar.DAY_OF_MONTH)) {
            // has started?
            if (TimeUnit.MILLISECONDS.toSeconds(now.getTime() - timestamp.getTime()) >= 0) {
                phase = MeetupPhase.MEETUP_ACTIVE;
            } else {
                phase = MeetupPhase.MEETUP_UPCOMING;
            }
        } else {
            phase = MeetupPhase.MEETUP_ENDED;
        }
        return phase;
    }

    /**
     * Returns whether user has given permission to access the location
     *
     * @param context The context the method is called from
     * @param type    The type of the permission
     * @return Whether user has given permission to access the location
     */
    public static boolean hasLocationPermission(Context context, String type) {
        if (type.equals(ACCESS_FINE_LOCATION)) {
            return ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        } else if (type.equals(ACCESS_COARSE_LOCATION)) {
            return ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;
        }
        return false;
    }

    /**
     * Convert a base64 string to a bitmap
     *
     * @param imageString The base64 string
     * @return The converted bitmap
     */
    public static Bitmap convertBaseStringToBitmap(String imageString) {
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Replaces a given string with another string
     * @param editable Editable to replace the string in
     * @param toReplace The string to replace
     * @param replacement String that replaces the toReplace string
     */
    public static void replaceIn(Editable editable, String toReplace, String replacement) {
        if (editable.toString().contains(toReplace)) {
            Editable replacedString = new SpannableStringBuilder(editable.toString().replace(toReplace, replacement));
            editable.replace(0, editable.length(), replacedString);
        }
    }
}

