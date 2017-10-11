package me.gensh.views;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gensh on 2017/10/10.
 * <p>
 * from: https://gist.github.com/adelnizamutdinov/31c8f054d1af4588dc5c
 * and https://stackoverflow.com/questions/27414173/equivalent-of-listview-setemptyview-in-recyclerview/27801394#27801394
 */

public class RecyclerViewWithEmptySupport extends RecyclerView {
    private View emptyView;

    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public RecyclerViewWithEmptySupport(Context context) {
        super(context);
    }

    public RecyclerViewWithEmptySupport(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithEmptySupport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    /**
     * add emptyViewLayout to view whose id is parentViewID.
     *
     * @param emptyViewLayout empty view layout
     * @param parent          root view
     * @param attachToRoot    attachToRoot
     */
    public void setEmptyView(@LayoutRes int emptyViewLayout, ViewGroup parent, boolean attachToRoot) {
        View view = LayoutInflater.from(getContext()).inflate(emptyViewLayout, parent, attachToRoot);
        if (!attachToRoot) {
            parent.addView(view);
        }
        this.emptyView = view;
        checkIfEmpty();
    }
}
