package com.example.cm.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.MeetupNotification;
import com.example.cm.data.models.Notification;
import com.example.cm.databinding.ItemNotificationBinding;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

    private List<Notification> mNotifications;
    private OnFriendAcceptanceListener listener;

    public NotificationListAdapter(OnFriendAcceptanceListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNotifications(List<Notification> newNotifications){
        if(mNotifications == null){
            mNotifications = newNotifications;
            notifyDataSetChanged();
            return;
        }
        mNotifications = newNotifications;
    }

    @NonNull
    @Override
    public NotificationListAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.NotificationViewHolder holder, int position) {
        Notification notification = mNotifications.get(position);

        String user = "@" + notification.getSenderName();
        boolean isAccepted = notification.getState() == Notification.NotificationState.NOTIFICATION_ACCEPTED;
        String date = notification.getCreationTimeAgo();

        int content = 0;
        switch(notification.getType()){
            case FRIEND_REQUEST:
                holder.getTvTitle().setText(R.string.friend_request_title);
                content = isAccepted ? R.string.friend_accepted_text : R.string.friend_request_text;
                break;
            case MEETUP_REQUEST:
                holder.getTvTitle().setText(notification.toString());
                content = isAccepted ? R.string.meetup_accepted_text : R.string.meetup_request_text;
                break;
            case MEETUP_ACCEPTED:
                holder.getTvTitle().setText(notification.toString());
                isAccepted = true;
                content = R.string.meetup_accepted_text;
                break;
            case MEETUP_DECLINED:
                holder.getTvTitle().setText(notification.toString());
                isAccepted = true;
                content = R.string.meetup_declined_text;
                break;
        }

        holder.getTvSender().setText(user);
        holder.getTvContent().setText(content);
        holder.getTvDate().setText(date);
        holder.getBtnAccept().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
        holder.getBtnDecline().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if(mNotifications == null){
            return 0;
        }
        return mNotifications.size();
    }

    public interface OnFriendAcceptanceListener {
        void onAccept(Notification notification);
        void onDecline(Notification notification);
        void onUndo(Notification notification, int position);
    }

    /**
     * ViewHolder class for the list items
     */
    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        private void setListeners() {
            binding.notificationAcceptButton.setOnClickListener(view -> onAccept());
            binding.notificationDeclineButton.setOnClickListener(view -> onDecline());
        }

        private void onAccept() {
            Notification notification = mNotifications.get(getAdapterPosition());
            listener.onAccept(notification);
            notifyItemChanged(getAdapterPosition());
        }

        private void onUndo(Notification notification, int position){
            listener.onUndo(notification, position);
            notifyItemInserted(position);
        }

        private void onDecline(){
            int position = getAdapterPosition();
            Notification notification = mNotifications.get(position);
            listener.onDecline(notification);
            notifyItemRemoved(position);
            Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.decline_snackbar_text, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo_snackbar_text, view -> onUndo(notification, position));
            snackbar.show();
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

        public TextView getTvSender() {
            return binding.notificationSenderTextView;
        }

        public TextView getTvDate() {
            return binding.notificationDateTextView;
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