package com.example.cm.ui.meetup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cm.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeetupNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeetupNotificationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Default fragment";
    private static final String ARG_PARAM2 = "TBD: Meetupnotifications here";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MeetupNotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_meetup_notifications.
     */
    // TODO: Rename and change types and number of parameters
    public static MeetupNotificationsFragment newInstance(String param1, String param2) {
        MeetupNotificationsFragment fragment = new MeetupNotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meetup_notifications, container, false);
    }
}