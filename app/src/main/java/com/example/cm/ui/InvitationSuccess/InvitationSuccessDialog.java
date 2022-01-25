package com.example.cm.ui.InvitationSuccess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cm.R;
import com.example.cm.utils.Navigator;

public class InvitationSuccessDialog extends AppCompatDialogFragment {

    private Navigator navigator;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String dialogMessage = getString(R.string.invitation_success_dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
        builder.setTitle("Meetup versendet!").setMessage(dialogMessage).setPositiveButton("zurück zur Karte", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                navigator.navigateToMap();
            }
        });
        return builder.create();
    }
}