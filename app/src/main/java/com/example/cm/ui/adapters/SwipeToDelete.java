package com.example.cm.ui.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.Constants;
import com.example.cm.R;

import java.util.Objects;

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

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            Context context = recyclerView.getContext();
            ColorDrawable background = new ColorDrawable();
            Drawable icon = AppCompatResources.getDrawable(context, R.drawable.ic_trash);

            background.setColor(context.getResources().getColor(R.color.orange600));
            background.setBounds(itemView.getRight() + (int) dX,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom());

            Objects.requireNonNull(icon).setBounds(itemView.getRight() - Constants.TRASH_ICON_MARGIN - Constants.TRASH_ICON_SIZE,
                    itemView.getTop() + ((itemView.getHeight() / 2) - (Constants.TRASH_ICON_SIZE / 2)),
                    (itemView.getRight() - Constants.TRASH_ICON_MARGIN - Constants.TRASH_ICON_SIZE) + Constants.TRASH_ICON_SIZE,
                    itemView.getBottom() - ((itemView.getHeight() / 2) - (Constants.TRASH_ICON_SIZE / 2)));

            background.draw(c);
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
