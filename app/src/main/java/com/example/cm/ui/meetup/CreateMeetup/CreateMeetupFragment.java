package com.example.cm.ui.meetup.CreateMeetup;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cm.R;
import com.example.cm.databinding.FragmentMeetupBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CreateMeetupFragment extends Fragment {

    int sMin, sHour;
    Calendar calendar = Calendar.getInstance();
    ArrayAdapter<CharSequence> adapter;
    private CreateMeetupViewModel createMeetupViewModel;
    private FragmentMeetupBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeetupBinding.inflate(inflater, container, false);
        setTodaysDate();
        initUI();
        initViewModel();
        initListener();
        return binding.getRoot();
    }

    private void setTodaysDate(){
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTime(new Date());
        calendar.set(Calendar.DATE, calendarNow.get(Calendar.DATE));
        calendar.set(Calendar.MONTH, calendarNow.get(Calendar.MONTH));
        calendar.set(Calendar.YEAR, calendarNow.get(Calendar.YEAR));
    }

    private void initUI() {
        showCurrentTime();
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.meetup_locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.meetupLocationSpinner.setAdapter(adapter);
    }

    private void initListener() {
        binding.meetupTimeText.setOnClickListener(v -> onTimePickerDialogClicked());
        binding.meetupInfoBtn.setOnClickListener(v -> checkTime());
    }

    private void showCurrentTime() {
        int localHour = calendar.get(Calendar.HOUR_OF_DAY);
        int localMin = calendar.get(Calendar.MINUTE);

        String formattedMin = String.format("%02d", localMin);
        String formattedHour = String.format("%02d", localHour);
        String currentTime = formattedHour + ":" + formattedMin;

        binding.meetupTimeText.setText(currentTime);
    }

    private void onTimePickerDialogClicked() {
        binding.meetupInfoBtn.setEnabled(true);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, selectedHour, selectedMinute) -> {
            sHour = selectedHour;
            sMin = selectedMinute;
            binding.meetupTimeText.setText(String.format(Locale.getDefault(), "%02d:%02d", sHour, sMin));
            calendar.set(Calendar.HOUR_OF_DAY, sHour);
            calendar.set(Calendar.MINUTE, sMin);
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, sHour, sMin, true);
        timePickerDialog.updateTime(sHour, sMin);
        timePickerDialog.show();
    }


    private void onMeetupInfoBtnClicked() {
        String location = binding.meetupLocationSpinner.getSelectedItem().toString();

        //TODO den boolean wieder benutzen wenn gefiltert werden kann
        //Boolean isPrivate = binding.meetupPrivateCheckBox.isChecked();

        Boolean isPrivate = true;

        createMeetupViewModel.setLocation(location);
        createMeetupViewModel.setIsPrivate(isPrivate);
        createMeetupViewModel.setMeetupTimestamp(calendar.getTime());

        Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInviteFriends);
    }


    private void initViewModel() {
        createMeetupViewModel = new ViewModelProvider(this).get(CreateMeetupViewModel.class);
        createMeetupViewModel.getMeetupLocation().observe(getViewLifecycleOwner(), location -> binding.meetupLocationSpinner.setSelection(adapter.getPosition(location)));
        createMeetupViewModel.getMeetupIsPrivate().observe(getViewLifecycleOwner(), isPrivate -> binding.meetupPrivateCheckBox.setChecked(isPrivate));
        createMeetupViewModel.getMeetupTimestamp().observe(getViewLifecycleOwner(), timestamp -> new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(calendar.getTime()));
    }

    private void checkTime() {
        int localHour = calendar.get(Calendar.HOUR_OF_DAY);
        int localMin = calendar.get(Calendar.MINUTE);

        String time = binding.meetupTimeText.getText().toString();
        String[] timeArray = time.split(":");
        int selectedHour = Integer.parseInt(timeArray[0]);
        int selectedMin = Integer.parseInt(timeArray[1]);

        if (localHour > selectedHour) {
            binding.meetupInfoBtn.setEnabled(false);
            Toast.makeText(getActivity(), R.string.meetup_time_in_past, Toast.LENGTH_SHORT).show();
        } else if (localHour == selectedHour && localMin > selectedMin) {
            binding.meetupInfoBtn.setEnabled(false);
            Toast.makeText(getActivity(), R.string.meetup_time_in_past, Toast.LENGTH_SHORT).show();
        } else {
            onMeetupInfoBtnClicked();
        }
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