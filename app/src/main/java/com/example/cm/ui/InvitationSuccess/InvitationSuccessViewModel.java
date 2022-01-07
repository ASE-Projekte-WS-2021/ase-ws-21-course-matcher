package com.example.cm.ui.InvitationSuccess;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InvitationSuccessViewModel extends ViewModel {
    public InvitationSuccessViewModel() {
        MutableLiveData<String> mText;

        mText = new MutableLiveData<>();
        mText.setValue("This is invite fragment");
    }
}
