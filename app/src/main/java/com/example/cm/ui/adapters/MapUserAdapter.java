package com.example.cm.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemMapUserBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MapUserAdapter extends RecyclerView.Adapter<MapUserAdapter.UserViewHolder> {

    private final OnItemClickListener listener;
    private final List<MutableLiveData<User>> mUsers = new ArrayList<>();

    public MapUserAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void addUser(User user) {
        mUsers.add(new MutableLiveData<>(user));
        notifyItemInserted(mUsers.size() - 1);
    }

    public User getUserAt(int position) {
        if (position >= mUsers.size()) {
            return null;
        }
        return mUsers.get(position).getValue();
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        ItemMapUserBinding binding = ItemMapUserBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new UserViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        User user = mUsers.get(position).getValue();
        if (user == null) {
            return;
        }

        String profileImageUrl = user.getProfileImageUrl();
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

    public interface OnItemClickListener {
        void onItemClicked(String id);

        void onMeetUserClicked(String id);
    }

    /**
     * ViewHolder class for the list items
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemMapUserBinding binding;

        public UserViewHolder(ItemMapUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        /**
         * Listeners for the views in the list item
         */
        private void setListeners() {
            binding.getRoot().setOnClickListener(v -> onItemClicked());
            binding.btnMeetUser.setOnClickListener(v -> onMeetUserClicked());
        }

        private void onItemClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;

            User user = mUsers.get(position).getValue();
            if (user == null) {
                return;
            }
            listener.onItemClicked(user.getId());
        }

        private void onMeetUserClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null) return;

            User user = mUsers.get(position).getValue();
            if (user == null) {
                return;
            }
            listener.onMeetUserClicked(user.getId());
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