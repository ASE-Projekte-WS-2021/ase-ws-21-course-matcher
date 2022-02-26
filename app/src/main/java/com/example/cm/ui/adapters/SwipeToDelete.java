package com.example.cm.ui.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDelete extends ItemTouchHelper.SimpleCallback {

    private final RecyclerView.Adapter adapter;

    public SwipeToDelete(RecyclerView.Adapter adapter) {
        super(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (adapter instanceof MeetupRequestListAdapter) {
            ((MeetupRequestListAdapter)adapter).deleteItem(position);
        } else if (adapter instanceof FriendRequestListAdapter) {
            ((FriendRequestListAdapter) adapter).deleteItem(position);
        }
    }
}
