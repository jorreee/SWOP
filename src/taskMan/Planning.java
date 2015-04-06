package taskMan;

import java.time.LocalDateTime;

import taskMan.util.TimeSpan;

public class Planning {
	
	private LocalDateTime beginTime;
	private final TimeSpan estimatedDuration;
	private final int acceptableDeviation;
	
	public Planning(int estimatedDuration, int acceptableDeviation) {
		this.estimatedDuration = new TimeSpan(estimatedDuration);
		this.acceptableDeviation = acceptableDeviation;
		
	}
	
	public LocalDateTime getBeginTime() {
		return beginTime;
	}
	
	public void setBeginTime(LocalDateTime beginTime) throws IllegalArgumentException {
		if(beginTime==null) {
			throw new IllegalArgumentException("The new beginTime is null");
		}
		if(getBeginTime()!=null) {
			throw new IllegalArgumentException("The beginTime is already set");
		}
		this.beginTime = beginTime;
	}
	
	public TimeSpan getEstimatedDuration() {
		return estimatedDuration;
	}
	
	public int getAcceptableDeviation() {
		return acceptableDeviation;
	}
	
	public LocalDateTime getPlannedEndTime() {
		if(getBeginTime() == null) {
			return null;
		}
		return TimeSpan.addSpanToLDT(beginTime, estimatedDuration);
	}
	
	public LocalDateTime getAcceptableEndTime() {
		if(getBeginTime() == null) {
			return null;
		}
		return TimeSpan.addSpanToLDT(beginTime, estimatedDuration.getAcceptableSpan(acceptableDeviation));
	}

}
