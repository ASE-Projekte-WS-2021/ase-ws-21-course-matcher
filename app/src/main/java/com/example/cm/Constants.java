package com.example.cm;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

public class Constants {
    public static final String KEY_USER_ID = "keyUserId";
    public static final String KEY_MEETUP_ID = "keyMeetupId";
    public static final int MAX_CHAR_COUNT = 125;
    public static final int SPLASH_TIMER = 000;

    public static final String FIREBASE_STORAGE_FOLDER = "profile_images/";
    public static final String PROFILE_IMAGE_EXTENSION = ".jpg";
    public static final int PROFILE_IMAGE_MAX_WIDTH = 800;
    public static final String KEY_IS_OWN_USER = "keyIsOwnUser";
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int TRASH_ICON_MARGIN = 15;
    public static final int TRASH_ICON_SIZE = 175;
    public static final int HALVING_FACTOR = 2;
    public static final LatLng DEFAULT_LOCATION = new LatLng(48.992162698, 12.090332972);
    public static final int MARKER_PADDING = 25;
    public static final float DEFAULT_MAP_ZOOM = 12.5f;
    public static final int MARKER_SIZE = 150;

}