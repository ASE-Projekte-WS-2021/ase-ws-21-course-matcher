package com.example.cm.ui.settings.edit_account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cm.databinding.FragmentEditAccountBinding;
import com.example.cm.utils.Navigator;

public class EditAccountFragment extends Fragment {
    FragmentEditAccountBinding binding;
    Navigator navigator;

    public EditAccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false);

        initListeners();

        return binding.getRoot();
    }

    private void initListeners() {
        navigator = new Navigator(requireActivity());
        binding.btnBack.setOnClickListener(v -> navigator.getNavController().popBackStack());
    }
}