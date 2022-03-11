package com.example.cm.data.listener;

public interface UserListener<TData> {
    void onUserSuccess(TData data);
    void onUserError(Exception error);
}
