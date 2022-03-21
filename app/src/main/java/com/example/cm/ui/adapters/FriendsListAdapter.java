package com.example.cm.ui.adapters;

import static com.example.cm.utils.Utils.calculateDiff;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemSingleFriendBinding;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.UserViewHolder> {

    private final OnItemClickListener listener;
    private List<User> mUsers;

    public FriendsListAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setFriends(List<User> newUsers) {
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
        ItemSingleFriendBinding binding = ItemSingleFriendBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new UserViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        User user = mUsers.get(position);

        String profileImageUrl = Objects.requireNonNull(user).getProfileImageUrl();
        String name = user.getFullName();
        String username = user.getUsername();

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            holder.getProfileImage().setImageTintMode(null);
            Picasso.get().load(profileImageUrl).fit().centerCrop().into(holder.getProfileImage());
        }
        holder.getTvName().setText(name);
        holder.getTvUsername().setText(username);
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
        void onItemClicked(String id);
    }

    /**
     * Fix for the bug in the RecyclerView that caused it to show incorrect data (e.g. image)
     * Source: https://www.solutionspirit.com/on-scrolling-recyclerview-change-values/
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * ViewHolder class for the list items
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemSingleFriendBinding binding;

        public UserViewHolder(ItemSingleFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        /**
         * Listeners for the views in the list item
         */
        private void setListeners() {
            binding.getRoot().setOnClickListener(v -> onItemClicked());
        }

        private void onItemClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;
            listener.onItemClicked(mUsers.get(position).getId());
        }


        /**
         * Getters for the views in the list item
         */
        public ImageView getProfileImage() {
            return binding.ivUserImage;
        }

        public TextView getTvName() {
            return binding.tvName;
        }

        public TextView getTvUsername() {
            return binding.tvUsername;
        }
    }
}