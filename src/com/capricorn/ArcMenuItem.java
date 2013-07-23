package com.capricorn;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ArcMenuItem extends ImageView {

	private OnClickListener mLtnOnClick;
	private int mID;
	private String mName;
	private float mAngle;
	
	public void setID(int id){ this.mID = id;}
	
	public int getID(){ return this.mID;}
	
	public void setName(String name){
		if(name == null) this.mName = "";
		else this.mName = name;
	}
	
	public String getName(){
		return (this.mName!=null)? this.mName : "";
	}
	
	public void setAngle(float angle){
		this.mAngle = angle;
	}
	
	public float getAngle(){
		return this.mAngle;
	}
	
	public void setMainOnClick(OnClickListener ltn){
		mLtnOnClick = ltn;
	}
	
	public OnClickListener getMainOnClick(){
		return this.mLtnOnClick;
	}
	
	public ArcMenuItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ArcMenuItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public ArcMenuItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

}
