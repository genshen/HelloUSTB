package me.gensh.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.*;
import android.view.View;

/**
 * @author gensh
 */
public class GuidePagerAdapter extends PagerAdapter {
    private List<View> views;
    private Context context;

    public GuidePagerAdapter(List<View> views, Context context) {
        this.views = views;
        this.context = context;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(views.get(position));
        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

}