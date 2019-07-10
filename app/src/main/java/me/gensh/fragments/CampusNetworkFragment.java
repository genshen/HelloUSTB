package me.gensh.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import me.gensh.helloustb.R;
import me.gensh.io.IOUtils;

public class CampusNetworkFragment extends Fragment {
    private static final String ARG_PARAM_CAMPUS_NETWORK = "ARG_PARAM_CAMPUS_NETWORK";

    static final int DATA_LENGTH = 8; // >=2
    float today_flow = 0f;
    ArrayList<String> args;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    AppCompatTextView flow_chat_value;

    public CampusNetworkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     **/
    public static CampusNetworkFragment newInstance(ArrayList<String> params) {
        CampusNetworkFragment fragment = new CampusNetworkFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM_CAMPUS_NETWORK, params);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            args = getArguments().getStringArrayList("Campus_network_information"); //init here.
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campus_network, container, false);
        if (args != null && args.size() == 5) {
            try {
                today_flow = Float.parseFloat(args.get(1)) / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((AppCompatTextView) rootView.findViewById(R.id.campus_network_account_value)).setText(
                    getActivity().getString(R.string.campus_network_account_value, getStringValue(args.get(4), 1)));
            ((AppCompatTextView) rootView.findViewById(R.id.campus_network_money_value)).setText(
                    getActivity().getString(R.string.campus_network_money_value, getStringValue(args.get(2), 10000)));
            ((AppCompatTextView) rootView.findViewById(R.id.campus_network_flow_v4)).setText(
                    getActivity().getString(R.string.campus_network_flow_value_v4, getStringValue(args.get(1), 1024)));
            ((AppCompatTextView) rootView.findViewById(R.id.campus_network_flow_v6)).setText(
                    getActivity().getString(R.string.campus_network_flow_value_v6, getStringValue(args.get(3), 4096)));
        }

        LineChartView mChart = rootView.findViewById(R.id.campus_network_chart);
        Calendar calendar_now = Calendar.getInstance();
        final String[] mLabels = getWeekAxis(calendar_now);
        final float[] mValues = calcVariance(calendar_now, today_flow);

        flow_chat_value = rootView.findViewById(R.id.chart_view_flow_value);
        setFlowChatValue(mValues[6], mLabels[6]);

        LineSet dataset = new LineSet(mLabels, mValues);
        dataset.setColor(Color.parseColor("#53c1bd"))
                .setThickness(2.5f)
                .setDotsRadius(5f)
                .setDotsColor(Color.parseColor("#FFFFFF"));
        mChart.addData(dataset);
        mChart.setClickablePointRadius(40.0f);

        mChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect entryRect) {
                setFlowChatValue(mValues[entryIndex], mLabels[entryIndex]);
            }
        });

        mChart.setBorderSpacing(1)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(true)
                .setYAxis(true)
                .setBorderSpacing(Tools.fromDpToPx(5));

        Animation anim = new Animation();
        mChart.show(anim);
        return rootView;
    }

    private String getStringValue(String str, int mod) {
        if (str == null) {
            return "";
        } else if (str.startsWith("\'") && str.endsWith("\'") && str.length() >= 2) {  // such as :  '41355000'
            return str.substring(1, str.length() - 1);
        } else {
            if (str.matches("[0-9]+")) {
//                DecimalFormat decimalFormat = new DecimalFormat(".00");
                return decimalFormat.format(Float.parseFloat(str) / mod);
            }
        }
        return "";
    }

    private void setFlowChatValue(float value, String tag) {
        String v = decimalFormat.format(value);
        flow_chat_value.setText(getActivity().getString(R.string.chart_view_flow_value, tag, v));
    }

    final String[] week = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
//    final String[] week = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    private String[] getWeekAxis(Calendar calendar_now) {
        String[] new_week = new String[DATA_LENGTH - 1];
        int w = calendar_now.get(Calendar.DAY_OF_WEEK);
        for (int i = 6; i >= 0; i--) {
            new_week[i] = week[(i + w) % 7];
        }
        return new_week;
    }

    private float[] calcVariance(Calendar calendar_now, float today_flow) {
        float[] data = M(calendar_now, today_flow);

        System.out.println("<<<<<<<<<<<<<" + today_flow);
        System.out.println(Arrays.toString(data));

        float[] variance = new float[DATA_LENGTH - 1];
        int day_counter = calendar_now.get(Calendar.DAY_OF_MONTH) - 1;
        for (int i = DATA_LENGTH - 2; i >= 0; i--) {
            if (day_counter != 0) {
                variance[i] = data[i + 1] - data[i];
            } else {
                variance[i] = data[i + 1];
            }
            day_counter--;
        }
        return variance;
    }

    public float[] M(Calendar calendar_now, float todayFlow) {
        float[] flowData = new float[]{0, 0, 0, 0, 0, 0, 0, 0};

        File file = new File(getActivity().getFilesDir(), IOUtils.FLOW_STORE_FILE_PATH);
        if (file.exists() && file.length() == 38) {
            try {
                RandomAccessFile random_f = new RandomAccessFile(file, "rw");
                for (int i = 0; i < DATA_LENGTH; i++) {
                    flowData[i] = random_f.readFloat();
                }
                setData(calendar_now, random_f.readInt(), random_f.readByte(), random_f.readByte(), flowData, todayFlow);
                // write data back
                random_f.seek(0);
                for (int i = 0; i < DATA_LENGTH; i++) {
                    random_f.writeFloat(flowData[i]);
                }
                random_f.writeInt(calendar_now.get(Calendar.YEAR));
                random_f.writeByte(calendar_now.get(Calendar.MONTH));
                random_f.writeByte(calendar_now.get(Calendar.DAY_OF_MONTH));
                random_f.close();
                return flowData;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (file.createNewFile()) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    for (int i = 0; i < DATA_LENGTH; i++) {
                        randomAccessFile.writeFloat(todayFlow);
                        flowData[i] = todayFlow;
                    }
                    randomAccessFile.writeInt(calendar_now.get(Calendar.YEAR));
                    randomAccessFile.writeByte(calendar_now.get(Calendar.MONTH));
                    randomAccessFile.writeByte(calendar_now.get(Calendar.DAY_OF_MONTH));
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flowData;
    }

    private static void setData(Calendar calendar_now, int year_old, byte month_old, byte days_old,
                                float[] flowData, float todayFlow) {
        Calendar calendar_old = Calendar.getInstance();
        calendar_old.set(year_old, month_old, days_old);
        int days = (int) (calendar_now.getTimeInMillis() / 86400000 - calendar_old.getTimeInMillis() / 86400000);
        days = days < 0 ? 0 : days;
        float last_data = flowData[DATA_LENGTH - 1];
        for (int i = days; i < DATA_LENGTH; i++) {
            flowData[i - days] = flowData[i];
        }
        if (calendar_now.get(Calendar.YEAR) == year_old && calendar_now.get(Calendar.MONTH) == month_old) {
            int index = DATA_LENGTH - days - 1;
            index = index < 0 ? 0 : index;
            for (int i = index; i < DATA_LENGTH; i++) {
                flowData[i] = todayFlow;//
            }
        } else {
            int index = DATA_LENGTH - days;
            index = index < 0 ? 0 : index;
            int day_today = calendar_now.get(Calendar.DAY_OF_MONTH);
            for (int i = DATA_LENGTH - 1; i >= index; i--) {
                if (day_today > 0) {
                    flowData[i] = todayFlow;
                    day_today--;
                } else {
                    flowData[i] = last_data;
                }
            }
        }
    }

}
