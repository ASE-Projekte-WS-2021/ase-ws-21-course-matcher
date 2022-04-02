package com.example.cm;

import com.google.android.gms.maps.model.LatLng;

public class Constants {
    public static final String KEY_USER_ID = "keyUserId";
    public static final String KEY_MEETUP_ID = "keyMeetupId";
    public static final String KEY_MEETUP_LOCATION_LAT = "keyMeetupLocationLat";
    public static final String KEY_MEETUP_LOCATION_LNG = "keyMeetupLocationLng";
    public static final String KEY_CREATE_MEETUP_VM = "keyCreateMeetupViewModel";
    public static final String KEY_EMAIL = "keyEmail";
    public static final String KEY_USERNAME = "keyUsername";
    public static final String KEY_PASSWORD = "keyPassword";
    public static final int MAX_CHAR_COUNT = 125;
    public static final int SPLASH_TIMER = 000;

    public static final String TEMP_EMAIL = "course.matcher@temp.cm";
    public static final String TEMP_PASSWORD = "temporaryUser";
    public static final String ALLOWED_CHARS_FOR_USERNAME = "qwertzuiopasdfghjklyxcvbnm0123456789_-.";
    public static final int QUALITY_PROFILE_IMG = 80;
    public static final int QUALITY_MEETUP_IMG = 100;
    public static final String KEY_IS_OWN_USER = "keyIsOwnUser";
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int TRASH_ICON_MARGIN = 15;
    public static final int TRASH_ICON_SIZE = 175;
    public static final LatLng DEFAULT_LOCATION = new LatLng(48.992162698, 12.090332972);
    public static final int MARKER_PADDING = 25;
    public static final float DEFAULT_MAP_ZOOM = 10f;
    public static final int MARKER_SIZE = 150;
    public static final int MEETUP_COUNT_RADIUS = 30;
    public static final int MEETUP_COUNT_TEXT_SIZE = 30;
    public static final int MAX_PARTICIPANT_COUNT_TO_SHOW = 9;
    public static final int MAP_CARD_ANIMATION_DURATION = 250;
    public static final float INITIAL_CARD_ALPHA = 0f;
    public static final float FINAL_CARD_ALPHA = 1f;
    public static final float ON_SNAPPED_MAP_ZOOM = 15.0f;
    public static final int DEFAULT_CARD_OFFSET = 0;
    public static final int MAX_CLUSTER_ITEM_DISTANCE = 40;
    public static final int CURRENT_USER_Z_INDEX = 999;
    public static final int MEETUP_Z_INDEX = 9999;

    public static final int MEETUP_DETAILED_USER_IMAGE_SIZE = 80;
    public static final int MEETUP_DETAILED_USER_IMAGE_PADDING = 5;
    public static final int MEETUP_DETAILED_USER_IMAGE_STROKE = 6;

    // Shared Preferances Keys
    public static final String PREFS_SETTINGS_KEY = "settings";
    public static final String PREFS_SHARE_LOCATION_KEY = "shareLocation";

    // Tabbar
    public static final float PENDING_HEADER_WEIGHT = 0.7f;
    public static final float ADD_HEADER_WEIGHT = 0.5f;
    public static final float CREATE_MEETUP_ZOOM_LEVEL = 15f;
    public static final int PENDING_TAB_INDEX = 2;
    public static final int ADD_MORE_TAB_INDEX = 3;
    public static final int MAX_QUERY_LENGTH = 5;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MAX_CHARACTER_NAME = 30;

    // Firebase errors
    public static String WEAK_PASSWORD = "Dein Passwort muss mindestens 6 Zeichen lang sein.";
    public static String INVALID_CREDENTIALS = "Die Email-Adresse und das Passwort stimmen nicht überein.";
    public static String USER_COLLISION = "Es existiert bereits ein User mit dieser Email-Adresse.";
    public static final long MAX_REGISTRATION_TIME = 240000;
    public static final Throwable UNEXPECTED_USER = new Throwable("This user is not expected here.");
    public static String DEFAULT_ERROR = "Etwas ist schief gegangen.";

    // Repository Field Names
    public static final String FIELD_FRIENDS = "friends";
    public static final String FIELD_PROFILE_IMAGE_STRING = "profileImageString";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_CONFIRMED_FRIENDS = "confirmedFriends";
    public static final String FIELD_DECLINED_FRIENDS = "declinedFriends";
    public static final String FIELD_INVITED_FRIENDS = "invitedFriends";
    public static final String FIELD_LATE_FRIENDS = "lateFriends";
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String FIELD_PHASE = "phase";
    public static final String FIELD_SENDER_ID = "senderId";
    public static final String FIELD_RECEIVER_ID = "receiverId";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_MEETUP_AT = "meetupAt";
    public static final String FIELD_MEETUP_ID = "meetupId";
    public static final String FIELD_ID = "id";
    public static final String FIELD_IS_SHARING_LOCATION = "isSharingLocation";
    public static final String FIELD_AVAILABILITY = "availability";

    // Konfetti
    public static final Long KONFETTI_DURATION = 5L;
    public static final int KOFFETTI_COUNT = 50;
    public static final int KOFFETTI_SIZE = 12;
    public static final float KONFETTI_MASS = 5f;
    public static final float KONFETTI_MASS_VARIANCE = 0.2f;
    public static final int KONFETTI_ANGLE = 270;
    public static final int KONFETTI_SPREAD = 90;
    public static final float KONFEETI_MIN_SPEED = 1f;
    public static final float KONFEETI_MAX_SPEED = 5f;
    public static final Long KONFETTI_TIME_TO_LIVE = 2000L;
    public static final double KONFETTI_MIN_POSITION = 0.0;
    public static final double KONFETTI_MAX_POSITION = 1.0;

    // Meetup Cards
    public static final int OFFSET_LEFT = -3;
    public static final int OFFSET_TOP = 0;
    public static final int OFFSET_RIGHT = 0;
    public static final int OFFSET_BOTTOM = 0;
}