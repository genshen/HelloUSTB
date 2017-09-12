package me.gensh.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gensh.helloustb.ELearningCategory;
import me.gensh.helloustb.R;

/**
 * created by gensh on 2027/08/28
 */
public class DashboardFragment extends Fragment {
    @BindView(R.id.grid_view_dashboard)
    GridView gridViewDashboard;

    final static String GRID_VIEW_ITEM_ICON = "icon", GRID_VIEW_ITEM_TITLE = "title";

    ArrayList<Map<String, Object>> dataList = new ArrayList<>();
    int[] resIcons = {R.drawable.guide_score, R.drawable.guide_more, R.drawable.guide_score, R.drawable.guide_score, R.drawable.guide_wifi,
            R.drawable.guide_score, R.drawable.guide_score, R.drawable.guide_score, R.drawable.guide_score,};
    String[] resTitles = {"成绩查询", "创新学分", "本科教学网通知", "课程表", "校园网",
            "校园卡消费", "图书馆", "志愿服务", "考试查询"};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);

        SimpleAdapter adapter = new SimpleAdapter(getContext(), getData(), R.layout.grid_view_dashboard,
                new String[]{GRID_VIEW_ITEM_ICON, GRID_VIEW_ITEM_TITLE}, new int[]{R.id.grid_view_dashboard_icon, R.id.grid_view_dashboard_title});
        gridViewDashboard.setAdapter(adapter);
        gridViewDashboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ELearningCategory.class); //todo
                startActivity(intent);
            }
        });
        return view;
    }

    private List<Map<String, Object>> getData() {
        for (int i = 0; i < resIcons.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put(GRID_VIEW_ITEM_ICON, resIcons[i]);
            map.put(GRID_VIEW_ITEM_TITLE, resTitles[i]);
            dataList.add(map);
        }
        return dataList;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
