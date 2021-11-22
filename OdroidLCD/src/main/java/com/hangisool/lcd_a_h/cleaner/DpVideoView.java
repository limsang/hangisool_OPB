package com.hangisool.lcd_a_h.cleaner;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class DpVideoView extends VideoView {
    public DpVideoView(Context context) {
        super(context);
        init(context);
    }

    public DpVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DpVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}