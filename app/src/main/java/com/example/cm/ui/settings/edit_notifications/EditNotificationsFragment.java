package com.example.cm.ui.settings.edit_notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cm.databinding.FragmentEditNotificationsBinding;
import com.example.cm.utils.Navigator;

public class EditNotificationsFragment extends Fragment {
    FragmentEditNotificationsBinding binding;
    Navigator navigator;

    public EditNotificationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditNotificationsBinding.inflate(inflater, container, false);

        initListeners();

        return binding.getRoot();
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
    }
}