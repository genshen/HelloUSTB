package me.gensh.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import me.gensh.helloustb.R;

/**
 * Created by gensh on 2017/9/10.
 * based on @author JustMe, {@link <a>http://blog.csdn.net/tianxiangshan/article/details/7956488</a>}
 */

public class WrapContentListView extends LinearLayout {
    private BaseAdapter adapter;
    private OnItemClickListener onItemClickListener;

    public WrapContentListView(Context context) {
        super(context);
        initAttr(null);
    }

    public WrapContentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
    }

    public void notifyChange() {
        int count = getChildCount();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        for (int i = count; i < adapter.getCount(); i++) {
            final int index = i;
            final LinearLayout layout = new LinearLayout(getContext());
            layout.setLayoutParams(params);
            layout.setOrientation(VERTICAL);
            View v = adapter.getView(i, null, null);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(WrapContentListView.this, layout, index, adapter.getItem(index));
                    }
                }
            });
            // 每个条目下面的线
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(R.color.gray2);
            imageView.setLayoutParams(params);
            layout.addView(v);
            layout.addView(imageView);
            addView(layout, index);
        }
    }

    /**
     * 设置方向
     *
     * @param attrs
     */
    public void initAttr(AttributeSet attrs) {
        setOrientation(VERTICAL);
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    /**
     * 设置adapter并模拟listview添加数据
     *
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        notifyChange();
    }

    /**
     * 设置条目监听事件
     *
     * @param onClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    /**
     * 点击事件监听
     *
     * @author JustMe
     */
    public static interface OnItemClickListener {
        public void onItemClick(ViewGroup parent, View view, int position, Object o);
    }
}