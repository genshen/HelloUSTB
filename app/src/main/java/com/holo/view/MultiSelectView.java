package com.holo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.holo.helloustb.R;

/**
 * Created by 根深 on 2016/3/25.
 */
public class MultiSelectView extends View {
    private long S = 0;
    private int MAX_LENGTH = 64;
    private Paint paint;
    private RectF rectf;
    private TextPaint textPaint;
    OnSelectedListener mOnSelectedListener;
    //attrs
    private int itemTextColor, itemBackgroundColor, itemSelectedColor;
    private int itemCount, rowNum;
    private float itemWidth, itemHeight, itemTextSize, fontHeight, mRadius, itemPadding;

    public MultiSelectView(Context context) {
        super(context);
        init(null);
    }

    public MultiSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MultiSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        itemCount = 0;
        rowNum = 4;
        itemTextColor = 0xFF666666;
        itemTextSize = density * 16;
        itemBackgroundColor = 0xFFFFFFFF;
        itemSelectedColor = 0xFF000000;
        mRadius = 0.0f;

        TypedArray ta = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.multiSelectView);
        if (ta != null) {
            itemCount = ta.getInt(R.styleable.multiSelectView_msvItemCount, itemCount);
            rowNum = ta.getInt(R.styleable.multiSelectView_msvRowNum, rowNum);
            itemHeight = ta.getDimension(R.styleable.multiSelectView_msvItemHeight, 0.0f);
            itemTextColor = ta.getColor(R.styleable.multiSelectView_msvItemTextColor, itemTextColor);
            itemTextSize = ta.getDimension(R.styleable.multiSelectView_msvItemTextSize, itemTextSize);
            itemBackgroundColor = ta.getColor(R.styleable.multiSelectView_msvItemBackgroundColor, itemBackgroundColor);
            itemSelectedColor = ta.getColor(R.styleable.multiSelectView_msvItemSelectedColor, itemSelectedColor);
            mRadius = ta.getDimension(R.styleable.multiSelectView_msvItemRadius, mRadius);
            itemPadding = ta.getDimension(R.styleable.multiSelectView_msvItemPadding, mRadius);
            ta.recycle();
        }

        rectf = new RectF();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(itemTextSize);
        textPaint.setColor(itemTextColor);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        fontHeight = Math.abs(fm.top + fm.bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int tempWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        itemWidth = tempWidth / rowNum;
        itemHeight = itemHeight == 0.0f ? itemWidth : itemHeight;
        setMeasuredDimension(tempWidth, measureHeight(itemHeight, heightMeasureSpec));
    }

    private int measureHeight(float itemHeight, int measureSpec) {
        int result = (int) ((itemCount + rowNum - 1) / rowNum * itemHeight + 0.5f);
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left, top;
        for (int i = 0; i < itemCount && i < MAX_LENGTH; i++) {
            left = (i % rowNum) * itemWidth;
            top = (i / rowNum) * itemHeight;
            rectf.set(left + itemPadding, top + itemPadding, left + itemWidth - itemPadding, top + itemHeight - itemPadding);
            if ((S >> i & 1) == 0) {
                paint.setColor(itemBackgroundColor);
            } else {
                paint.setColor(itemSelectedColor);
            }
            canvas.drawRoundRect(rectf, mRadius, mRadius, paint);
            canvas.drawText((i+1) + "", left + itemWidth / 2, top + (itemHeight + fontHeight) / 2, textPaint);
        }
    }

    int selectPosition = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (itemCount == 0 || rowNum == 0 || !isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                selectPosition = getPosition(event.getX(), event.getY());
                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.v("i", "ACTION_MOVE");
//                break;
            case MotionEvent.ACTION_UP:
                int position = getPosition(event.getX(), event.getY());
                selectIndex(position, true);
                break;
        }
        return true;
    }

    private int getPosition(float x, float y) {
        int _x = (int) (x / itemWidth);
        int _y = (int) (y / itemHeight);
        return rowNum * _y + _x; //must  >=0 ?
    }

    private void selectResponse(int position, boolean selected) {
        if (null != mOnSelectedListener) {
            mOnSelectedListener.onItemSelect(this, position, selected);
        }
    }

    public interface OnSelectedListener {
        void onItemSelect(MultiSelectView wheelView, int position, boolean selected);
    }

    public void selectIndex(int position, boolean post) {
        if (position == selectPosition && selectPosition < MAX_LENGTH) {
            long temp = 1 << position;
            if ((temp & S) == 0) {
                S |= temp;
                selectResponse(position, true);
            } else {
                temp = ~temp;
                S &= temp;
                selectResponse(position, false);
            }

            if (post) {
                postInvalidate();
            } else {
                invalidate();
            }
        }
    }

    public void setSelect(long S) {
        this.S = S;
        invalidate();
    }

    public long getSelected() {
        return S;
    }

    public void setOnSelectedListener(OnSelectedListener callback) {
        mOnSelectedListener = callback;
    }
}
