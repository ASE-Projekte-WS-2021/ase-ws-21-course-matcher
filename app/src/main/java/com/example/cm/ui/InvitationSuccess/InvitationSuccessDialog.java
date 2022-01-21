package com.example.cm.ui.InvitationSuccess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cm.utils.Navigator;

public class InvitationSuccessDialog extends AppCompatDialogFragment {

    private Navigator navigator;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
        builder.setTitle("Meetup versendet!").setMessage("Dein Meetup wurde erfolgreich versendet. Warte auf die Reaktion deiner Freunde und viel Spaß bei eurem Treffen!").setPositiveButton("zurück zur Karte", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                navigator.navigateToMap();
            }
        });
        return builder.create();
    }
}