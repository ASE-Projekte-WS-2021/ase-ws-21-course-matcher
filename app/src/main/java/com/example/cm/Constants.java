package com.example.cm;

import java.util.Calendar;
import java.util.Date;

public class Constants {
    public static final String KEY_USER_ID = "keyUserId";
    public static final String KEY_MEETUP_ID = "keyMeetupId";
    public static final int MAX_CHAR_COUNT = 125;

    public static Date getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static final String FIREBASE_STORAGE_FOLDER = "profile_images/";
    public static final String PROFILE_IMAGE_EXTENSION = ".jpg";
    public static final int PROFILE_IMAGE_MAX_WIDTH = 800;
    public static final String KEY_IS_OWN_USER = "keyIsOwnUser";
    public static final int MIN_PASSWORD_LENGTH = 6;
}