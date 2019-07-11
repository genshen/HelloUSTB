package me.gensh.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import me.gensh.utils.AnimHelper;

/**
 * Created by gensh on 2017/10/6.
 */

public class AvatarBehavior extends CoordinatorLayout.Behavior<CircularImageView> {

    private int appbarStartPoint;
    private boolean visible = true;

    public AvatarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircularImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircularImageView child, View dependency) {
        if (appbarStartPoint == 0)
            appbarStartPoint = dependency.getBottom();

        float expandedPercentageFactor = dependency.getBottom() / (appbarStartPoint * 1.0f);
        if (expandedPercentageFactor < 0.68f) {
//            child.setVisibility(View.INVISIBLE);
            if (visible) {
                AnimHelper.zoomOut(child, 250);
            }
            visible = false;
        } else {
//            child.setVisibility(View.VISIBLE);
            if (!visible) {
                AnimHelper.zoomIn(child, 250);
            }
            visible = true;
        }
        return true;
    }

}
