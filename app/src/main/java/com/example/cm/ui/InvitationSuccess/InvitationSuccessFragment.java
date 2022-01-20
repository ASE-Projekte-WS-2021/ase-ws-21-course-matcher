package com.example.cm.ui.InvitationSuccess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cm.R;
import com.example.cm.databinding.FragmentInvitationSuccessBinding;


public class InvitationSuccessFragment extends Fragment {

    private InvitationSuccessViewModel invitationSuccessViewModel;
    private FragmentInvitationSuccessBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        invitationSuccessViewModel = new ViewModelProvider(this).get(InvitationSuccessViewModel.class);

        binding = FragmentInvitationSuccessBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        invitationSuccessViewModel = new ViewModelProvider(this).get(InvitationSuccessViewModel.class);
        initListener();

        return root;
    }

    private void initListener() {
        binding.backToHomeBtn.setOnClickListener(v ->{
            Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToHome);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}