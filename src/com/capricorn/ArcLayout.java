/*
 * Copyright (C) 2012 Capricorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.capricorn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.example.arcmenu_lib.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * A Layout that arranges its children around its center. The arc can be set by
 * calling {@link #setArc(float, float) setArc()}. You can override the method
 * {@link #onMeasure(int, int) onMeasure()}, otherwise it is always
 * WRAP_CONTENT.
 * 
 * @author Capricorn
 * 
 */
public class ArcLayout extends ViewGroup {
    /**
     * children will be set the same size.
     */
//	private static boolean isFixEdge = false;
//	
//	private static boolean isItemOnEdge = true;
	
	private final String TAG = "ArcLayout";
	
    private int mChildSize;

    private int mChildPadding = 5;

    private int mLayoutPadding = 5;

    private static final int DEFAULT_VISIBLE_ITEMS = 7;
    
    public static final float DEFAULT_FROM_DEGREES = 90.0f;

    public static final float DEFAULT_TO_DEGREES = 270.0f;

    private float mFromDegrees = DEFAULT_FROM_DEGREES;

    private float mToDegrees = DEFAULT_TO_DEGREES;
    
    private int mVisibleitems = DEFAULT_VISIBLE_ITEMS;
    
    private int mInvisibleitems = 0;
    
    private static final int MIN_RADIUS = 150;

    /* the distance between the layout's center and any child's center */
    private int mRadius;

    private boolean mExpanded = false;
    
    private FrameLayout mControlLayout;
    
    private ImageView mControlHint;
    
    public ImageView getControlHint(){
    	return mControlHint;
    }
    
    public ArrayList<ArcMenuItem> mListItems = new ArrayList<ArcMenuItem>();
    
    public float getFromDegrees(){
    	return mFromDegrees;
    }
    
    public float getEndDegrees(){
    	return mToDegrees;
    }
    
    @Override
	protected void finalize() throws Throwable {
		mListItems.clear();
		mListItems = null;
    	super.finalize();
		
	}

	public ArcLayout(Context context) {
        super(context);
    }

    public ArcLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(Color.BLUE);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ArcLayout, 0, 0);
            mFromDegrees = a.getFloat(R.styleable.ArcLayout_fromDegrees, DEFAULT_FROM_DEGREES);
            mToDegrees = a.getFloat(R.styleable.ArcLayout_toDegrees, DEFAULT_TO_DEGREES);
            mChildSize = Math.max(a.getDimensionPixelSize(R.styleable.ArcLayout_childSize, 0), 0);
            
            a.recycle();
        }
    }

    public void initView(ArrayList<View> items){
    	if(mListItems!=null){
    		mListItems.clear();
    	}else{
    		mListItems = new ArrayList<ArcMenuItem>();
    	}
    	
    	final float perdegrees = 360.0f/items.size();
    	
    	for(int i=0; i< items.size(); i++){
    		ArcMenuItem tmpItem = (ArcMenuItem)items.get(i);
    		tmpItem.setAngle(mFromDegrees+ perdegrees*i);
    		Log.e(TAG,"tmpItem.setAngle:"+ tmpItem.getAngle());
    		addView(tmpItem);
    		mListItems.add(tmpItem);
    	}
    }
    
    private static int computeRadius(final float arcDegrees, final int childCount, final int childSize,
            final int childPadding, final int minRadius) {
        if (childCount < 2) {
            return minRadius;
        }
        
        final float perDegrees = arcDegrees / (childCount-1);
        final float perHalfDegrees = perDegrees / 2.0f;
        final int perSize = childSize + childPadding;

        final int radius = (int) ((perSize / 2) / Math.sin(Math.toRadians(perHalfDegrees)));

        return Math.max(radius, minRadius);
    }

    
    private static Rect computeChildFrame(final int centerX, final int centerY, final int radius, final float degrees,
            final int size) {
    	
        final double childCenterX = centerX + radius * Math.cos(Math.toRadians(degrees));
        final double childCenterY = centerY - radius * Math.sin(Math.toRadians(degrees));

        return new Rect((int) (childCenterX - size / 2), (int) (childCenterY - size / 2),
                (int) (childCenterX + size / 2), (int) (childCenterY + size / 2));
    }

    public float validateAngleFromStartPoint(float angle){
    	if(angle < mFromDegrees) return angle+360.0f;
		else if(angle >= (mFromDegrees+360.0f)) return angle-360.0f;
		else return angle;
    }
    
    public void arrangeItem(){
    	if(mListItems!=null && mListItems.size()>1){
	    	Collections.sort(mListItems, new Comparator<ArcMenuItem>(){
	
	        	public int compare(ArcMenuItem c1, ArcMenuItem c2) {
					float a1 = validateAngleFromStartPoint(c1.getAngle());
					if(a1 >= (mFromDegrees+350.0f)) a1-=360.0f;
					float a2 = validateAngleFromStartPoint(c2.getAngle());
					if(a2 >= (mFromDegrees+350.0f)) a2-=360.0f;
					
					if(a1 > a2) return 1;
					else if(a1 < a2) return -1;
					else return 0;
				}
	        });
    	}
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	mVisibleitems = Math.min(mVisibleitems, getChildCount());
    	mInvisibleitems = Math.max(mVisibleitems, getChildCount()) - mVisibleitems;
        final int radius = mRadius = computeRadius(Math.abs(mToDegrees - mFromDegrees), mVisibleitems, mChildSize,
                mChildPadding, MIN_RADIUS);
//        final int size = radius * 2 + mChildSize + mChildPadding + mLayoutPadding * 2;

        final int height = radius * 2 + mChildSize + mChildPadding + mLayoutPadding * 2;
        final int width = (int)(radius + mChildSize + mChildPadding*2 + mLayoutPadding * 2);
        
        setMeasuredDimension(width, height);
        
        arrangeItem();
        
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
           mListItems.get(i).measure(MeasureSpec.makeMeasureSpec(mChildSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mChildSize, MeasureSpec.EXACTLY));
        }
    }

    private Point centerPoint;
    
    public Point getCenterPoint(){
    	if(centerPoint == null){
    		centerPoint = new Point(
    			(int)(getRight() - mLayoutPadding - mChildPadding - mChildSize/2.0f),
    			(int)((getBottom()-getTop())/2.0f));
    	}else{
    		centerPoint.set((int)(
    				getRight() - mLayoutPadding - mChildPadding - mChildSize/2.0f),
    				(int)((getBottom()-getTop())/2.0f));
    	}
    	return centerPoint;
    }
    
    private int getRadiusByAngle(float degrees){
    	int radius = mRadius;
    	if(!mExpanded){
    		radius = 0;
    	} else if((degrees < (mFromDegrees + 350.0f)) 
    			&& (degrees > (mToDegrees + 10.0f)) ){
    		radius = 0;
    	}
    	Log.e(TAG,"getRadiusByAngle: "+degrees + " is: "+ radius);
    	return radius;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final Point centerP = getCenterPoint();
        Log.e(TAG,"center Point: "+ centerP.x + "--" + centerP.y );

        final int childCount = mListItems.size();
        arrangeItem();
        for (int i = 0; i < childCount; i++) {
            Rect frame = computeChildFrame(centerP.x, centerP.y, getRadiusByAngle(mListItems.get(i).getAngle()), mListItems.get(i).getAngle(), mChildSize);
            mListItems.get(i).layout(frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    /**
     * refers to {@link LayoutAnimationController#getDelayForView(View view)}
     */
    private static long computeStartOffset(final int childCount, final boolean expanded, final int index,
            final float delayPercent, final long duration, Interpolator interpolator) {
        final float delay = delayPercent * duration;
        final long viewDelay = (long) (getTransformedIndex(expanded, childCount, index) * delay);
        final float totalDelay = delay * childCount;

        float normalizedDelay = viewDelay / totalDelay;
        normalizedDelay = interpolator.getInterpolation(normalizedDelay);

        return (long) (normalizedDelay * totalDelay);
    }

    private static int getTransformedIndex(final boolean expanded, final int count, final int index) {
        if (expanded) {
            return count - 1 - index;
        }

        return index;
    }

    private static Animation createExpandAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,
            long startOffset, long duration, Interpolator interpolator) {
        Animation animation = new RotateAndTranslateAnimation(0, toXDelta, 0, toYDelta, 0, 720);
        animation.setStartOffset(startOffset);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        animation.setFillAfter(true);

        return animation;
    }

    private static Animation createShrinkAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,
            long startOffset, long duration, Interpolator interpolator) {
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setFillAfter(true);

        final long preDuration = duration / 2;
        Animation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setStartOffset(startOffset);
        rotateAnimation.setDuration(preDuration);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);

        animationSet.addAnimation(rotateAnimation);

        Animation translateAnimation = new RotateAndTranslateAnimation(0, toXDelta, 0, toYDelta, 360, 720);
        translateAnimation.setStartOffset(startOffset + preDuration);
        translateAnimation.setDuration(duration - preDuration);
        translateAnimation.setInterpolator(interpolator);
        translateAnimation.setFillAfter(true);

        animationSet.addAnimation(translateAnimation);

        return animationSet;
    }
    
    private void bindChildAnimation(final View child, final int index, final long duration) {
        final boolean expanded = mExpanded;
        final Point centerPoint = getCenterPoint();
        final int childCount = mListItems.size();
        arrangeItem();
        Rect frame = computeChildFrame(centerPoint.x, centerPoint.y, getRadiusByAngle(mListItems.get(index).getAngle()), mListItems.get(index).getAngle(), mChildSize);

        final int toXDelta = frame.left - child.getLeft();
        final int toYDelta = frame.top - child.getTop();

        Interpolator interpolator = mExpanded ? new AccelerateInterpolator() : new OvershootInterpolator(1.5f);
        final long startOffset = computeStartOffset(childCount, mExpanded, index, 0.1f, duration, interpolator);

        Animation animation = mExpanded ? createShrinkAnimation(0, toXDelta, 0, toYDelta, startOffset, duration,
                interpolator) : createExpandAnimation(0, toXDelta, 0, toYDelta, startOffset, duration, interpolator);

        final boolean isLast = getTransformedIndex(expanded, childCount, index) == childCount - 1;
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isLast) {
                    postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            onAllAnimationsEnd();
                        }
                    }, 50);
                }
            }
        });

        child.setAnimation(animation);
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setArc(float fromDegrees, float toDegrees) {
        if (mFromDegrees == fromDegrees && mToDegrees == toDegrees) {
            return;
        }

        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;

        requestLayout();
    }

    public void setChildSize(int size) {
        if (mChildSize == size || size < 0) {
            return;
        }

        mChildSize = size;

        requestLayout();
    }

    /**
     * switch between expansion and shrinkage
     * 
     * @param showAnimation
     */
    public void switchState(final boolean showAnimation) {

        mExpanded = !mExpanded;
        if (showAnimation) {
            final int childCount = mListItems.size();
            for (int i = 0; i < childCount; i++) {
                bindChildAnimation(mListItems.get(i), i, 300);
            }
        }


        if (!showAnimation) {
            requestLayout();
        }
        
        invalidate();
    }

    private void onAllAnimationsEnd() {
        final int childCount = mListItems.size();
        for (int i = 0; i < childCount; i++) {
            mListItems.get(i).clearAnimation();
        }

        requestLayout();
    }
}
