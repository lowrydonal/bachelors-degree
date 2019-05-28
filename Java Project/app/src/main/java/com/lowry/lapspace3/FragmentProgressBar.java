package com.lowry.lapspace3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.victor.loading.newton.NewtonCradleLoading;
import com.victor.loading.rotate.RotateLoading;

/**
 * FRAGMENT: PROGRESS BAR
 */

public class FragmentProgressBar extends Fragment {

    private RotateLoading progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_bar, container, false);
        progressBar = (RotateLoading) view.findViewById(R.id.rotating_progress_bar);
        progressBar.start();

        return view;
    }
}
