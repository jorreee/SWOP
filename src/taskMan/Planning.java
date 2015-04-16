package taskMan;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.util.TimeSpan;
import taskMan.view.ResourceView;

public class Planning {

	private LocalDateTime plannedBeginTime;
	private LocalDateTime beginTime;
	private LocalDateTime endTime;
	private final TimeSpan estimatedDuration;
	private final int acceptableDeviation;
	
	private List<ResourceView> plannedDevelopers;
	
	public Planning(int estimatedDuration, int acceptableDeviation) {
		this.estimatedDuration = new TimeSpan(estimatedDuration);
		this.acceptableDeviation = acceptableDeviation;
		
	}
	
	public LocalDateTime getBeginTime() {
		return beginTime;
	}
	
	public LocalDateTime getPlannedBeginTime() {
		return plannedBeginTime;
	}
	
	public boolean setPlannedBeginTime(LocalDateTime newPlannedBeginTime) {
		if(newPlannedBeginTime==null) {
			return false;
		}
//		if(getPlannedBeginTime() != null) { // TODO this will happen when conflicts arise
//			return false; //already set
//		}
		if(beginTime != null) {
			return false; //already started working
		}
		this.plannedBeginTime = newPlannedBeginTime;
		return true;
	}
	
	public boolean setBeginTime(LocalDateTime beginTime) {
		if(beginTime==null) {
			return false;
		}
		if(getBeginTime() != null) {
			return false; //already set
		}
		if(getPlannedBeginTime() == null) {
			return false; //nog niet gepland
		}
		this.beginTime = beginTime;
		return true;
	}
	
	public LocalDateTime getEndTime() {
		return endTime;
	}
	
	public boolean setEndTime(LocalDateTime endTime) {
		if(getBeginTime() == null) {
			return false; //The plan hasn't even started yet
		}
		if(endTime == null) {
			return false; 
		}
		if(getEndTime() != null) {
			return false; //already set
		}
		this.endTime = endTime;
		return true;
		
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
				beginTime, 
				currentTime);
		
		return new TimeSpan(currentTimeSpent);
		
	}
	
	public LocalDateTime getPlannedEndTime() {
		if(plannedBeginTime == null) {
			return null;
		}
		if(beginTime == null) {
			return TimeSpan.addSpanToLDT(plannedBeginTime, estimatedDuration);
		}
		return TimeSpan.addSpanToLDT(beginTime, estimatedDuration);
	}
	
	public LocalDateTime getAcceptableEndTime() {
		if(getBeginTime() == null) {
			return null;
		}
		return TimeSpan.addSpanToLDT(beginTime, estimatedDuration.getAcceptableSpan(acceptableDeviation));
	}

	public boolean planDevelopers(List<ResourceView> plannedDevelopers) {
		// TODO Actual developers?
		this.plannedDevelopers = plannedDevelopers;
		return true;              
	}

}
