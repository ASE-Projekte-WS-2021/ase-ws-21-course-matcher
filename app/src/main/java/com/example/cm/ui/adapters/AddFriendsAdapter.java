package com.example.cm.ui.adapters;

import static com.example.cm.utils.Utils.calculateDiff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.Notification;
import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemSendFriendRequestBinding;

import java.util.List;

import timber.log.Timber;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.UserViewHolder> {

    private final OnItemClickListener listener;
    private final Context context;
    private List<User> users;
    private List<Notification> sentFriendRequests;
    private boolean isFriendRequestLoading = false;


    public AddFriendsAdapter(AddFriendsAdapter.OnItemClickListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    public void setSentFriendRequests(List<Notification> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
        isFriendRequestLoading = false;
        listener.onFriendRequestsSet();
    }

    public void setUsers(List<User> newUsers) {
        if (users == null) {
            users = newUsers;
            notifyItemRangeInserted(0, newUsers.size());
            return;
        }

        DiffUtil.DiffResult result = calculateDiff(users, newUsers);
        users = newUsers;
        result.dispatchUpdatesTo(this);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        ItemSendFriendRequestBinding binding = ItemSendFriendRequestBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new UserViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        String name = users.get(position).getFullName();
        String username = users.get(position).getUsername();

        holder.getTvName().setText(name);
        holder.getTvUsername().setText(username);
        holder.getFriendRequestButton().setEnabled(!isFriendRequestLoading);

        for (Notification notification : sentFriendRequests) {
            boolean notificationExists = notification.getReceiverId().equals(users.get(position).getId());

            if (!notificationExists) {
                holder.getFriendRequestButton().setText(R.string.btn_send_friend_request_default);
                holder.getFriendRequestButton().setBackgroundColor(R.color.orange);
            } else {
                holder.getFriendRequestButton().setText(R.string.btn_send_friend_request_pending);
                holder.getFriendRequestButton().setBackgroundColor(R.color.grey_medium);
            }
        }
    }

    // Return the size of the list
    @Override
    public int getItemCount() {
        if (users == null) {
            return 0;
        }
        return users.size();
    }

    public interface OnItemClickListener {
        void onFriendRequestButtonClicked(String receiverId);

        void onItemClicked(String id);

        void onFriendRequestsSet();
    }


    /**
     * ViewHolder class for the list items
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemSendFriendRequestBinding binding;

        public UserViewHolder(ItemSendFriendRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        /**
         * Listeners for the views in the list item
         */
        private void setListeners() {
            binding.getRoot().setOnClickListener(v -> onItemClicked());
            binding.btnSendFriendRequest.setOnClickListener(v -> onButtonClicked());
        }

        private void onItemClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;
            listener.onItemClicked(users.get(position).getId());
        }

        @SuppressLint("ResourceAsColor")
        private void onButtonClicked() {
            isFriendRequestLoading = true;

            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;
            listener.onFriendRequestButtonClicked(users.get(position).getId());

            if (binding.btnSendFriendRequest.getText().toString().equals(context.getString(R.string.btn_send_friend_request_default))) {
                binding.btnSendFriendRequest.setText(R.string.btn_send_friend_request_pending);
                binding.btnSendFriendRequest.setBackgroundColor(R.color.orange);
            } else {
                binding.btnSendFriendRequest.setText(R.string.btn_send_friend_request_default);
                binding.btnSendFriendRequest.setBackgroundColor(R.color.grey_medium);
            }
        }

        /**
         * Getters for the views in the list item
         */
        public TextView getTvName() {
            return binding.tvName;
        }

        public TextView getTvUsername() {
            return binding.tvUsername;
        }

        public Button getFriendRequestButton() {
            return binding.btnSendFriendRequest;
        }
    }
}