package com.example.cm.data.listener;

public interface MeetupListener<TData> {
    void onMeetupSuccess(TData data);
    void onMeetupError(Exception error);
}
