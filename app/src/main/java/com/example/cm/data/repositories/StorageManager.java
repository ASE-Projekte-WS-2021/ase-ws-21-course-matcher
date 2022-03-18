package com.example.cm.data.repositories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.example.cm.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StorageManager extends Repository {
    private final Context context;
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public StorageManager(Context context) {
        this.context = context;
    }

    /**
     * Uploads image in form of a uri to firebase storage
     *
     * @param uri      Uri of image to upload
     * @param userId   Id of current user
     * @param callback Callback to handle success or failure
     */
    public void uploadImage(Uri uri, String userId, Callback callback, Constants.ImageType type) {
        int quality;
        String title, folder;
        Bitmap bitmap = uriToBitmap(uri);
        if (type == Constants.ImageType.PROFILE_IMAGE) {
            quality = 80;
            title = Constants.FIREBASE_STORAGE_TITLE_PROFILE_IMAGES;
            folder = Constants.FIREBASE_STORAGE_FOLDER_PROFILE_IMAGES;
            bitmap = resizeBitmap(bitmap, Constants.PROFILE_IMAGE_MAX_WIDTH);
        } else {
            quality = 100;
            title = Constants.FIREBASE_STORAGE_TITLE_MEETUP_IMAGES;
            folder = Constants.FIREBASE_STORAGE_FOLDER_MEETUP_IMAGES;

        }
        Uri resizedImageUri = bitmapToUri(bitmap, title, quality);

        StorageReference profileImageRef = storageReference.child(folder + userId + Constants.IMAGE_EXTENSION);
        profileImageRef.putFile(resizedImageUri)
                .addOnSuccessListener(task -> {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(urlToImage -> {
                        callback.onSuccess(urlToImage.toString());
                    });
                }).addOnFailureListener(e -> {
                    callback.onError(e);
                });
    }

    /**
     * Converts a given uri to a bitmap
     *
     * @param uri Uri to convert
     * @return Bitmap of the uri
     */
    private Bitmap uriToBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT > 27) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Converts a bitmap to a uri needed for uploading to firebase storage
     *
     * @param bitmap Bitmap to convert
     * @return Uri of the bitmap
     */
    private Uri bitmapToUri(Bitmap bitmap, String title, int quality) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, null);
        return Uri.parse(path);
    }

    /**
     * Resize the bitmap to the given width. Inspired by https://stackoverflow.com/a/28367226
     *
     * @param bitmap   Original bitmap
     * @param maxWidth Maximum width of bitmap
     * @return Resized bitmap
     */
    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap resizedBitmap = bitmap;

        if (width > maxWidth) {
            float initialRatio = (float) width / height;
            int newHeight = (int) ((float) maxWidth / initialRatio);
            resizedBitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, false);
        }
        return resizedBitmap;
    }

    public interface Callback {
        void onSuccess(String url);

        void onError(Exception e);
    }
}
