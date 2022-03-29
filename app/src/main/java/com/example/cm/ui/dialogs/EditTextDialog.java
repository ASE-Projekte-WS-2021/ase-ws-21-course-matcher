package com.example.cm.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.cm.R;
import com.example.cm.databinding.DialogEditTextBinding;
import com.google.android.material.textfield.TextInputLayout;

public class EditTextDialog extends Dialog {
    DialogEditTextBinding binding;
    OnSaveListener listener;
    String initialValue, fieldToUpdate;
    String confirmButtonText;

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

    public EditTextDialog setTitle(String title) {
        binding.dialogTitle.setText(title);
        return this;
    }

    public EditTextDialog setDescription(String description) {
        binding.dialogDescription.setText(description);
        binding.dialogDescription.setVisibility(View.VISIBLE);
        return this;
    }

    public EditTextDialog setConfirmButtonText(String text) {
        binding.btnConfirm.setText(text);
        confirmButtonText = text;
        return this;
    }

    public void disableConfirmButton() {
        binding.btnConfirm.setEnabled(false);
        binding.btnConfirm.setText(R.string.confirm_button_loading);
        Drawable buttonDrawable = DrawableCompat.wrap(binding.btnConfirm.getBackground());
        DrawableCompat.setTint(buttonDrawable, getContext().getResources().getColor(R.color.gray600));
        binding.btnConfirm.setBackground(buttonDrawable);
    }

    public void enableConfirmButton() {
        binding.btnConfirm.setEnabled(true);
        binding.btnConfirm.setText(confirmButtonText);
        Drawable buttonDrawable = DrawableCompat.wrap(binding.btnConfirm.getBackground());
        DrawableCompat.setTint(buttonDrawable, getContext().getResources().getColor(R.color.orange500));
        binding.btnConfirm.setBackground(buttonDrawable);
    }

    public EditTextDialog setIconMode(int type) {
        switch (type) {
            case TextInputLayout.END_ICON_PASSWORD_TOGGLE:
                binding.inputField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            default:
                binding.inputField.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                break;
        }

        binding.textInputLayout.setEndIconMode(type);
        return this;
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

    public void setError(String error) {
        binding.textInputLayout.setError(error);
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
        binding.btnCancel.setOnClickListener(v -> onDismissClicked());
        binding.btnConfirm.setOnClickListener(v -> onSaveClicked());
    }

    private void onDismissClicked() {
        binding.textInputLayout.setErrorEnabled(false);
        binding.inputField.setText(null);
        dismiss();
    }

    private void onSaveClicked() {
        if (binding.inputField.getText() == null) {
            return;
        }

        String newValue = binding.inputField.getText().toString();
        if (listener != null) {
            listener.onTextInputSaved(fieldToUpdate, newValue);
        }
        disableConfirmButton();
    }

    public interface OnSaveListener {
        void onTextInputSaved(String fieldToUpdate, String updatedValue);
    }
}