package com.example.cm.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

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

    public TextWithButtonDialog setTitle(String title) {
        binding.dialogTitle.setText(title);
        return this;
    }

    public TextWithButtonDialog setConfirmButtonText(String text) {
        binding.btnConfirm.setText(text);
        return this;
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
        binding.btnConfirm.setOnClickListener(v -> {
            listener.onConfirmClicked();
        });
    }

    public interface OnConfirmListener {
        void onConfirmClicked();
    }
}
