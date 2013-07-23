package com.capricorn;

import android.app.ActionBar.Tab;
import android.content.Context;
import android.util.Log;
import android.view.animation.AnimationUtils;

/**
 * This class encapsulates rotation.  The duration of the rotation
 * can be passed in the constructor and specifies the maximum time that
 * the rotation animation should take.  Past this time, the rotation is 
 * automatically moved to its final stage and computeRotationOffset()
 * will always return false to indicate that scrolling is over.
 */
public class Rotator {
	private final String TAG = "Rotator";
    private int mMode;
    private float mStartAngle;
    private float mCurrAngle;
    
    
    private long mStartTime;
    /**
     * mDuration is messured in milisecond unit
     */
    private long mDuration;
    
    /**
     * totalAngleFlinged is messured in degrees
     */
    private float totalAngleFlinged;
    
    
    private float mDeltaAngle;
    
    private boolean mFinished;

    private float mCoeffVelocity = 0.3f;
    
    /*
     * mVelocity is messured in degrees per second
     */
    private float mVelocity;
    
    private static final int DEFAULT_DURATION = 800;
    private static final int SCROLL_MODE = 0;
    private static final int FLING_MODE = 1;
    
    private final float REF_DECELERATION = 500.0f;
    private float mDeceleration = REF_DECELERATION;
    
    
    /**
     * Create a Scroller with the specified interpolator. If the interpolator is
     * null, the default (viscous) interpolator will be used.
     */
    public Rotator(Context context) {
        mFinished = true;
    }
    
    /**
     * 
     * Returns whether the scroller has finished scrolling.
     * 
     * @return True if the scroller has finished scrolling, false otherwise.
     */
    public final boolean isFinished() {
        return mFinished;
    }
    
    /**
     * Force the finished field to a particular value.
     *  
     * @param finished The new finished value.
     */
    public final void forceFinished(boolean finished) {
        mFinished = finished;
    }
    
    /**
     * Returns how long the scroll event will take, in milliseconds.
     * 
     * @return The duration of the scroll in milliseconds.
     */
    public final long getDuration() {
        return mDuration;
    }
    
    /**
     * Returns the current X offset in the scroll. 
     * 
     * @return The new X offset as an absolute distance from the origin.
     */
    public final float getCurrAngle() {
        return mCurrAngle;
    }
        
    
    
    /**
     * @hide
     * Returns the current velocity.
     *
     * @return The original velocity less the deceleration. Result may be
     * negative.
     */
    public float getCurrVelocity() {
        return mVelocity - mDeceleration * timePassed();
    }

    /**
     * Returns the start X offset in the scroll. 
     * 
     * @return The start X offset as an absolute distance from the origin.
     */
    public final float getStartAngle() {
        return mStartAngle;
    }    
    
    
    
    /**
     * Returns the time elapsed since the beginning of the scrolling.
     *
     * @return The elapsed time in milliseconds.
     */
    public int timePassed() {
        return (int)(AnimationUtils.currentAnimationTimeMillis() - mStartTime);
    }
    
//    /**
//     * Extend the scroll animation. This allows a running animation to scroll
//     * further and longer, when used with {@link #setFinalX(int)} or {@link #setFinalY(int)}.
//     *
//     * @param extend Additional time to scroll in milliseconds.
//     * @see #setFinalX(int)
//     * @see #setFinalY(int)
//     */
//    public void extendDuration(int extend) {
//        int passed = timePassed();
//        mDuration = passed + extend;
//        mFinished = false;
//    }
    
    /**
     * Stops the animation. Contrary to {@link #forceFinished(boolean)},
     * aborting the animating cause the scroller to move to the final x and y
     * position
     *
     * @see #forceFinished(boolean)
     */
    public void abortAnimation() {
        mFinished = true;
    }    
    

    /**
     * Call this when you want to know the new location.  If it returns true,
     * the animation is not yet finished.  loc will be altered to provide the
     * new location.
     */ 
    public boolean computeAngleOffset()
    {
        if (mFinished) {
            return false;
        }
        
        long systemClock = AnimationUtils.currentAnimationTimeMillis();
        long timePassed = systemClock - mStartTime;
        boolean result = true;
        switch (mMode) {
		case SCROLL_MODE:
			if(!mFinished){
				Log.i(TAG,"timePassed:"+ timePassed);
				if (timePassed < mDuration) {
	    			float sc = (float)timePassed / mDuration;
	                mCurrAngle = Math.round(mDeltaAngle * sc); 
	                Log.i(TAG,"mCurrAngle:"+ mCurrAngle);
	                result =  true;
				}else{
					mCurrAngle = mDeltaAngle;
					Log.i(TAG,"mCurrAngle:"+ mCurrAngle);
					mFinished = true;
					result = true;
				}
			}else{
				Log.i(TAG,"mFinished:"+ mFinished);
				result = false;
			}
			break;
		case FLING_MODE:
			if (timePassed < mDuration) {
				Log.i(TAG,"timePassed:"+ timePassed);
				float timePassedSeconds = timePassed / 1000.0f;
				float distance = Math.signum(mVelocity)*
						(Math.abs(mVelocity) * timePassedSeconds - 
								(mDeceleration * timePassedSeconds * timePassedSeconds / 2.0f) );
				Log.i(TAG,"distance:"+ distance);
				if(Math.abs(distance) < Math.abs(totalAngleFlinged)){
					mCurrAngle = distance;
					result = true;
				}else{
					mCurrAngle = totalAngleFlinged;
					mFinished = true;
					result = false;
				}
			}else{
				mFinished = true;
				result = false;
			}
			
			break;
		}
        
        return result;
    }
    
    
    /**
     * Start scrolling by providing a starting point and the distance to travel.
     * 
     * @param startX Starting horizontal scroll offset in pixels. Positive
     *        numbers will scroll the content to the left.
     * @param startY Starting vertical scroll offset in pixels. Positive numbers
     *        will scroll the content up.
     * @param dx Horizontal distance to travel. Positive numbers will scroll the
     *        content to the left.
     * @param dy Vertical distance to travel. Positive numbers will scroll the
     *        content up.
     * @param duration Duration of the scroll in milliseconds.
     */
    public void startRotate(float startAngle, float dAngle, int duration) {
        mMode = SCROLL_MODE;
        mFinished = false;
        mDuration = duration;
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        mStartAngle = startAngle;
        mDeltaAngle = dAngle;
        Log.e(TAG,"startRotate->startAngle"+startAngle);
        Log.e(TAG,"startRotate->dAngle"+dAngle);
        Log.e(TAG,"startRotate->duration"+duration);
    }    
    
    /**
     * Start scrolling by providing a starting point and the distance to travel.
     * The scroll will use the default value of 250 milliseconds for the
     * duration.
     * 
     * @param startX Starting horizontal scroll offset in pixels. Positive
     *        numbers will scroll the content to the left.
     * @param startY Starting vertical scroll offset in pixels. Positive numbers
     *        will scroll the content up.
     * @param dx Horizontal distance to travel. Positive numbers will scroll the
     *        content to the left.
     * @param dy Vertical distance to travel. Positive numbers will scroll the
     *        content up.
     */
    public void startRotate(float startAngle, float dAngle) {
        startRotate(startAngle, dAngle, DEFAULT_DURATION);
    }
    
    /**
     * 
     * @param verlocity measured in degrees per second.
     * @return
     */
    private float calTotalAngle(float verlocity, float angleStep){
    	Log.e(TAG,"mStartAngle:"+ mStartAngle);
    	float totalAngle = 0.5f*verlocity*verlocity/REF_DECELERATION;
    	totalAngle = angleStep*(int)Math.ceil((mStartAngle + totalAngle)/angleStep) - mStartAngle;
    	
    	mDeceleration = 0.5f*(verlocity*verlocity)/totalAngle;
    	Log.e(TAG,"mDeceleration:"+ mDeceleration);
    	mDuration = (int)Math.ceil(((Math.abs(verlocity)/mDeceleration)*1000));
    	Log.e(TAG,"mDuration:"+ mDuration);
    	return Math.signum(verlocity)*totalAngle;
    }
    
    /**
     * Start scrolling based on a fling gesture. The distance travelled will
     * depend on the initial velocity of the fling.
     * 
     * @param velocityAngle Initial velocity of the fling (X) measured in degrees per second.
     */
    	
    public void fling(float iVerlocity, float angleStep, float startAngle){
    	mStartAngle = startAngle;
    	float velocityAngle = iVerlocity* mCoeffVelocity; 
    	mMode = FLING_MODE;
    	mFinished = false;
    	mVelocity = velocityAngle;
    	Log.e(TAG,"iVerlocity:"+ mVelocity);
    	totalAngleFlinged = calTotalAngle(mVelocity, angleStep);
    	Log.e(TAG,"totalAngleFlinged:"+ totalAngleFlinged);
    	
    	mStartTime = AnimationUtils.currentAnimationTimeMillis();
    }
}
