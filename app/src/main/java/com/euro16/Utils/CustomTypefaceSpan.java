package com.euro16.Utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

import com.euro16.R;

public class CustomTypefaceSpan extends TypefaceSpan {

    private final Typeface newType;
    private Context context;

    public CustomTypefaceSpan(Context context, String family, Typeface type) {
        super(family);
        this.context = context;
        newType = type;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(context, ds, newType);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(context, paint, newType);
    }

    private static void applyCustomTypeFace(Context context, Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
        paint.setTextSize(50);
        paint.setColor(context.getResources().getColor(R.color.black));
    }
}
