package com.example.cm.ui.adapters;

import static com.example.cm.utils.Utils.calculateDiff;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class SelectFriendsAdapter extends RecyclerView.Adapter<SelectFriendsAdapter.UserViewHolder> {

    private final OnItemClickListener listener;
    private List<User> mUsers;
    private List<Notification> sentFriendRequests;


    public SelectFriendsAdapter(SelectFriendsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSentFriendRequests(List<Notification> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
        listener.onFriendRequestsSet();
    }

    public void setUsers(List<User> newUsers) {
        if (mUsers == null) {
            mUsers = newUsers;
            notifyItemRangeInserted(0, newUsers.size());
            return;
        }

        DiffUtil.DiffResult result = calculateDiff(mUsers, newUsers);
        mUsers = newUsers;
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
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        String name = mUsers.get(position).getFullName();
        String username = mUsers.get(position).getUsername();

        holder.getTvName().setText(name);
        holder.getTvUsername().setText(username);

        // Check whether a notification has been sent to this user
        if (sentFriendRequests == null) {
            return;
        }
        for (Notification notification : sentFriendRequests) {
            if (notification.getReceiverId().equals(mUsers.get(position).getId())) {
                holder.getFriendRequestButton().setText(R.string.btn_send_friend_request_pending);
                holder.getFriendRequestButton().setBackgroundColor(Color.parseColor("#FFD3D3D3"));
                break;
            }
        }
    }

    // Return the size of the list
    @Override
    public int getItemCount() {
        if (mUsers == null) {
            return 0;
        }
        return mUsers.size();
    }

    public interface OnItemClickListener {
        void onFriendRequestButtonClicked(String receiverId, int position);

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
            listener.onItemClicked(mUsers.get(position).getId());
        }

        private void onButtonClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;
            listener.onFriendRequestButtonClicked(mUsers.get(position).getId(), position);
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