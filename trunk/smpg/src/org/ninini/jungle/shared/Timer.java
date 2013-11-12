package org.ninini.jungle.shared;

import java.util.Date;

public class Timer {
	private long start;
	private int period;
	
	public Timer(int milliseconds){
		this.period = milliseconds;
		if(milliseconds > 0){
			start = now();
		}
	}
	
	public long now(){
		return new Date().getTime();
	}
	
	public boolean didTimeout(){
		return period>0?(now()-start)>period:false;
	}
}
