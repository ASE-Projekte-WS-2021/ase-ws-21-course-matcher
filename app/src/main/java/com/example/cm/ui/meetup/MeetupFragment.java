package com.example.cm.ui.meetup;

import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.util.GregorianCalendar;


public class MeetupFragment extends Fragment {

    ArrayAdapter<CharSequence> adapter;
    int sMin, sHour;
    Calendar calendar = GregorianCalendar.getInstance();
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
        binding.meetupInfoBtn.setOnClickListener(v -> {
            checkTime();
            //onMeetupInfoBtnClicked();
        });
    }

    private void onMeetupInfoBtnClicked() {

        Date timeStamp = new Date();
        String location = binding.meetupLocationSpinner.getSelectedItem().toString();
        String hour = binding.meetupTimePicker.getCurrentHour().toString();
        String min = binding.meetupTimePicker.getCurrentMinute().toString();
        if (min.length() == 1) {
            min = "0" + min;
        }
        String time = hour + ":" + min;
        //TODO den boolean wieder benutzen wenn gefiltert werden kann
        //Boolean isPrivate = binding.meetupPrivateCheckBox.isChecked();

        Boolean isPrivate = true;

        createMeetupViewModel.setLocation(location);
        createMeetupViewModel.setTime(time);
        createMeetupViewModel.setIsPrivate(isPrivate);
        createMeetupViewModel.setMeetupTimestamp(timeStamp);

        Navigation.findNavController(binding.getRoot()).navigate(R.id.navigateToInviteFriends);
    }


    private void initViewModel() {
        createMeetupViewModel = new ViewModelProvider(this).get(CreateMeetupViewModel.class);
        createMeetupViewModel.getMeetupLocation().observe(getViewLifecycleOwner(), location -> binding.meetupLocationSpinner.setSelection(adapter.getPosition(location)));
        createMeetupViewModel.getMeetupTime().observe(getViewLifecycleOwner(), time -> setTimePickerTime(time));
        createMeetupViewModel.getMeetupIsPrivate().observe(getViewLifecycleOwner(), isPrivate -> binding.meetupPrivateCheckBox.setChecked(isPrivate));
        createMeetupViewModel.getMeetupTimestamp().observe(getViewLifecycleOwner(), timestamp -> getTimestamp());

    }

    private void getTimestamp() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    private void setTimePickerTime(String time) {
        String[] timeArray = time.split(":");
        sHour = Integer.parseInt(timeArray[0]);
        sMin = Integer.parseInt(timeArray[1]);
        binding.meetupTimePicker.setCurrentHour(sHour);
        binding.meetupTimePicker.setCurrentMinute(sMin);
    }

    private void checkTime() {
        int localHour = calendar.get(Calendar.HOUR_OF_DAY);
        int localMin = calendar.get(Calendar.MINUTE);

        int selectedHour = binding.meetupTimePicker.getCurrentHour();
        int selectedMin = binding.meetupTimePicker.getCurrentMinute();

        if (localHour > selectedHour) {
            delayButton();
        } else if (localHour == selectedHour && localMin > selectedMin) {
            delayButton();
        } else {
            onMeetupInfoBtnClicked();
        }
    }

    private void delayButton() {
        binding.meetupInfoBtn.setEnabled(false);
        Toast.makeText(getActivity(), "Die gewählte Uhrzeit liegt in der Vergangenheit. Bitte wähle eine neue Uhrzeit.", Toast.LENGTH_LONG).show();
        new CountDownTimer(2000, 10) { //Set Timer for 5 seconds
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                binding.meetupInfoBtn.setEnabled(true);
            }
        }.start();
        return;
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