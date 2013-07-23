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

import com.example.arcmenu_lib.R;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * A custom view that looks like the menu in <a href="https://path.com">Path
 * 2.0</a> (for iOS).
 * 
 * @author Capricorn
 * 
 */
public class ArcMenu extends RelativeLayout implements GestureDetector.OnGestureListener{
	
	/**
     * If true, this onScroll is the first for this user's drag (remember, a
     * drag sends many onScrolls).
     */
    private boolean mIsFirstScroll;
	private boolean mDirection = true;
	private Point p_Center;
	private Point lastTouch_Point;
    private ArcLayout mArcLayout;

    private boolean mShouldStopFling;
    
    private FlingRotateRunnable mFlingRunnable = new FlingRotateRunnable();
    
    private GestureDetector mGestureDetector;
    
    private ImageView mHintView;

    private int mAnimationDuration = 400;
    
    private View mSelectedChild;
    
    public ArcMenu(Context context) {
        super(context);
        init(context);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
    	mGestureDetector = new GestureDetector(this.getContext(), this);
		mGestureDetector.setIsLongpressEnabled(true);
		
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.arc_menu, this);

        mArcLayout = (ArcLayout) findViewById(R.id.item_layout);

        final ViewGroup controlLayout = (ViewGroup) findViewById(R.id.control_layout);
        controlLayout.setClickable(true);
        controlLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHintView.startAnimation(createHintSwitchAnimation(mArcLayout.isExpanded()));
				mArcLayout.switchState(true);
			}
		});
        
//        controlLayout.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mHintView.startAnimation(createHintSwitchAnimation(mArcLayout.isExpanded()));
//                    mArcLayout.switchState(true);
//                }
//                return false;
//            }
//        });

        mHintView = (ImageView) findViewById(R.id.control_hint);
    }

	public void rotate(float degrees){
    	if(mArcLayout.isExpanded()){
    		
    	}
    }
    
    public void addItems(ArrayList<View> items){
    	mArcLayout.initView(items);
    	for(View item: items){
    		item.setOnClickListener(getItemClickListener(((ArcMenuItem)item).getMainOnClick()));
    	}
//    	
//    	Point centerP = mArcLayout.getCenterPoint();
//    	mHintView.setLeft((int)(centerP.x - mHintView.getWidth()/2.0f));
//    	mHintView.setLeft((int)(centerP.x - mHintView.getHeight()/2.0f));
    	
    }

    private OnClickListener getItemClickListener(final OnClickListener listener) {
        return new OnClickListener() {

            @Override
            public void onClick(final View viewClicked) {
//                Animation animation = bindItemAnimation(viewClicked, true, 400);
//                animation.setAnimationListener(new AnimationListener() {
//
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        postDelayed(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                itemDidDisappear();
//                            }
//                        }, 0);
//                    }
//                });
////
//                final int itemCount = mArcLayout.mListItems.size();
//                for (int i = 0; i < itemCount; i++) {
//                    View item = mArcLayout.mListItems.get(i);
//                    if (viewClicked != item) {
//                        bindItemAnimation(item, false, 300);
//                    }
//                }
//
//                mArcLayout.invalidate();
//                mHintView.startAnimation(createHintSwitchAnimation(true));

            	if(mFlingRunnable.mRotator.isFinished()){
            		float item_angle = ((ArcMenuItem)viewClicked).getAngle();
                	float centerDegrees = (mArcLayout.getFromDegrees() + mArcLayout.getEndDegrees())/2.0f;
                	
                	mDirection = centerDegrees >= item_angle;
                	float angle = centerDegrees - item_angle;
                	if(Math.abs(angle) >0.5f){
                    	Log.i("ArcMenu","scrollIntoSlots-> angle:"+angle);
                    	mFlingRunnable.startUsingDistance(angle, item_angle);
                    }else{
                        // Set selected position
//                        setSelectedPositionInt(position);
//                    	onFinishedMovement();
//                    	invokeStopAtPointG(getChildAt(position), position,
//            					mAdapter.getItemId(position));
                    }
                	
            		if (listener != null) {
                        listener.onClick(viewClicked);
                    }
            	}else{
            		mFlingRunnable.stop(false);
                	
            	}
            }
        };
    }

    private Animation bindItemAnimation(final View child, final boolean isClicked, final long duration) {
        Animation animation = createItemDisapperAnimation(duration, isClicked);
        child.setAnimation(animation);

        return animation;
    }

    private void itemDidDisappear() {
        final int itemCount = mArcLayout.mListItems.size();
        for (int i = 0; i < itemCount; i++) {
            View item = mArcLayout.mListItems.get(i);
            item.clearAnimation();
        }

        mArcLayout.switchState(false);
    }

    private static Animation createItemDisapperAnimation(final long duration, final boolean isClicked) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, isClicked ? 2.0f : 0.0f, 1.0f, isClicked ? 2.0f : 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));

        animationSet.setDuration(duration);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setFillAfter(true);

        return animationSet;
    }

    private static Animation createHintSwitchAnimation(final boolean expanded) {
        Animation animation = new RotateAnimation(expanded ? 45 : 0, expanded ? 0 : 45, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(0);
        animation.setDuration(100);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);

        return animation;
    }
    
    private Point getCenterPoint(){
    	int[] location = {0,0};
    	mHintView.getLocationInWindow(location);
    	
    	int centerx = (int)(mHintView.getWidth()/2.0f + location[0]);
    	int centery = (int)(mHintView.getHeight()/2.0f + location[1]);
    	
    	if(p_Center == null){
    		p_Center = new Point();
    	}
		p_Center.set(centerx, centery);
		return p_Center;
    	
    }

    
    /**
     * 
     * @param angle : in degree unit
     * @return validated Angle in degree Unit
     */
    private float validateAngle(float angle){
    	if(angle < 0.0f) return angle + 360.0f;
    	else if(angle >= 360.0f) return angle - 360.0f;
    	else return angle;
    }
    
    
    /**
     * 
     * @param offSetAngle : in degree unit
     * @return validated OffSet Angle in degree Unit
     */
    private float validateOffSetAngle(float offSetAngle){
    	if(offSetAngle < -180.0f) return offSetAngle + 360.0f;
    	else if(offSetAngle >= 180.0f) return offSetAngle - 360.0f;
    	else return offSetAngle;
    }
    
    private float calDegreesFromPoints(Point p_from, Point p_end){
    	Point p_center = getCenterPoint();
    	float angle1 = validateAngle((float) Math.toDegrees(Math.atan2(p_center.y - p_from.y, p_from.x - p_center.x)));
    	Log.i("ArcMenu","Angle1:"+angle1);
    	float angle2 = validateAngle((float) Math.toDegrees(Math.atan2(p_center.y - p_end.y, p_end.x - p_center.x)));
    	Log.i("ArcMenu","Angle2:"+angle2);
    	return validateOffSetAngle(angle2 - angle1);
    	
    }
    
    private static float calDistancePoints(Point p1, Point p2){
		float result = (float)Math.sqrt(
				Math.pow(Math.abs(p2.x - p1.x), 2.0f) + Math.pow(Math.abs(p2.y - p1.y), 2.0f));
    	return result;
    }

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		mFlingRunnable.stop(false);
		
		if(lastTouch_Point == null) lastTouch_Point = new Point((int)e.getRawX(), (int)e.getRawY());
		else lastTouch_Point.set((int)e.getRawX(), (int)e.getRawY());
		
		mIsFirstScroll = true;
		return true;
	}

	
	/**
	 * 
	 * @param p_from
	 * @param p_end
	 * @param velX
	 * @param velY
	 * @return angleVerlocity is messured in degrees per second
	 */
	private float calDegreesVerlocity(Point p_from, Point p_end, float velX, float velY){
		Point p_center = getCenterPoint();
		float radius2PointFrom = calDistancePoints(p_center, p_from);
		Log.e("ArcMenu","radius2PointFrom:"+ radius2PointFrom);
		
		double degreesAtFromPoint = Math.toDegrees(
				(Math.atan2(p_from.y - p_center.y, p_center.x - p_from.x) - 
					Math.atan2(p_from.y - p_end.y, p_end.x - p_from.x) ) );
		degreesAtFromPoint = validateOffSetAngle((float)degreesAtFromPoint);
		double realVel = Math.sqrt(
				Math.pow(velX, 2.0f) + Math.pow(velY, 2.0f));
		Log.i("ArcMenu","realVel:"+ realVel);
		Log.i("ArcMenu","degreesAtFromPoint:"+ degreesAtFromPoint);
		realVel *=Math.abs( Math.sin(Math.toRadians(degreesAtFromPoint)) );
		Log.i("ArcMenu","realVel_decac_radial:"+ realVel);
		
		float angleFromPoints = calDegreesFromPoints(p_from, p_end);
		return (float)(Math.signum(angleFromPoints)*Math.toDegrees(realVel/radius2PointFrom));
		
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(e1!=null){
			Log.i("ArcMenu","e1x:"+ e1.getRawX() + " e1y:"+ e1.getRawY());
		}
		if(e2!=null){
			Log.i("ArcMenu","e2x:"+ e2.getRawX() + " e2y:"+ e2.getRawY());
		}
		
		Point p_from = new Point((int)e1.getRawX(), (int)e1.getRawY());
		Point p_end = new Point((int)e2.getRawX(), (int)e2.getRawY());
		
		float real_Velocity = calDegreesVerlocity(p_from, p_end, velocityX, velocityY);
		Log.i("ArcMenu","realVel_degreel:"+ real_Velocity);
		mFlingRunnable.startUsingVelocity(real_Velocity);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		getParent().requestDisallowInterceptTouchEvent(true);
		Point p_from = lastTouch_Point;
		Point p_end = new Point((int)e2.getRawX(), (int)e2.getRawY());
		float deltaDegree =
				calDegreesFromPoints(p_from, p_end);
		lastTouch_Point.set((int)e2.getRawX(), (int)e2.getRawY());
		
		
		trackMotionScroll(deltaDegree);
		
		mIsFirstScroll = false;
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean retValue = mGestureDetector.onTouchEvent(event);
		
		int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            // Helper method for lifted finger
            onUp();
        } else if (action == MotionEvent.ACTION_CANCEL) {
            onCancel();
        }
		return retValue;
	}
	
	void onCancel(){
    	Log.i("Carousel","onCancel()");
    	onUp();
    }
	
	void onUp(){
    	Log.i("Carousel","onUp()");
        if (mFlingRunnable.mRotator.isFinished()) {
        	Log.i("carousel","onUp -> scrollIntoSlots");
            scrollIntoSlots();
        }        
        dispatchUnpress();    	
    }
	
	private void dispatchUnpress() {
    	//Log.i("Carousel","dispatchUnpress()");
        for (int i = mArcLayout.mListItems.size() - 1; i >= 0; i--) {
        	mArcLayout.mListItems.get(i).setPressed(false);
        }
        setPressed(false);
    }   
	
	private class FlingRotateRunnable implements Runnable {

		private Rotator mRotator;
		private float mLastFlingAngle;
		
        public FlingRotateRunnable(){
        	mRotator = new Rotator(getContext());
        }
        
        private void startCommon() {
        	removeCallbacks(this);
        }
		
        private float computeStartAngle(float initialVelocity){
        	if(mArcLayout.mListItems.size() == 0) return 0.0f;
        	float firstAngle = mArcLayout.mListItems.get(0).getAngle();
        	return Math.signum(initialVelocity)*(firstAngle - mArcLayout.getFromDegrees());
        }
        
        public void startUsingVelocity(float initialVelocity) {
        	Log.e("ArcMenu","startUsingVelocity:"+ initialVelocity);
        	if (initialVelocity == 0.0f) return;
        	float startAngle = computeStartAngle(initialVelocity);
        	
        	startCommon();
        	mLastFlingAngle = 0.0f;
        	float perdegrees = 360.0f/mArcLayout.mListItems.size();
        	mRotator.fling(initialVelocity, perdegrees, startAngle);
        	post(this);
        }        

        public void startUsingDistance(float deltaAngle, float startAngle) {
            if (deltaAngle == 0.0f) return;
            
            startCommon();
            
            mLastFlingAngle = 0.0f;
            synchronized(this){
            	mRotator.startRotate(startAngle, deltaAngle, mAnimationDuration);
            }
            post(this);
        }
        
        public void stop(boolean scrollIntoSlots) {
        	removeCallbacks(this);
            endFling(scrollIntoSlots);
        }
        
        private void endFling(boolean scrollIntoSlots) {
        	synchronized(this){
        		mRotator.forceFinished(true);
        	}
            
            if (scrollIntoSlots){ 
            	//Log.i("Carousel","endFling -> scrollIntoSlots");
            	scrollIntoSlots();
            }
        }
        
        
		public void run() { 
			if(getChildCount() == 0){
				endFling(true);
				return;
			}
			
			mShouldStopFling = false;
			final Rotator rotator;
			final float degrees;
			boolean more;
			synchronized (this) {
				rotator = mRotator;
				more = rotator.computeAngleOffset();
				degrees = rotator.getCurrAngle();
			}
			float delta_degrees = degrees - mLastFlingAngle;
			
			trackMotionScroll(delta_degrees);
			
			if(more && !mShouldStopFling){
				mLastFlingAngle = degrees;
				post(this);
			}else{
				mLastFlingAngle = 0.0f;
//				endFling(true);
			}
			
		}
		
	}
	
	private void trackMotionScroll(float deltaDegrees){
		Log.e("ArcMenu","angleOffset:"+ deltaDegrees);
		
		mDirection = (deltaDegrees >=0);
		
		int childCount = mArcLayout.mListItems.size();
		if(childCount == 0){
			return;
		}
		
		for(int i=0; i< childCount;i++){
			float tmpNextAngle = mArcLayout.validateAngleFromStartPoint(mArcLayout.mListItems.get(i).getAngle() + deltaDegrees);
			mArcLayout.mListItems.get(i).setAngle(tmpNextAngle);
		}
		mArcLayout.arrangeItem();
		mArcLayout.requestLayout();
	}
	
	private void scrollIntoSlots(){
    	//Log.i("Carousel","scrollIntoSlots()");
    	// Nothing to do
        if (mArcLayout.mListItems.size() == 0) return;
        
        // get nearest item to the 0 degrees angle
        // Sort itmes and get nearest angle
    	float angle; 
    	int position = 0;;
    	
    	float perdegrees = 360.0f/mArcLayout.mListItems.size();
    	float centerDegrees = (mArcLayout.getFromDegrees() + mArcLayout.getEndDegrees())/2.0f;
    	float lessNearestPosition = centerDegrees - perdegrees;
    	float itemAngle = 0;
    	
    	
		for(int i=0; i < mArcLayout.mListItems.size(); i++){
			itemAngle = mArcLayout.mListItems.get(i).getAngle();
    		if(itemAngle > lessNearestPosition && itemAngle < centerDegrees){
    			position = i;
    			break;
    		}
    	}
    	if(mDirection){
    		angle = centerDegrees - itemAngle;
    	}else{
    		angle = lessNearestPosition - itemAngle;
    		position++;
    	}
    	
    	if(Math.abs(angle) >0.5f){
        	Log.i("ArcMenu","scrollIntoSlots-> angle:"+angle);
        	mFlingRunnable.startUsingDistance(angle, itemAngle);
        }else{
            // Set selected position
        	Log.i("ArcMenu","selected position-> position:"+position);
//            setSelectedPositionInt(position);
//        	onFinishedMovement();
//        	invokeStopAtPointG(getChildAt(position), position,
//					mAdapter.getItemId(position));
        }
    	
    	
	}
}
