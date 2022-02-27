package com.example.cm.ui.adapters;

import static com.example.cm.utils.Utils.calculateDiff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemSendFriendRequestBinding;
import com.example.cm.ui.add_friends.AddFriendsViewModel;

import java.util.List;
import java.util.Objects;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.UserViewHolder> {

    private final OnItemClickListener listener;
    private final Context context;
    private List<MutableLiveData<User>> mUsers;
    private List<MutableLiveData<FriendRequest>> sentFriendRequests;

    public AddFriendsAdapter(AddFriendsAdapter.OnItemClickListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    public void setSentFriendRequests(List<MutableLiveData<FriendRequest>> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
        listener.onFriendRequestsSet();
    }

    public void setUsers(List<MutableLiveData<User>> newUsers) {
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
        ItemSendFriendRequestBinding binding = ItemSendFriendRequestBinding
                .inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new UserViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        User user = mUsers.get(position).getValue();

        String name = Objects.requireNonNull(user).getFullName();
        String username = user.getUsername();

        holder.getTvName().setText(name);
        holder.getTvUsername().setText(username);
        holder.getFriendRequestButton().setEnabled(true);

        for (MutableLiveData<FriendRequest> request : sentFriendRequests) {
            boolean notificationExists = Objects.requireNonNull(request.getValue()).getReceiverId().equals(user.getId()) &&
                    request.getValue().getState() == Request.RequestState.REQUEST_PENDING;

            int btnContent, btnColor;
            if (!notificationExists) {
                btnContent = R.string.btn_send_friend_request_default;
                btnColor = holder.getFriendRequestButton().getContext().getResources().getColor(R.color.orange);
            } else {
                btnContent = R.string.btn_send_friend_request_pending;
                btnColor = holder.getFriendRequestButton().getContext().getResources().getColor(R.color.grey_medium);
            }
            holder.getFriendRequestButton().setText(btnContent);
            holder.getFriendRequestButton().setBackgroundColor(btnColor);
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
        void onFriendRequestButtonClicked(String receiverId);

        void onItemClicked(String id);

        void onFriendRequestsSet();
    }

    /**
     * ViewHolder class for the list items
     */
    public class UserViewHolder extends RecyclerView.ViewHolder
            implements AddFriendsViewModel.OnRequestSentListener {

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
            if (position == RecyclerView.NO_POSITION || listener == null)
                return;
            listener.onItemClicked(Objects.requireNonNull(mUsers.get(position).getValue()).getId());
        }

        @SuppressLint("ResourceAsColor")
        private void onButtonClicked() {
            binding.btnSendFriendRequest.setEnabled(false);

            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null)
                return;
            listener.onFriendRequestButtonClicked(Objects.requireNonNull(mUsers.get(position).getValue()).getId());

            int btnContent, btnColor;
            if (binding.btnSendFriendRequest.getText().toString()
                    .equals(context.getString(R.string.btn_send_friend_request_default))) {
                btnContent = R.string.btn_send_friend_request_pending;
                btnColor = binding.btnSendFriendRequest.getContext().getResources().getColor(R.color.grey_medium);
            } else {
                btnContent = R.string.btn_send_friend_request_default;
                btnColor = binding.btnSendFriendRequest.getContext().getResources().getColor(R.color.orange);
            }
            binding.btnSendFriendRequest.setText(btnContent);
            binding.btnSendFriendRequest.setBackgroundColor(btnColor);
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

        @Override
        public void onRequestAdded() {
            binding.btnSendFriendRequest.setEnabled(true);
        }

        @Override
        public void onRequestDeleted() {
            binding.btnSendFriendRequest.setEnabled(true);
        }
    }
}