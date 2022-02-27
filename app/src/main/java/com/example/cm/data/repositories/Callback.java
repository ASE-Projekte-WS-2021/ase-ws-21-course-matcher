package com.example.cm.data.repositories;

import com.google.android.gms.tasks.OnSuccessListener;

public interface Callback {
    OnSuccessListener<? super Void> onSuccess(Object object);
    void onError(Object object);
}
