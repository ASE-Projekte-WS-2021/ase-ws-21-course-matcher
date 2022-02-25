package com.example.cm.data.repositories;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageRepository extends Repository {
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public void uploadProfileImage(Uri uri, String userId, Callback callback) {
        StorageReference profileImageRef = storageReference.child("profile_images/" + userId + ".jpg");
        profileImageRef.putFile(uri).addOnSuccessListener(task -> {
            profileImageRef.getDownloadUrl().addOnSuccessListener(urlToImage -> {
                callback.onSuccess(urlToImage.toString());
            });
        }).addOnFailureListener(e -> {
            callback.onError(e);
        });
    }

    public interface Callback {
        void onSuccess(String url);

        void onError(Exception e);
    }
}
