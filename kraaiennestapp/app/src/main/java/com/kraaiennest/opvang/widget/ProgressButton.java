package com.kraaiennest.opvang.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.kraaiennest.opvang.R;

public class ProgressButton extends RelativeLayout {

    private AttributeSet attrs;
    private int defStyleAttr;
    private LottieAnimationView progressBar;
    private TextView buttonTextView;

    public ProgressButton(Context context) {
        super(context);
        init();
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        init();
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
        init();
    }

    public void init() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.progress_button, this, true);
        buttonTextView = root.findViewById(R.id.button_text);
        progressBar = root.findViewById(R.id.progress_indicator);
        loadAttr(attrs, defStyleAttr);
    }


    private void loadAttr(AttributeSet attrs, int defStyleAttr) {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressButton, defStyleAttr, 0);

        String buttonText = arr.getString(R.styleable.ProgressButton_text);
        boolean loading = arr.getBoolean(R.styleable.ProgressButton_loading, false);
        boolean enabled = arr.getBoolean(R.styleable.ProgressButton_enabled, true);
        int lottieResId = arr.getResourceId(R.styleable.ProgressButton_lottie_resId, R.raw.lottie_button_loader);
        arr.recycle();
        setEnabled(enabled);
        buttonTextView.setEnabled(enabled);
        setText(buttonText);
        progressBar.setAnimation(lottieResId);
        setLoading(loading);
    }

    public void setLoading(boolean loading) {
        setClickable(!loading); //Disable clickable when loading
        if (loading) {
            buttonTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            buttonTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setText(String text) {
        buttonTextView.setText(text);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        buttonTextView.setEnabled(enabled);
    }
}
