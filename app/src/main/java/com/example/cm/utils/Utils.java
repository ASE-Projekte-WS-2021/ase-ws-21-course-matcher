package com.example.cm.utils;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cm.R;

import java.util.ArrayList;
import java.util.List;

public class Utils {

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
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(typeOfList.cast(o));
            }
            return result;
        }
        return null;
    }


}
