package com.example.cm.ui.settings.edit_profile;

import static com.example.cm.Constants.FIELD_BIO;
import static com.example.cm.Constants.FIELD_DISPLAY_NAME;
import static com.example.cm.Constants.FIELD_USERNAME;
import static com.example.cm.Constants.LABEL_BIO;
import static com.example.cm.Constants.LABEL_DISPLAY_NAME;
import static com.example.cm.Constants.LABEL_USERNAME;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.listener.Callback;
import com.example.cm.data.models.Status;
import com.example.cm.data.models.StatusFlag;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.utils.InputValidator;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class EditProfileViewModel extends ViewModel implements Callback {
    private final UserRepository userRepository;
    public MutableLiveData<Status> status = new MutableLiveData<>();
    private MutableLiveData<User> user;

    public EditProfileViewModel() {
        userRepository = new UserRepository();
        user = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void updateImage(Uri uri, Context context) throws FileNotFoundException {
        if (user.getValue() == null) {
            return;
        }

        InputStream imageStream = context.getContentResolver().openInputStream(uri);
        Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, Constants.QUALITY_PROFILE_IMG, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String imageBaseString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        User usr = user.getValue();
        if (usr != null) {
            userRepository.updateProfileImage(imageBaseString, usr.getId());
        }
    }

    public void updateField(Context context, String field, String value) {
        if (value.trim().isEmpty() && !field.equals(context.getString(R.string.input_label_bio))) {
            status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_field_not_empty));
            return;
        }

        String trimmedValue = value.trim();

        switch (field) {
            case LABEL_USERNAME:
                if (!InputValidator.hasMinLength(trimmedValue, Constants.MIN_USERNAME_LENGTH)) {
                    status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_username_min_length));
                    break;
                }
                userRepository.updateField(FIELD_USERNAME, trimmedValue, this);
                break;
            case LABEL_DISPLAY_NAME:
                if (!InputValidator.hasMinLength(trimmedValue, Constants.MIN_NAME_LENGTH)) {
                    status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_display_name_min_length));
                    break;
                }
                userRepository.updateField(FIELD_DISPLAY_NAME, trimmedValue, this);
                break;
            case LABEL_BIO:
                userRepository.updateField(FIELD_BIO, trimmedValue, this);
                break;
            default:
                break;
        }
    }

    public void getUsernames(UserRepository.UsernamesRetrievedCallback callback) {
        userRepository.getUsernames(callback);
    }

    @Override
    public void onSuccess(Object object) {
        status.postValue(new Status(StatusFlag.SUCCESS, R.string.edit_profile_success));
        user = userRepository.getCurrentUser();
    }

    @Override
    public void onError(Object object) {
        status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_general_error));
    }
}
