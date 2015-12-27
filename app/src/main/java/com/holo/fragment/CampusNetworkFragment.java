package com.holo.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holo.helloustb.R;

public class CampusNetworkFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle args = getArguments();

        View rootView = inflater.inflate(R.layout.fragment_campus_network, container,false);
        ((TextView) rootView.findViewById(R.id.network_information)).setText(
                "校园网登录成功\n\n我的校园网信息：\n"+args.getStringArrayList("Campus_network_information"));

        return rootView;
    }

}
