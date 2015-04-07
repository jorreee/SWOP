package taskMan;

import java.time.LocalDateTime;

import taskMan.util.TimeSpan;

public class Planning {

	private LocalDateTime beginTime;
	private LocalDateTime endTime;
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
	
	public LocalDateTime getEndTime() {
		return endTime;
	}
	
	public void setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
		if(endTime==null) {
			throw new IllegalArgumentException("The new beginTime is null");
		}
		if(getEndTime()!=null) {
			throw new IllegalArgumentException("The beginTime is already set");
		}
		this.endTime = endTime;
	}
	
	public TimeSpan getEstimatedDuration() {
		return estimatedDuration;
	}
	
	public int getAcceptableDeviation() {
		return acceptableDeviation;
	}
	
	public TimeSpan getTimeSpent(LocalDateTime currentTime) {
		if(currentTime == null) {
			return new TimeSpan(0);
		}
		if(beginTime == null) {
			return new TimeSpan(0);
		}
		if(beginTime.isAfter(currentTime)) {
			return new TimeSpan(0);
		}
		if(endTime != null) {
			return new TimeSpan(TimeSpan.getDifferenceWorkingMinutes(beginTime, endTime));
		}
		
		int currentTimeSpent = TimeSpan.getDifferenceWorkingMinutes(
				getBeginTime(), 
				currentTime);
		
		return new TimeSpan(currentTimeSpent);
		
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
