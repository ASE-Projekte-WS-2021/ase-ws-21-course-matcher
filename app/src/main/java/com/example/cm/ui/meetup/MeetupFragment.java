package com.example.cm.ui.meetup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupBinding;
import com.example.cm.ui.SharedViewModel;


public class MeetupFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private FragmentMeetupBinding binding;
    ArrayAdapter<CharSequence> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMeetupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        initUI();
        initViewModel();
        initListener();
        return root;

    }

    private void initUI() {
        binding.meetupTimePicker.setIs24HourView(true);
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.meetup_locations, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.meetupLocationSpinner.setAdapter(adapter);


    }

    private void initListener() {


        binding.meetupInfoBtn.setOnClickListener(v -> {

            // shows the next fragment where you can choose your friends
            Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInviteFriends);
            String location = binding.meetupLocationSpinner.getSelectedItem().toString();
            String hour = binding.meetupTimePicker.getCurrentHour().toString();
            String min = binding.meetupTimePicker.getCurrentMinute().toString();
            String time = hour + ":" + min;
            Boolean isPrivate = binding.meetupPrivateCheckBox.isChecked();

            sharedViewModel.setLocation(location);
            sharedViewModel.setTime(time);
            sharedViewModel.setIsPrivate(isPrivate);
        });
    }

    private void initViewModel(){
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getMeetupLocation().observe(getViewLifecycleOwner(), location ->{
            binding.meetupLocationSpinner.setSelection(adapter.getPosition(location));
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}