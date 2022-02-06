package com.example.cm.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.Request;
import com.example.cm.databinding.ItemNotificationBinding;
import com.google.android.material.snackbar.Snackbar;


import java.util.List;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.NotificationViewHolder> {

    protected List<Request> mRequests;
    protected OnRequestAcceptanceListener listener;

    public RequestListAdapter(OnRequestAcceptanceListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNotifications(List<Request> newRequests){
        if(mRequests == null){
            mRequests = newRequests;
            notifyDataSetChanged();
            return;
        }
        mRequests = newRequests;
    }

    @NonNull
    @Override
    public RequestListAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RequestListAdapter.NotificationViewHolder holder, int position) {
        Request request = mRequests.get(position);

        String user = "@" + request.getSenderName();
        String date = request.getCreationTimeAgo();

        holder.getTvSender().setText(user);
        holder.getTvDate().setText(date);
    }

    @Override
    public int getItemCount() {
        if(mRequests == null){
            return 0;
        }
        return mRequests.size();
    }

    public interface OnRequestAcceptanceListener {
        void onAccept(Request request);
        void onDecline(Request request);
        void onUndo(Request request, int position);
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
            Request request = mRequests.get(getAdapterPosition());
            listener.onAccept(request);
            notifyItemChanged(getAdapterPosition());
        }

        private void onUndo(Request request, int position){
            listener.onUndo(request, position);
            notifyItemInserted(position);
        }

        private void onDecline(){
            int position = getAdapterPosition();
            Request request = mRequests.get(position);
            listener.onDecline(request);
            notifyItemRemoved(position);
            Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.decline_snackbar_text, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo_snackbar_text, view -> onUndo(request, position));
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
