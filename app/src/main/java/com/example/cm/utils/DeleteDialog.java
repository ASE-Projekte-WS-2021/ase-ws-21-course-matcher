package com.example.cm.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.cm.databinding.DialogDeleteMeetupBinding;
import com.example.cm.databinding.DialogLogoutBinding;

public class DeleteDialog extends Dialog {

    DialogDeleteMeetupBinding binding;
    DeleteDialog.OnDeleteListener listener;

    public DeleteDialog(@NonNull Context context, DeleteDialog.OnDeleteListener listener) {
        super(context);
        this.listener = listener;
        binding = DialogDeleteMeetupBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        initRootView();
        initListeners();
    }

    private void initRootView() {
        Window window = this.getWindow();
        // Make root transparent so rounded border is visible
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Make dialog full width
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // Show keyboard
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void initListeners() {
        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnLogout.setOnClickListener(v -> onLogoutClicked());
    }

    private void onLogoutClicked() {
        listener.onDeleteApproved();
    }

    public interface OnDeleteListener {
        void onDeleteApproved();
    }
}
