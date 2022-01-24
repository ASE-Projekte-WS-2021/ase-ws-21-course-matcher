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


public class CreateMeetupFragment extends Fragment {

    ArrayAdapter<CharSequence> adapter;
    int sMin, sHour;
    private CreateMeetupViewModel createMeetupViewModel;
    private FragmentMeetupBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMeetupBinding.inflate(inflater, container, false);

        initUI();
        initViewModel();
        initListener();
        return binding.getRoot();
    }

    private void initUI() {
        binding.meetupTimePicker.setIs24HourView(true);
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.meetup_locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.meetupLocationSpinner.setAdapter(adapter);
    }

    private void initListener() {
        binding.meetupInfoBtn.setOnClickListener(v -> onMeetupInfoBtnClicked());
    }

    private void onMeetupInfoBtnClicked() {
        String location = binding.meetupLocationSpinner.getSelectedItem().toString();
        String hour = binding.meetupTimePicker.getCurrentHour().toString();
        String min = binding.meetupTimePicker.getCurrentMinute().toString();
        String time = hour + ":" + min;
        Boolean isPrivate = binding.meetupPrivateCheckBox.isChecked();

        createMeetupViewModel.setLocation(location);
        createMeetupViewModel.setTime(time);
        createMeetupViewModel.setIsPrivate(isPrivate);

        Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInviteFriends);
    }

    private void initViewModel() {
        createMeetupViewModel = new ViewModelProvider(this).get(CreateMeetupViewModel.class);
        createMeetupViewModel.getMeetupLocation().observe(getViewLifecycleOwner(), location -> binding.meetupLocationSpinner.setSelection(adapter.getPosition(location)));
        createMeetupViewModel.getMeetupTime().observe(getViewLifecycleOwner(), this::setTimePickerTime);
        createMeetupViewModel.getMeetupIsPrivate().observe(getViewLifecycleOwner(), isPrivate -> binding.meetupPrivateCheckBox.setChecked(isPrivate));
    }

    private void setTimePickerTime(String time) {
        String[] timeArray = time.split(":");
        sHour = Integer.parseInt(timeArray[0]);
        sMin = Integer.parseInt(timeArray[1]);
        binding.meetupTimePicker.setCurrentHour(sHour);
        binding.meetupTimePicker.setCurrentMinute(sMin);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createMeetupViewModel = new ViewModelProvider(requireActivity()).get(CreateMeetupViewModel.class);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}