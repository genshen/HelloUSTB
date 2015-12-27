package com.holo.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holo.helloustb.R;
import com.holo.view.MagicScrollView;
import com.holo.view.MagicTextView;

import java.util.ArrayList;

public class VolunteerHomeFragment extends Fragment {
    private static final String VOL_RESULT = "vol_result";
    private ArrayList<String> volunteer_meg;
    public static int mWinheight;
    int[] location = new int[2];
    private MagicScrollView mScrollView;
    private MagicTextView mIncomeTxt;
    private LinearLayout mContainer;

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

        Rect fram = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(fram);
        mWinheight = fram.height();
        mScrollView = (MagicScrollView) rootView.findViewById(R.id.magic_scroll);
        mContainer = (LinearLayout) rootView.findViewById(R.id.vol_home_text_parent);
        mIncomeTxt = (MagicTextView) rootView.findViewById(R.id.vol_my_work);
        mIncomeTxt.setValue(work_hour);
        initListener();
        mHandler.sendEmptyMessage(0);

        return rootView;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int height = mContainer.getMeasuredHeight();
            Log.d("height  is ====>", "" + height);
            onMeasureTxt(mIncomeTxt);
            mScrollView.sendScroll(MagicScrollView.UP, 0);
        }
    };

    private void initListener() {
        mScrollView.AddListener(mIncomeTxt);
    }

    private void onMeasureTxt(MagicTextView view) {
        // 用来获取view在距离屏幕顶端的距离(屏幕顶端也是scrollView的顶端)
        view.getLocationInWindow(location);
        view.setLocHeight(location[1]);
        Log.d("window y is ====>", "" + location[1]);
    }

}
