package com.example.cm.data.listener;

public interface RequestListener<TData> {
    void onRequestSuccess(TData data);
    void onRequestError(Exception error);
}
