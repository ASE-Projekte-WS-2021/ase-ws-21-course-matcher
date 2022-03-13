package com.example.cm.utils;

import static com.example.cm.Constants.MAX_CHAR_COUNT;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.cm.R;
import com.example.cm.databinding.DialogTextareaBinding;


public class EditTextAreaDialog extends Dialog {
    DialogTextareaBinding binding;
    OnSaveListener listener;
    String initialValue, fieldToUpdate;

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
        binding.inputField.setSelection(binding.inputField.getText().length());
    }

    public EditTextAreaDialog setFieldToUpdate(String fieldToUpdate) {
        this.fieldToUpdate = fieldToUpdate;
        String title = getContext().getResources().getString(R.string.dialog_title_field);
        title = title.replace("{field}", fieldToUpdate);
        binding.dialogTitle.setText(title);
        return this;
    }

    public EditTextAreaDialog setValueOfField(String value) {
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
        // Show keyboard
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void initUI() {
        binding.bioCharacterCount.setText(String.format("%d/%d", 0, MAX_CHAR_COUNT));
    }

    private void initListeners() {
        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSave.setOnClickListener(v -> onSaveClicked());

        binding.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currCharCount = s.length();
                binding.bioCharacterCount.setText(String.format("%d/%d", currCharCount, MAX_CHAR_COUNT));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 125) {
                    binding.inputField.getText().delete(MAX_CHAR_COUNT, s.length());
                }
            }
        });
    }

    private void onSaveClicked() {
        String newValue = binding.inputField.getText().toString();
        if (listener != null) {
            listener.onTextAreaSaved(fieldToUpdate, newValue);
        }
    }

    public interface OnSaveListener {
        void onTextAreaSaved(String fieldToUpdate, String updatedValue);
    }
}
