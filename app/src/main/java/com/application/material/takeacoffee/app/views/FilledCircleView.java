package com.application.material.takeacoffee.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.application.material.takeacoffee.app.R;

/**
 * Created by davide on 02/12/14.
 */
public class FilledCircleView extends View {

    private final String TAG = "FilledCircleView";
    private Bitmap coffeeCupBitmap;
    private PointF center = new PointF();
    private RectF circleRect = new RectF();
    private Path segment = new Path();
    private Paint strokePaint = new Paint();
    private Paint fillPaint = new Paint();

    private int strokeColor;
    private float strokeWidth;
    private double value;
    private int fillColor;
    private float radius;
    private static final double MIN_VALUE = 0;
    private static final double MAX_VALUE = 100;
    private Path path = new Path();
    private double width;
    private double height;
    private double diagonalWidth;


    public FilledCircleView(Context context) {
        super(context);
    }

    public FilledCircleView(Context context, AttributeSet set) {
        super(context, set);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                set,
                R.styleable.FilledCircleView,
                0,
                0);
        try {
            fillColor = attributes.getColor(R.styleable.FilledCircleView_fillColor,
                    Color.WHITE);
            strokeColor = attributes.getColor(R.styleable.FilledCircleView_strokeColor,
                    Color.BLACK);
            strokeWidth = attributes.getFloat(R.styleable.FilledCircleView_strokeWidth,
                    1f);
            value = attributes.getInteger(R.styleable.FilledCircleView_value,
                    20);

            width = 400;
            height = 700;
            diagonalWidth = 100;

            adjustValue(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            attributes.recycle();
        }

        Log.d(TAG, "" + fillColor);
        Log.d(TAG, "" + strokeColor);
        Log.d(TAG, "" + strokeWidth);
        Log.d(TAG, "" + value);
        fillPaint.setColor(fillColor);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setStyle(Paint.Style.STROKE);

        coffeeCupBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.coffee_cup_icon);

    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
        fillPaint.setColor(fillColor);
        invalidate();
    }
    public int getFillColor() {
        return this.fillColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        strokePaint.setColor(strokeColor);
        invalidate();

    }

    public int getStrokeColor() {
        return this.strokeColor;
    }


    public double getValue() {
        return value;
    }

    public void setValue(int value) {
        adjustValue(value);
        setPolygonPaths();

        invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        strokePaint.setStrokeWidth(strokeWidth);
        invalidate();
    }

    private void adjustValue(double value) {
        this.value = Math.min(MAX_VALUE, Math.max(MIN_VALUE, value));
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        center.x = getWidth() / 2;
        center.y = getHeight() / 2;
        radius = Math.min(getWidth(), getHeight()) / 2 - strokeWidth;
        circleRect.set(center.x - radius, center.y - radius,
                center.x + radius, center.y + radius);
        this.setPolygonPaths();
    }

    private void setPaths() {
        float y = center.y + radius - (float) (2 * radius * value / 100 - 1);
        float x = center.x - (float) Math.sqrt(Math.pow(radius, 2) -
            Math.pow(y - center.y, 2));

        float angle = (float) Math.toDegrees(Math.
                atan((center.y - y) / (x - center.x)));
        float startAngle = 180 - angle;
        float sweepAngle = 2 * angle - 180;

        segment.rewind();
        segment.addArc(circleRect, startAngle, sweepAngle);
        segment.close();
    }

    private void setPolygonPaths() {
        float w = (float) width;
        float h= (float) (height * value / 100 - 1); //inverse animation from bottom to up
        float d = (float) (diagonalWidth * value / 100 - 1);

        //draw poligone
        Point points[] = {
                new Point(0, 0),
                new Point(w + (2 * d), 0),
                new Point(w + d, h),
                new Point(d, h)
        };

        segment.rewind();
        segmentDrawPolygon(points);
        segment.close();
    }
        @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(segment, fillPaint);
//        canvas.drawPath(path, fillPaint);
        canvas.drawBitmap(coffeeCupBitmap, null, circleRect, strokePaint);
//        canvas.drawCircle(center.x, center.y, radius, strokePaint);
    }

    public void segmentDrawPolygon(Point[] points) {
        if(points == null || points.length < 2){
            Log.e(TAG, "error");
            return;
        }

        segment.moveTo(points[0].x, points[0].y);
        for(int i = 1; i < points.length; i ++) {
            segment.lineTo(points[i].x, points[i].y);
        }

        segment.lineTo(points[0].x, points[0].y);
    }

    public class Point {
        public float x, y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
