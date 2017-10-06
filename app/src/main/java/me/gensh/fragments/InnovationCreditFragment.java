package me.gensh.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.gensh.helloustb.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InnovationCreditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnovationCreditFragment extends Fragment {
    private static final String ARG_PARAM_INNOVATION_CREDIT = "ARG_PARAM_INNOVATION_CREDIT";
    private OnInnovationCreditLoadListener mListener;
    List<Map<String, Object>> listItems = new ArrayList<>();
    float creditSum = 0;

    public InnovationCreditFragment() {
        // Required empty public constructor
    }

    /**
     * @param param data from http response in its parent activity
     * @return A new instance of fragment InnovationCreditFragment.
     */
    public static InnovationCreditFragment newInstance(ArrayList<String> param) {
        InnovationCreditFragment fragment = new InnovationCreditFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM_INNOVATION_CREDIT, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            final ArrayList<String> innovationCreditQueryResult = getArguments().getStringArrayList(ARG_PARAM_INNOVATION_CREDIT);
            if (innovationCreditQueryResult != null) {
                int size = innovationCreditQueryResult.size();
                for (int i = 0; i < size / 4; i++) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("type", innovationCreditQueryResult.get(i * 4));
                    m.put("title", innovationCreditQueryResult.get(i * 4 + 1));
                    m.put("credit", innovationCreditQueryResult.get(i * 4 + 2));
                    m.put("time", innovationCreditQueryResult.get(i * 4 + 3));
                    listItems.add(m);
                    creditSum += Float.parseFloat(innovationCreditQueryResult.get(i * 4 + 2));
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_innovation_credit, container, false);
        ListView innovationCreditList = view.findViewById(R.id.innovation_credit_list);
        SimpleAdapter home_adapter = new SimpleAdapter(getActivity(), listItems, R.layout.listview_innovation_credit,
                new String[]{"type", "title", "credit", "time"},
                new int[]{R.id.innovation_credit_type, R.id.innovation_credit_title,
                        R.id.innovation_credit_credit, R.id.innovation_credit_time});
        innovationCreditList.setAdapter(home_adapter);
        if (listItems == null || listItems.size() == 0) {
            Toast.makeText(getActivity(), R.string.no_innovation_credit_now, Toast.LENGTH_LONG).show();
        }

        mListener.onInnovationCreditCalculated(creditSum); //mListener must not be null
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInnovationCreditLoadListener) {
            mListener = (OnInnovationCreditLoadListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnInnovationCreditLoadListener");
        }
    }

    /**
     * for pre-lollipop version
     * see <a>https://stackoverflow.com/questions/32604552/onattach-not-called-in-fragment</a>  for more details.
     *
     * @param activity container activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (activity instanceof OnInnovationCreditLoadListener) {
                mListener = (OnInnovationCreditLoadListener) activity;
            } else {
                throw new RuntimeException(activity.toString() + " must implement OnInnovationCreditLoadListener");
            }
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
    public interface OnInnovationCreditLoadListener {
        void onInnovationCreditCalculated(float creditSum);
    }

}
