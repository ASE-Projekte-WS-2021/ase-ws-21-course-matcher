package com.example.cm.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.cm.databinding.DialogEditTextBinding;

public class EditTextDialog extends Dialog {
    DialogEditTextBinding binding;
    OnSaveListener listener;
    String initialValue, fieldToUpdate;

    public EditTextDialog(@NonNull Context context, OnSaveListener listener) {
        super(context);
        this.listener = listener;
        binding = DialogEditTextBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        initRootView();
        initListeners();
    }

    @Override
    public void show() {
        super.show();
        binding.inputField.requestFocus();
        binding.inputField.setSelection(binding.inputField.getText().length());
    }

    public EditTextDialog setFieldToUpdate(String fieldToUpdate) {
        this.fieldToUpdate = fieldToUpdate;
        binding.dialogTitle.setText(fieldToUpdate + " bearbeiten");
        return this;
    }

    public EditTextDialog setValueOfField(String value) {
        binding.inputField.setText(value);
        initialValue = value;
        return this;
    }

    private void initRootView() {
        Window window = this.getWindow();
        // Make root transparent so rounded border is visible
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Make dialog full width
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void initListeners() {
        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSave.setOnClickListener(v -> {
            String newValue = binding.inputField.getText().toString();
            if (listener != null) {
                listener.onSave(fieldToUpdate, newValue);
            }
        });
    }

    public interface OnSaveListener {
        void onSave(String fieldToUpdate, String updatedValue);
    }
}
