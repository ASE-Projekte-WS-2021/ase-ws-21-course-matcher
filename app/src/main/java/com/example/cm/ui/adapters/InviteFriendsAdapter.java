package com.example.cm.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemSelectFriendBinding;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class InviteFriendsAdapter extends RecyclerView.Adapter<InviteFriendsAdapter.UserViewHolder> {

    private final OnItemClickListener listener;
    private List<User> mUsers;
    // Store a current selection of users in memory
    private List<String> selectedUsers;


    public InviteFriendsAdapter(InviteFriendsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedUsers(List<String> selectedUsers) {
        this.selectedUsers = selectedUsers;
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

    /**
     * Calculate the difference between two lists and return the result
     * Also used to animate the changes
     * From https://stackoverflow.com/questions/49588377/how-to-set-adapter-in-mvvm-using-databinding
     *
     * @param oldUsers The old list of users
     * @param newUsers The new list of users
     * @return The result of the calculation
     */
    private DiffUtil.DiffResult calculateDiff(List<User> oldUsers, List<User> newUsers) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldUsers.size();
            }

            @Override
            public int getNewListSize() {
                return newUsers.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldUsers.get(oldItemPosition).getId(), newUsers.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                User newUser = newUsers.get(newItemPosition);
                User oldUser = oldUsers.get(oldItemPosition);
                return Objects.equals(newUser.getId(), oldUser.getId())
                        && Objects.equals(newUser.getFirstName(), oldUser.getFirstName())
                        && Objects.equals(newUser.getLastName(), oldUser.getLastName())
                        && Objects.equals(newUser.getUsername(), oldUser.getUsername());
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        ItemSelectFriendBinding binding = ItemSelectFriendBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);

        return new UserViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        String profileImageUrl = mUsers.get(position).getProfileImageUrl();
        String name = mUsers.get(position).getFirstName() + " " + mUsers.get(position).getLastName();
        String username = mUsers.get(position).getUsername();

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            holder.getProfileImage().setImageTintMode(null);
            Picasso.get().load(profileImageUrl).fit().centerCrop().into(holder.getProfileImage());
        }
        holder.getTvName().setText(name);
        holder.getTvUsername().setText(username);

        if (selectedUsers != null) {
            holder.getCbSelect().setChecked(selectedUsers.contains(mUsers.get(position).getId()));
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
        void onCheckBoxClicked(String id);

        void onItemClicked(String id);
    }


    /**
     * ViewHolder class for the list items
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemSelectFriendBinding binding;

        public UserViewHolder(ItemSelectFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        /**
         * Listeners for the views in the list item
         */
        private void setListeners() {
            binding.getRoot().setOnClickListener(v -> onItemClicked());
            binding.cbSelect.setOnClickListener(v -> onCheckBoxClicked());
        }

        private void onItemClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;
            listener.onItemClicked(mUsers.get(position).getId());
        }

        private void onCheckBoxClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;
            listener.onCheckBoxClicked(mUsers.get(position).getId());
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

        public CheckBox getCbSelect() {
            return binding.cbSelect;
        }
    }
}