package com.piliguerra.android.hbpm.anim;

import android.view.animation.Interpolator;

public class CustomCycleInterpolator implements Interpolator {

	
	public CustomCycleInterpolator(float cycles) {
        mCycles = cycles;
    }
     
     
    public float getInterpolation(float input) {
        return (float)(Math.sin(2 * mCycles * Math.PI * input));
    }
    
    public void setCycles(float cycles){
    	mCycles = cycles;
    }
     
    public float getCycles(){
    	return mCycles;
    }
    private float mCycles;
}
