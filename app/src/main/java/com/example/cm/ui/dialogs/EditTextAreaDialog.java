package com.example.cm.ui.dialogs;

import static com.example.cm.Constants.MAX_CHAR_COUNT;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.cm.R;
import com.example.cm.databinding.DialogTextareaBinding;


public class EditTextAreaDialog extends Dialog {
    private final DialogTextareaBinding binding;
    private final OnSaveListener listener;
    private String fieldToUpdate;
    private String confirmButtonText;

    public EditTextAreaDialog(@NonNull Context context, OnSaveListener listener) {
        super(context);
        this.listener = listener;
        binding = DialogTextareaBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        initRootView();
        initUI();
        initListeners();
    }

    @Override
    public void show() {
        super.show();
        binding.inputField.requestFocus();

        if (binding.inputField.getText() != null) {
            binding.inputField.setSelection(binding.inputField.getText().length());
        }
    }

    public EditTextAreaDialog setFieldToUpdate(String fieldToUpdate) {
        this.fieldToUpdate = fieldToUpdate;
        String title = getContext().getString(R.string.dialog_title_field, fieldToUpdate);
        binding.dialogTitle.setText(title);
        return this;
    }

    public EditTextAreaDialog setValueOfField(String value) {
        binding.inputField.setText(value);
        return this;
    }

    public EditTextAreaDialog setConfirmButtonText(String text) {
        binding.btnSave.setText(text);
        confirmButtonText = text;
        return this;
    }

    public void setError(String error) {
        binding.textInputLayout.setError(error);
    }

    public void disableConfirmButton() {
        Drawable buttonDrawable = DrawableCompat.wrap(binding.btnSave.getBackground());
        DrawableCompat.setTint(buttonDrawable, getContext().getResources().getColor(R.color.gray600));

        binding.btnSave.setEnabled(false);
        binding.btnSave.setText(R.string.confirm_button_loading);
        binding.btnSave.setBackground(buttonDrawable);
    }

    public void enableConfirmButton() {
        Drawable buttonDrawable = DrawableCompat.wrap(binding.btnSave.getBackground());
        DrawableCompat.setTint(buttonDrawable, getContext().getResources().getColor(R.color.orange500));

        binding.btnSave.setEnabled(true);
        binding.btnSave.setText(confirmButtonText);
        binding.btnSave.setBackground(buttonDrawable);
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

    private void initUI() {
        String charCountString = getContext().getString(R.string.edit_profile_bio_char_count, 0, MAX_CHAR_COUNT);
        binding.bioCharacterCount.setText(charCountString);
    }

    private void initListeners() {
        binding.btnCancel.setOnClickListener(v -> onDismissClicked());
        binding.btnSave.setOnClickListener(v -> onSaveClicked());

        binding.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currCharCount = s.length();
                String charCountString = getContext().getString(R.string.edit_profile_bio_char_count, currCharCount, MAX_CHAR_COUNT);
                binding.bioCharacterCount.setText(charCountString);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.inputField.getText() == null) {
                    return;
                }

                // Prevent entering new line characters
                if (!s.toString().isEmpty() && s.toString().substring(s.length() - 1).equals("\n")) {
                    binding.inputField.getText().delete(s.length() - 1, s.length());
                }

                // Limit bio length to MAX_CHAR_COUNT
                if (s.length() > MAX_CHAR_COUNT) {
                    binding.inputField.getText().delete(MAX_CHAR_COUNT, s.length());
                }
            }
        });
    }

    private void onDismissClicked() {
        binding.textInputLayout.setErrorEnabled(false);
        binding.inputField.setText(null);
        dismiss();
    }

    private void onSaveClicked() {
        if (binding.inputField.getText() == null || listener == null) {
            return;
        }

        String newValue = binding.inputField.getText().toString();
        listener.onTextAreaSaved(fieldToUpdate, newValue);
        disableConfirmButton();
    }

    public interface OnSaveListener {
        void onTextAreaSaved(String fieldToUpdate, String updatedValue);
    }
}
