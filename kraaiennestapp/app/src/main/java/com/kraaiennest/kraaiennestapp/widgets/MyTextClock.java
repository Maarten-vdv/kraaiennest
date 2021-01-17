package com.kraaiennest.kraaiennestapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextClock;

public class MyTextClock extends TextClock {

    public MyTextClock(Context context) {
        super(context);
    }

    public MyTextClock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyTextClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        try {
            super.onAttachedToWindow();
        } catch(Exception e)  {
            // fix rendering issue
        }
    }

}
