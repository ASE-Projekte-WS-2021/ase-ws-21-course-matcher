package com.example.cm.ui.adapters;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.Constants;
import com.example.cm.R;

public class SwipeToDelete extends ItemTouchHelper.SimpleCallback {

    private final RecyclerView.Adapter adapter;

    public SwipeToDelete(RecyclerView.Adapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            ColorDrawable background = new ColorDrawable();
            background.setColor(recyclerView.getContext().getResources().getColor(R.color.orange600));
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                    itemView.getRight(), itemView.getBottom());
            background.draw(c);

            Drawable icon = recyclerView.getContext().getDrawable(R.drawable.ic_delete_item);
            icon.setBounds(itemView.getRight() - Constants.TRASH_ICON_MARGIN - Constants.TRASH_ICON_SIZE,
                    itemView.getTop() + ((itemView.getHeight() / 2) - (Constants.TRASH_ICON_SIZE / 2)),
                    (itemView.getRight() - Constants.TRASH_ICON_MARGIN - Constants.TRASH_ICON_SIZE) + Constants.TRASH_ICON_SIZE,
                    itemView.getBottom() - ((itemView.getHeight() / 2) - (Constants.TRASH_ICON_SIZE / 2)));
            icon.draw(c);
        }
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
