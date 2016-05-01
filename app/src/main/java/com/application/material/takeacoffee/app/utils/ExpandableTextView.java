package com.application.material.takeacoffee.app.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.application.material.takeacoffee.app.R;

/**
 * Created by davide on 01/05/16.
 */
public class ExpandableTextView extends TextView implements View.OnClickListener {
    private static final int DEFAULT_TRIM_LENGTH = 100;
    private static final String ELLIPSIS = ".....";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH);
        typedArray.recycle();

        setOnClickListener(this);
    }

    /**
     * //TODO animate it
     */
    private void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    /**
     *
     * @return
     */
    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText(text);
        bufferType = type;
        setText();
    }

    /**
     *
     * @param text
     * @return
     */
    private CharSequence getTrimmedText(CharSequence text) {
        if (originalText != null && originalText.length() > trimLength) {
            return new SpannableStringBuilder(originalText, 0, trimLength + 1).append(ELLIPSIS);
        } else {
            return originalText;
        }
    }

    /**
     *
     * @return
     */
    public CharSequence getOriginalText() {
        return originalText;
    }

    /**
     *
     * @param trimLength
     */
    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        trimmedText = getTrimmedText(originalText);
        setText();
    }

    /**
     *
     * @return
     */
    public int getTrimLength() {
        return trimLength;
    }

    @Override
    public void onClick(View v) {
        toggleEllipsize();
    }

    /**
     * togglo ellipsize - show or hide textview ellipsize
     */
    public void toggleEllipsize() {
        trim = !trim;
        setText();
        requestFocusFromTouch();

    }
}