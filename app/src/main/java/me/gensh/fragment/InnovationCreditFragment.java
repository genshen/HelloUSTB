package me.gensh.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.gensh.helloustb.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InnovationCreditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnovationCreditFragment extends Fragment {
    private static final String ARG_PARAM = "ARG_PARAM_RECORD";
    private String mParam1;

    public InnovationCreditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param Parameter 1.
     * @return A new instance of fragment InnovationCreditFragment.
     */
    public static InnovationCreditFragment newInstance(String param) {
        InnovationCreditFragment fragment = new InnovationCreditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_innovation_credit, container, false);
    }

}
