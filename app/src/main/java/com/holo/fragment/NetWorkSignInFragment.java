package com.holo.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holo.helloustb.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class NetWorkSignInFragment extends Fragment {

    public NetWorkSignInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_net_work_sign_in, container, false);
    }
}
