package com.example.cm.ui.adapters;

import static com.example.cm.utils.Utils.calculateDiff;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.Availability;
import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemSelectFriendBinding;
import com.example.cm.utils.Utils;

import java.util.ArrayList;
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

    public void setUsers(List<User> users) {
        List<User> newUsers = new ArrayList<>(users);

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
        ItemSelectFriendBinding binding = ItemSelectFriendBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);

        return new UserViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        String profileImageString = mUsers.get(position).getProfileImageString();
        String name = mUsers.get(position).getDisplayName();
        String username = mUsers.get(position).getUsername();
        Availability availability = mUsers.get(position).getAvailability();

        if (profileImageString != null && !profileImageString.isEmpty()) {
            Bitmap img = Utils.convertBaseStringToBitmap(profileImageString);
            holder.getProfileImage().setImageBitmap(img);
        }
        holder.getTvName().setText(name);
        holder.getTvUsername().setText(username);


        if (selectedUsers != null) {
            holder.getCbSelect().setChecked(selectedUsers.contains(mUsers.get(position).getId()));
        }
        if(availability != null){
            switch (availability) {
                case AVAILABLE:
                    holder.getAvailabilityDot().setImageResource(R.drawable.ic_dot_available);
                    break;
                case SOON_AVAILABLE:
                    holder.getAvailabilityDot().setImageResource(R.drawable.ic_dot_soon_available);
                    break;
                case UNAVAILABLE:
                    holder.getAvailabilityDot().setImageResource(R.drawable.ic_dot_unavailable);
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
        void onCheckBoxClicked(String id);

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

        public ImageView getAvailabilityDot(){
            return binding.dotAvailabilityIcon;
        }
    }
}