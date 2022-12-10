package com.cdh.bebetter.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class FontIconView extends TextView {
    public FontIconView(Context context) {
        super(context);
        initIcon(context);
    }

    public FontIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initIcon(context);
    }

    public FontIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initIcon(context);
    }

    private void initIcon(Context context){
        Typeface font = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
        this.setTypeface(font);
    }
}
