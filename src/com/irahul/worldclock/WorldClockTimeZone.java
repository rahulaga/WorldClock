package com.irahul.worldclock;

import java.util.TimeZone;
/**
 * TimeZone representation. Decorator for java.util.Timezone
 * @author rahul
 *
 */
public class WorldClockTimeZone {

	private TimeZone timeZone;
	private String displayName;
	
	public WorldClockTimeZone(TimeZone timeZone) {
		this(timeZone, timeZone.getDisplayName());
	}	
	
	public WorldClockTimeZone(TimeZone timeZone, String displayName) {		
		this.timeZone = timeZone;
		this.displayName = displayName;
	}


	@Override
	public String toString() {		
		return this.timeZone.getID()+" ("+this.getDisplayName()+")";
	}

	public String getId() {		
		return this.timeZone.getID();
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public TimeZone getTimeZone() {
		return this.timeZone;
	}
	
	@Override
	public boolean equals(Object o) {		
		if(o==null) return false;
		if(!(o instanceof WorldClockTimeZone)) return false;	
		
		WorldClockTimeZone that = (WorldClockTimeZone)o;				
		return this.getId().equals(that.getId());
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
}
