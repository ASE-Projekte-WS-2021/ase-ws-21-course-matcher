package com.example.cm.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cm.R;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.databinding.ItemMeetupRequestBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

public class MeetupRequestListAdapter extends RecyclerView.Adapter<MeetupRequestListAdapter.MeetupRequestViewHolder> {

    private ViewGroup parent;
    private List<MeetupRequest> mRequests;
    private List<User> users;
    private final OnMeetupRequestListener listener;

    public MeetupRequestListAdapter(OnMeetupRequestListener listener) {
        this.listener = listener;
    }

    public void setRequests(List<MeetupRequest> newRequests, List<User> users) {
        if (mRequests == null) {
            mRequests = newRequests;
            this.users = users;
            notifyItemRangeInserted(0, newRequests.size());
            return;
        }

        DiffUtil.DiffResult result = calculateDiffMeetupRequests(mRequests, newRequests);
        mRequests = newRequests;
        result.dispatchUpdatesTo(this);
    }

    public static DiffUtil.DiffResult calculateDiffMeetupRequests(List<MeetupRequest> oldRequests,
            List<MeetupRequest> newRequests) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldRequests.size();
            }

            @Override
            public int getNewListSize() {
                return newRequests.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldRequests.get(oldItemPosition).getId(),
                        newRequests.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                MeetupRequest newRequest = newRequests.get(newItemPosition);
                MeetupRequest oldRequest = oldRequests.get(oldItemPosition);

                return Objects.equals(Objects.requireNonNull(newRequest).getId(),
                        Objects.requireNonNull(oldRequest).getId());
            }
        });
    }

    public void deleteItem(int position) {
        MeetupRequest request = mRequests.get(position);
        Request.RequestState previousState = Objects.requireNonNull(request).getState();
        mRequests.remove(position);
        notifyItemRemoved(position);
        listener.onItemDeleted(request);
        Snackbar snackbar = Snackbar.make(parent, R.string.delete_snackbar_text, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo_snackbar_text, view -> onUndoDelete(request, position, previousState));
        snackbar.show();
    }

    private void onUndoDelete(MeetupRequest request, int position, Request.RequestState previousState) {
        listener.onUndoDelete(request, position, previousState);
        mRequests.add(position, request);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public MeetupRequestListAdapter.MeetupRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
            int viewType) {
        this.parent = parent;
        ItemMeetupRequestBinding binding = ItemMeetupRequestBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new MeetupRequestListAdapter.MeetupRequestViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MeetupRequestListAdapter.MeetupRequestViewHolder holder, int position) {
        Context context = holder.binding.getRoot().getContext();
        MeetupRequest request = mRequests.get(position);

        String user = String.format("@%s ", getDisplayName(Objects.requireNonNull(request).getSenderId()));
        String date = request.getCreationTimeAgo();
        String location = request.getLocation();

        boolean isAccepted = request.getState() == Request.RequestState.REQUEST_ACCEPTED;

        int content = 0;
        switch (request.getType()) {
            case MEETUP_REQUEST:
                content = isAccepted ? R.string.meetup_accepted_text : R.string.meetup_request_description;
                break;
            case MEETUP_INFO_ACCEPTED:
                isAccepted = true;
                content = R.string.meetup_accepted_text;
                break;
            case MEETUP_INFO_DECLINED:
                isAccepted = true;
                content = R.string.meetup_declined_text;
                break;
        }

        holder.getTvLocation().setText(location);
        holder.getTvSender().setText(user);
        holder.getTvDate().setText(date);
        holder.getTvDescription().setText(content);
        holder.getBtnAccept().setVisibility(isAccepted ? View.GONE : View.VISIBLE);
        holder.getBtnDecline().setVisibility(isAccepted ? View.GONE : View.VISIBLE);

        switch (request.getPhase()) {
            case MEETUP_UPCOMING:
                holder.getTvMeetupTime().setText(request.getFormattedTime());
                holder.getTvMeetupTime().setTextColor(context.getResources().getColor(R.color.orange400));
                holder.getTvMeetupTime().setBackground(context.getResources().getDrawable(R.drawable.label_rounded_upcoming));
                break;
            case MEETUP_ACTIVE:
                holder.getTvMeetupTime().setText(context.getString(R.string.meetup_active_text, request.getFormattedTime()));
                holder.getTvMeetupTime().setTextColor(context.getResources().getColor(R.color.orange600));
                holder.getTvMeetupTime().setBackground(context.getResources().getDrawable(R.drawable.label_rounded_active));
                break;
            case MEETUP_ENDED:
                holder.getTvMeetupTime().setText(R.string.meetup_ended_text);
                holder.getTvMeetupTime().setTextColor(context.getResources().getColor(R.color.gray500));
                holder.getTvMeetupTime().setBackground(context.getResources().getDrawable(R.drawable.label_rounded_ended));

                int color = context.getResources().getColor(R.color.outgreyed);
                holder.getTvLocation().setTextColor(color);
                holder.getTvSender().setTextColor(color);
                holder.getTvDescription().setTextColor(color);
                holder.getBtnAccept().setVisibility(View.GONE);
                holder.getBtnDecline().setVisibility(View.GONE);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getDisplayName(String userId) {
        User user = users.stream()
                .filter(userData -> userData.getId().equals(userId)).findAny()
                .orElse(null);
        if (user != null) {
            return user.getDisplayName();
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        if (mRequests == null) {
            return 0;
        }
        return mRequests.size();
    }

    public interface OnMeetupRequestListener {
        void onItemClicked(String id);

        void onItemDeleted(MeetupRequest request);

        void onAccept(MeetupRequest request);

        void onDecline(MeetupRequest request);

        void onUndoDecline(MeetupRequest request, int position);

        void onUndoDelete(MeetupRequest request, int position, Request.RequestState previousState);
    }

    /**
     * Fix for the bug in the RecyclerView that caused it to show incorrect data
     * (e.g. image)
     * Source:
     * https://www.solutionspirit.com/on-scrolling-recyclerview-change-values/
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MeetupRequestViewHolder extends RecyclerView.ViewHolder {

        private final ItemMeetupRequestBinding binding;

        public MeetupRequestViewHolder(ItemMeetupRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setListeners();
        }

        private void setListeners() {
            binding.getRoot().setOnClickListener(view -> onItemClicked());
            binding.acceptButton.setOnClickListener(view -> onAccept());
            binding.declineButton.setOnClickListener(view -> onDecline());
        }

        private void onItemClicked() {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || listener == null)
                return;
            listener.onItemClicked(mRequests.get(position).getMeetupId());
        }

        private void onAccept() {
            MeetupRequest request = mRequests.get(getAdapterPosition());
            listener.onAccept(request);
            notifyItemChanged(getAdapterPosition());
        }

        private void onUndo(MeetupRequest request, int position) {
            listener.onUndoDecline(request, position);
            notifyItemInserted(position);
        }

        private void onDecline() {
            int position = getAdapterPosition();
            MeetupRequest request = mRequests.get(position);
            mRequests.remove(position);
            notifyItemRemoved(position);
            listener.onDecline(request);

            Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.decline_snackbar_text, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo_snackbar_text, view -> onUndo(request, position));
            snackbar.show();
        }

        /**
         * Getters for the views in the list item
         */
        public TextView getTvMeetupTime() {
            return binding.meetupTimeTextView;
        }

        public TextView getTvLocation() {
            return binding.locationTextView;
        }

        public TextView getTvSender() {
            return binding.meetupSenderTextView;
        }

        public TextView getTvDescription() {
            return binding.meetupSenderDescriptionTextView;
        }

        public TextView getTvDate() {
            return binding.sentDateTextView;
        }

        public ImageView getBtnAccept() {
            return binding.acceptButton;
        }

        public ImageView getBtnDecline() {
            return binding.declineButton;
        }
    }
}
