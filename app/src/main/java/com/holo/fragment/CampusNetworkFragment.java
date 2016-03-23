package com.holo.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.holo.helloustb.R;
import com.holo.sdcard.SdCardPro;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CampusNetworkFragment extends Fragment {
    static final String filepath = SdCardPro.getSDPath() + "/MyUstb/Data/flow.b";
    static final int DATA_LENGTH = 8; // >=2
    float today_flow = 0f;
    AppCompatTextView flow_chat_value;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ArrayList<String> args = getArguments().getStringArrayList("Campus_network_information");
        View rootView = inflater.inflate(R.layout.fragment_campus_network, container, false);
        if (args != null && args.size() == 11) {
            today_flow = Float.parseFloat(getStringValue(args.get(9)));
            ((AppCompatTextView) rootView.findViewById(R.id.campus_network_account_value)).setText(
                    getActivity().getString(R.string.campus_network_account_value,
                            getStringValue(args.get(1)), getStringValue(args.get(0))));
            ((AppCompatTextView) rootView.findViewById(R.id.campus_network_money_value)).setText(
                    getActivity().getString(R.string.campus_network_money_value, getStringValue(args.get(7))));
            ((AppCompatTextView) rootView.findViewById(R.id.campus_network_flow_v4)).setText(
                    getActivity().getString(R.string.campus_network_flow_value_v4, getStringValue(args.get(9))));
            ((AppCompatTextView) rootView.findViewById(R.id.campus_network_flow_v6)).setText(
                    getActivity().getString(R.string.campus_network_flow_value_v6, getStringValue(args.get(10))));
        }

        LineChartView mChart = (LineChartView) rootView.findViewById(R.id.campus_network_chart);
        Calendar calendar_now = Calendar.getInstance();
        final String[] mLabels = getWeekAxis(calendar_now);
        final float[] mValues = calcVariance(calendar_now, today_flow);

        flow_chat_value = (AppCompatTextView) rootView.findViewById(R.id.chart_view_flow_value);
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

    private String getStringValue(String str) {
        String[] s = str.split(":");
        if (s.length == 2) {
            return s[1];
        }
        return "";
    }

    DecimalFormat decimalFormat = new DecimalFormat("0.00");

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


    private static float[] calcVariance(Calendar calendar_now, float today_flow) {
        float[] data = M(calendar_now, today_flow);
        float[] variance = new float[DATA_LENGTH - 1];
        int day_counter = calendar_now.get(Calendar.DAY_OF_MONTH) - 1;
        for (int i = DATA_LENGTH - 2; i >= 0; i--) {
            if (day_counter != 0) {
                variance[i] = data[i + 1] - data[i];
            } else {
                variance[i] = data[i];
            }
            day_counter--;
        }
        return variance;
    }

    public static float[] M(Calendar calendar_now, float todayFlow) {
        float[] flowData = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
        File file = new File(filepath);
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
                flowData[i] = todayFlow;
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
