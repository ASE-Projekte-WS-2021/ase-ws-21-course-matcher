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
import com.example.cm.databinding.DialogTextWithButtonBinding;

public class TextWithButtonDialog extends Dialog {

    DialogTextWithButtonBinding binding;
    TextWithButtonDialog.OnConfirmListener listener;

    public TextWithButtonDialog(@NonNull Context context, TextWithButtonDialog.OnConfirmListener listener) {
        super(context);
        this.listener = listener;
        binding = DialogTextWithButtonBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        initRootView();
        initListeners();
    }

    public void setTitle(String title) {
        binding.dialogTitle.setText(title);
    }

    public void setConfirmButtonText(String text) {
        binding.btnConfirm.setText(text);
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
        binding.btnConfirm.setOnClickListener(v -> onConfirmClicked());
    }

    private void onConfirmClicked() {
        listener.onConfirmClicked();
    }

    public interface OnConfirmListener {
        void onConfirmClicked();
    }
}
