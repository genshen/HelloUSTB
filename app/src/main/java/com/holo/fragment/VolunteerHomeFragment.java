package com.holo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holo.helloustb.R;
import com.holo.view.RiseNumberTextView;

import java.util.ArrayList;

public class VolunteerHomeFragment extends Fragment {
    private static final String VOL_RESULT = "vol_result";
    private ArrayList<String> volunteer_meg;


    public static VolunteerHomeFragment newInstance(ArrayList<String> vol_result) {
        VolunteerHomeFragment fragment = new VolunteerHomeFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(VOL_RESULT, vol_result);
        fragment.setArguments(args);
        return fragment;
    }

    public VolunteerHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            volunteer_meg = getArguments().getStringArrayList(VOL_RESULT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_volunteer_home, container, false);

        float work_hour = Float.parseFloat(volunteer_meg.get(4));
        String school = volunteer_meg.get(0).split("：")[1];    //学院； 特别注意分号字符，是中文的
        String vol_class = volunteer_meg.get(1).split("：")[1];    //班级
        ((TextView) rootView.findViewById(R.id.vol_school)).setText(school);
        ((TextView) rootView.findViewById(R.id.vol_class)).setText(vol_class);

        RiseNumberTextView work_hour_text = (RiseNumberTextView) rootView.findViewById(R.id.vol_my_work);
        work_hour_text.setRiseInterval(0, work_hour)
                .setDuration(2000)
                .runInt(false)
                .setDecimal(2)
                .start();
        return rootView;
    }
}
