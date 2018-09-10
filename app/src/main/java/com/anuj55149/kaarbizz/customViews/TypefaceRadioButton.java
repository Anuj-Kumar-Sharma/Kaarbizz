package com.anuj55149.kaarbizz.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.anuj55149.kaarbizz.R;

public class TypefaceRadioButton extends AppCompatRadioButton {

    private static LruCache<String, Typeface> sTypefaceCache =
            new LruCache<String, Typeface>(12);

    public TypefaceRadioButton(Context context) {
        super(context);
    }

    public TypefaceRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Typeface, 0, 0);

        try {
            String typefaceName = a.getString(
                    R.styleable.Typeface_typeface);

            if (!isInEditMode() && !TextUtils.isEmpty(typefaceName)) {
                Typeface typeface = sTypefaceCache.get(typefaceName);

                if (typeface == null) {
                    typeface = Typeface.createFromAsset(context.getAssets(),
                            String.format("fonts/%s.otf", typefaceName));


                    // Cache the Typeface object
                    sTypefaceCache.put(typefaceName, typeface);
                }
                setTypeface(typeface);

                // Note: This flag is required for proper typeface rendering
                setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
        } finally {
            a.recycle();
        }
    }
}
