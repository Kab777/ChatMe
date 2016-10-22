package com.junyu.IMBudget.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.junyu.IMBudget.R;

/**
 * Created by Junyu on 10/17/2016.
 */

public class CpTextView extends TextView {
    public CpTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CpTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CpTextView(Context context) {
        super(context);
        init(null);
    }

    //accept string from xml attribute, and set it here
    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CpTextView);
            String fontName = a.getString(R.styleable.CpTextView_font);
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }
}
