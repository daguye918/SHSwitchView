package com.seavenheaven.iosswitch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.util.Property;

/**
 * Created by 7heaven on 15/3/14.
 */
public class ShSwitchView extends View {

    private ObjectAnimator innerContentAnimator;
    private Property<ShSwitchView, Float> innerContentProperty = new Property<ShSwitchView, Float>(Float.class, "innerBound"){
        @Override
        public void set(ShSwitchView sv, Float innerContentRate){
            sv.setInnerContentRate(innerContentRate);
        }

        @Override
        public Float get(ShSwitchView sv){
            return sv.getInnerContentRate();
        }
    };

    private ObjectAnimator knobExpandAnimator;
    private Property<ShSwitchView, Float> knobExpandProperty = new Property<ShSwitchView, Float>(Float.class, "knobExpand"){
        @Override
        public void set(ShSwitchView sv, Float knobExpandRate){
            sv.setKnobExpandRate(knobExpandRate);
        }

        @Override
        public Float get(ShSwitchView sv){
            return sv.getKnobExpandRate();
        }
    };

    private ObjectAnimator knobMoveAnimator;
    private Property<ShSwitchView, Float> knobMoveProperty = new Property<ShSwitchView, Float>(Float.class, "knobMove"){
        @Override
        public void set(ShSwitchView sv, Float knobMoveRate){
            sv.setKnobMoveRate(knobMoveRate);
        }

        @Override
        public Float get(ShSwitchView sv){
            return sv.getKnobMoveRate();
        }
    };

    private GestureDetector gestureDetector;
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onDown(MotionEvent event){

            return true;
        }

        @Override
        public void onShowPress(MotionEvent event){



            innerContentAnimator = ObjectAnimator.ofFloat(ShSwitchView.this, innerContentProperty, innerContentRate, 0.0F);
            innerContentAnimator.setDuration(300L);
            innerContentAnimator.setInterpolator(new DecelerateInterpolator());

            innerContentAnimator.start();

            knobExpandAnimator = ObjectAnimator.ofFloat(ShSwitchView.this, knobExpandProperty, knobExpandRate, 1.0F);
            knobExpandAnimator.setDuration(300L);
            knobExpandAnimator.setInterpolator(new DecelerateInterpolator());

            knobExpandAnimator.start();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event){



            if(!isOn){
                innerContentAnimator = ObjectAnimator.ofFloat(ShSwitchView.this, innerContentProperty, innerContentRate, 1.0F);
                innerContentAnimator.setDuration(300L);
                innerContentAnimator.setInterpolator(new DecelerateInterpolator());

                innerContentAnimator.start();
            }

            knobExpandAnimator = ObjectAnimator.ofFloat(ShSwitchView.this, knobExpandProperty, knobExpandRate, 0.0F);
            knobExpandAnimator.setDuration(300L);
            knobExpandAnimator.setInterpolator(new DecelerateInterpolator());

            knobExpandAnimator.start();

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if(e2.getX() > centerX){
                if(!isOn){
                    knobMoveAnimator = ObjectAnimator.ofFloat(ShSwitchView.this, knobMoveProperty, knobMoveRate, 1.0F);
                    knobMoveAnimator.setDuration(300L);
                    knobMoveAnimator.setInterpolator(new DecelerateInterpolator());
                    isOn = !isOn;

                    knobMoveAnimator.start();

                    innerContentAnimator = ObjectAnimator.ofFloat(ShSwitchView.this, innerContentProperty, innerContentRate, 0.0F);
                    innerContentAnimator.setDuration(300L);
                    innerContentAnimator.setInterpolator(new DecelerateInterpolator());

                    innerContentAnimator.start();
                }
            }else{
                if(isOn){
                    knobMoveAnimator = ObjectAnimator.ofFloat(ShSwitchView.this, knobMoveProperty, knobMoveRate, 0.0F);
                    knobMoveAnimator.setDuration(300L);
                    knobMoveAnimator.setInterpolator(new DecelerateInterpolator());
                    isOn = !isOn;

                    knobMoveAnimator.start();


                }
            }

            return true;
        }
    };


    private static final int intrinsicWidth = 0;
    private static final int intrinsicHeight = 0;

    private int width;
    private int height;

    private int centerX;
    private int centerY;

    private float cornerRadius;

    private int shadowSpace = 10;
    private int outerStrokeWidth = 2;

    private RectF knobBound;
    private float knobMaxExpandWidth;
    private float intrinsicKnobWidth;
    private float knobExpandRate;
    private float knobMoveRate;

    private boolean isOn;

    private RectF innerContentBound;
    private float innerContentRate;
    private float intrinsicInnerWidth;
    private float intrinsicInnerHeight;

    private int tintColor;
    private static final int backgroundColor = 0xFFCCCCCC;
    private int colorStep = backgroundColor;
    private float colorStepRate;
    private static final int foregroundColor = 0xFFEFEFEF;

    private Paint paint;

    private RectF ovalForPath;
    private Path roundRectPath;

    public ShSwitchView(Context context){
        this(context, null);
    }

    public ShSwitchView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public ShSwitchView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ShSwitchView);

        tintColor = ta.getColor(R.styleable.ShSwitchView_tintColor, /*0xFF9CE949*/ 0xFF0099CC);

        knobBound = new RectF();
        innerContentBound = new RectF();
        ovalForPath = new RectF();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roundRectPath = new Path();

        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);

        if(Build.VERSION.SDK_INT >= 11){
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


    }

    void setInnerContentRate(float rate){
        this.innerContentRate = rate;

        float w = (float) intrinsicInnerWidth / 2.0F * rate;
        float h = (float) intrinsicInnerHeight / 2.0F * rate;

        this.innerContentBound.left = centerX - w;
        this.innerContentBound.top = centerY - h;
        this.innerContentBound.right = centerX + w;
        this.innerContentBound.bottom = centerY + h;

        invalidate();
    }

    float getInnerContentRate(){
        return this.innerContentRate;
    }

    void setKnobExpandRate(float rate){
        this.knobExpandRate = rate;

        float w = intrinsicKnobWidth + (float) (knobMaxExpandWidth - intrinsicKnobWidth) * rate;

        boolean left = knobBound.left + knobBound.width() / 2 > centerX;

        if(left){
            knobBound.left = knobBound.right - w;
        }else{
            knobBound.right = knobBound.left + w;
        }

        invalidate();
    }

    float getKnobExpandRate(){
        return this.knobExpandRate;
    }

    void setKnobMoveRate(float rate){
        this.knobMoveRate = rate;

        float kw = knobBound.width();
        float w = (float) (width - kw - ((shadowSpace + outerStrokeWidth) * 2)) * rate;

        this.colorStep = colorTransform(rate, backgroundColor, tintColor);


        knobBound.left = shadowSpace + outerStrokeWidth + w;
        knobBound.right = knobBound.left + kw;

        invalidate();
    }

    float getKnobMoveRate(){
        return knobMoveRate;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        centerX = width / 2;
        centerY = height / 2;

        cornerRadius = centerY - shadowSpace;

        innerContentBound.left = outerStrokeWidth + shadowSpace;
        innerContentBound.top = outerStrokeWidth + shadowSpace;
        innerContentBound.right = width - outerStrokeWidth - shadowSpace;
        innerContentBound.bottom = height - outerStrokeWidth - shadowSpace;

        intrinsicInnerWidth = innerContentBound.width();
        intrinsicInnerHeight = innerContentBound.height();

        knobBound.left = outerStrokeWidth + shadowSpace;
        knobBound.top = outerStrokeWidth + shadowSpace;
        knobBound.right = height - outerStrokeWidth - shadowSpace;
        knobBound.bottom = height - outerStrokeWidth - shadowSpace;

        intrinsicKnobWidth = knobBound.height();
        knobMaxExpandWidth = (float) width * 0.7F;
        if(knobMaxExpandWidth > knobBound.width() * 1.2F){
            knobMaxExpandWidth = knobBound.width() * 1.2F;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch(event.getAction()){
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(!isOn){
                    innerContentAnimator = ObjectAnimator.ofFloat(ShSwitchView.this, innerContentProperty, innerContentRate, 1.0F);
                    innerContentAnimator.setDuration(300L);
                    innerContentAnimator.setInterpolator(new DecelerateInterpolator());

                    innerContentAnimator.start();
                }

                knobExpandAnimator = ObjectAnimator.ofFloat(ShSwitchView.this, knobExpandProperty, knobExpandRate, 0.0F);
                knobExpandAnimator.setDuration(300L);
                knobExpandAnimator.setInterpolator(new DecelerateInterpolator());

                knobExpandAnimator.start();

                break;
        }

        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //background
        paint.setColor(colorStep);
        paint.setStyle(Paint.Style.FILL);

        drawRoundRect(shadowSpace, shadowSpace, width - shadowSpace, height - shadowSpace, cornerRadius, canvas, paint);

        //innerContent
        paint.setColor(foregroundColor);
        drawRoundRect(innerContentBound, innerContentBound.height() / 2, canvas, paint);

        //knob
        paint.setShadowLayer(5, 0, 5, 0x44000000);
        drawRoundRect(knobBound, cornerRadius, canvas, paint);
        paint.setShadowLayer(0, 0, 0, 0);

        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);

        drawRoundRect(knobBound, cornerRadius, canvas, paint);

    }

    private void drawRoundRect(RectF bound, float radius, Canvas canvas, Paint paint){
        drawRoundRect(bound.left, bound.top, bound.right, bound.bottom, radius, canvas, paint);
    }

    private void drawRoundRect(float left, float top, float right, float bottom, float radius, Canvas canvas, Paint paint){
        roundRectPath.reset();

        float radiusSize = radius * 2;

        roundRectPath.moveTo(left, top + radius);

        ovalForPath.set(left, top, left + radiusSize, top + radiusSize);
        roundRectPath.arcTo(ovalForPath, 180, 90, false);
        roundRectPath.lineTo(right - radius, top);
        ovalForPath.set(right - radiusSize, top, right, top + radiusSize);
        roundRectPath.arcTo(ovalForPath, 270, 90, false);
        roundRectPath.lineTo(right, bottom - radius);
        ovalForPath.set(right - radiusSize, bottom - radiusSize, right, bottom);
        roundRectPath.arcTo(ovalForPath, 0, 90, false);
        roundRectPath.lineTo(left + radius, bottom);
        ovalForPath.set(left, bottom - radiusSize, left + radiusSize, bottom);
        roundRectPath.arcTo(ovalForPath, 90, 90, false);
        roundRectPath.close();

        canvas.drawPath(roundRectPath, paint);
    }

    private int colorTransform(float progress, int fromColor, int toColor) {
        int or = (fromColor >> 16) & 0xFF;
        int og = (fromColor >> 8) & 0xFF;
        int ob = fromColor & 0xFF;

        int nr = (toColor >> 16) & 0xFF;
        int ng = (toColor >> 8) & 0xFF;
        int nb = toColor & 0xFF;

        int rGap = (int) ((float) (nr - or) * progress);
        int gGap = (int) ((float) (ng - og) * progress);
        int bGap = (int) ((float) (nb - ob) * progress);

        return 0xFF000000 | ((or + rGap) << 16) | ((og + gGap) << 8) | (ob + bGap);

    }
}