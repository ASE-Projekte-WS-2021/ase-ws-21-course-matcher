package com.example.cm.ui.meetup_invite_success;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cm.R;

public class InvitationSuccessDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String dialogMessage = getString(R.string.invitation_success_dialog);
        String dialogTitle = getString(R.string.invitation_success_title);
        String dialogClose = getString(R.string.invitation_success_close_dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
        builder.setTitle(dialogTitle).setMessage(dialogMessage).setPositiveButton(dialogClose, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }
}