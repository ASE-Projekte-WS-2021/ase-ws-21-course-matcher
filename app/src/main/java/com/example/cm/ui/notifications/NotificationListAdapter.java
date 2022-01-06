package com.example.cm.ui.notifications;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.Notification;
import com.example.cm.databinding.ItemSingleNotificationBinding;

import java.util.List;
import java.util.Objects;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

    private List<Notification> mNotifications;

    public void setNotifications(List<Notification> newNotifications){
        if(mNotifications == null){
            mNotifications = newNotifications;
            notifyDataSetChanged();
            return;
        }
        mNotifications = newNotifications;
    }


    private void onAcceptClick(Notification notification){
        //todo: notification.accept();
        //notification.setContent((String) context.getText(R.string.meetup_accepted_text));
        //notifyDataSetChanged();
    }

    private void onDeclineClick(Notification notification){
        //todo: notification.decline();
        mNotifications.remove(notification);
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationListAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSingleNotificationBinding binding = ItemSingleNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.NotificationViewHolder holder, int position) {
        String title = mNotifications.get(position).getTitle();
        String content = mNotifications.get(position).getContent();

        holder.getTvTitle().setText(title);
        holder.getTvContent().setText(content);

        holder.getBtnAccept().setOnClickListener(view -> onAcceptClick(mNotifications.get(position)));
        holder.getBtnDecline().setOnClickListener(view -> onDeclineClick(mNotifications.get(position)));
    }

    @Override
    public int getItemCount() {
        if(mNotifications == null){
            return 0;
        }
        return mNotifications.size();
    }

    /**
     * ViewHolder class for the list items
     */
    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final ItemSingleNotificationBinding binding;

        public NotificationViewHolder(ItemSingleNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Getters for the views in the list item
         */
        public TextView getTvTitle() {
            return binding.notificationTitleTextView;
        }

        public TextView getTvContent() {
            return binding.notificationContentTextView;
        }

        public ImageView getIvProfilePicture(){
            return binding.notificationImageView;
        }

        public Button getBtnAccept(){
            return binding.notificationAcceptButton;
        }

        public Button getBtnDecline(){
            return binding.notificationDeclineButton;
        }
    }
}
