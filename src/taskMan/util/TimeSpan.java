package taskMan.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * Creates a timeSpan object.
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel, Eli Vangrieken
 *
 */
public class TimeSpan {

	private final int[] span;

	/**
	 * Construct a TimeSpan object from 2 LocalDateTime objects.
	 * 
	 * @param 	time1
	 * 			The first localDateTime object.
	 * @param 	time2
	 * 			The second localDateTime object.
	 * @throws	IllegalArgumentException
	 * 			An exception is thrown when a null pointer is shown
	 */
	public TimeSpan(LocalDateTime time1, LocalDateTime time2 ) throws IllegalArgumentException {
		if(time1 == null || time2 == null)
			throw new IllegalArgumentException("Time objects are null");

		LocalDateTime fromDateTime = null;
		LocalDateTime toDateTime =null;
		
		if(time2.isAfter(time1)){
			 fromDateTime = time1;
			 toDateTime = time2;
		}
		else{
			 fromDateTime = time2;
			 toDateTime = time1;
		}

			LocalDateTime tempDateTime = LocalDateTime.from( fromDateTime );

			long years = tempDateTime.until( toDateTime, ChronoUnit.YEARS);
			tempDateTime = tempDateTime.plusYears( years );

			long months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS);
			tempDateTime = tempDateTime.plusMonths( months );

			long days = tempDateTime.until( toDateTime, ChronoUnit.DAYS);
			tempDateTime = tempDateTime.plusDays( days );


			long hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS);
			tempDateTime = tempDateTime.plusHours( hours );

			long minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES);
			tempDateTime = tempDateTime.plusMinutes( minutes );
			this.span = new int[] {(int) years, (int) months, (int) days, (int) hours, (int) minutes };
	}

	/**
	 * Creates a TimeSpan object based on a duration in minutes.
	 * 
	 * @param 	duration
	 * 			The number of minutes.
	 * @throws	IllegalArgumentException
	 * 			if the duration is negative.
	 */
	public TimeSpan(int duration) throws IllegalArgumentException{
		if(duration<0)
			throw new IllegalArgumentException("Invalid duration");
		int minutesM = duration%60;
		duration -= minutesM;
		int hoursM =  duration%(24*60);
		duration -= hoursM;
		int dayM = duration%(24*60*30);
		duration -= dayM;
		int monthsM = duration%(24*60*30*12);
		duration -= monthsM;
		int yearsM = duration;
		
		span = new int[] {yearsM/(24*60*30*12),monthsM/(24*60*30),dayM/(24*60),hoursM/60,minutesM};
	}
	
	/**
	 * Creates a TimeSpan object based on an integer array that holds years,
	 * months, days, hours and minutes in that order.
	 * 
	 * @param 	newSpan
	 * 			an integer array representing the time span
	 * @throws 	IllegalArgumentException
	 * 			if the integer array has the wrong dimensions (!= 5)
	 */
	public TimeSpan(int[] newSpan) throws IllegalArgumentException{
		if(newSpan.length != 5)
			throw new IllegalArgumentException("invalid number of arguments");
		for(int i: newSpan)
			if(i<0)
				throw new IllegalArgumentException("values must be positive");
		this.span = newSpan;
	}
	
	/**
	 * Returns the span of time.
	 * 
	 * @return	The span of time.
	 */
	public int[] getSpan(){
		return this.span;
	}
	
	/**
	 * Returns the TimeSpan in minutes.
	 * 
	 * @return	the span in minutes.
	 */
	public int getSpanMinutes(){
		return this.getMinutes()+this.getHours()*60+this.getDays()*60*24+
				this.getMonths()*60*24*30+this.getYears()*60*24*30*12;
	}
	
	/**
	 * Creates a new TimeSpan object by adding this TimeSpan object to another TimeSpan.
	 * 
	 * @param 	newSpan
	 * 			The other TimeSpan object
	 * @return	A new TimeSpan object created by adding 2 timeSpan objects
	 */
	public TimeSpan add(TimeSpan newSpan) throws IllegalArgumentException{
		if(newSpan == null)
			throw new IllegalArgumentException("Invalid newSpan");
		int minutes = this.getMinutes() + newSpan.getMinutes();
		int hours = this.getHours() + newSpan.getHours();
		int days = this.getDays() + newSpan.getDays();
		int months = this.getMonths() + newSpan.getMonths();
		int years = this.getYears() + newSpan.getYears();
		if(minutes>60){
			minutes -= 60;
			hours += 1;
		}
		if(hours>24){
			hours -= 24;
			days += 1;
		}
		if(days>30){
			days -= 30;
			months += 1;
		}
		if(months>12){
			months -= 12;
			years += 1;
		}
		int[] span = new int[]{years,months,days,hours,minutes};
		return new TimeSpan(span);
	}
	
	/**
	 * Returns the number of minutes in the current TimeSpan.
	 * 
	 * @return	the number of minutes;
	 */
	public int getMinutes(){
		return this.getSpan()[4];
	}
	
	/**
	 * Returns the number of hours in the current TimeSpan.
	 * 
	 * @return	the number of hours;
	 */
	public int getHours(){
		return this.getSpan()[3];
	}
	
	/**
	 * Returns the number of days in the current TimeSpan.
	 * 
	 * @return	the number of days;
	 */
	public int getDays(){
		return this.getSpan()[2];
	}
	
	/**
	 * Returns the number of months in the current TimeSpan.
	 * 
	 * @return	the months of minutes;
	 */
	public int getMonths(){
		return this.getSpan()[1];
	}
	
	/**
	 * Returns the number of years in the current TimeSpan.
	 * 
	 * @return	the number of years;
	 */
	public int getYears(){
		return this.getSpan()[0];
	}
	
	/**
	 * Creates a new TimeSpan object from a given deviation.
	 * 
	 * @param 	deviation
	 * 			The deviation to be used.
	 * @return	An new TimeSpan object representing a acceptable span.
	 * @throws	IllegalArgumentException
	 * 			if the deviation is negative
	 */
	public TimeSpan getAcceptableSpan(int deviation) throws IllegalArgumentException {
		if(deviation<0) {
			throw new IllegalArgumentException("Invalid deviaiton");
		}
		int span = getSpanMinutes();
		float dev = deviation;
		float acceptableSpan = span + (span * (dev/100));
		return new TimeSpan((int) acceptableSpan);
	}
	
	/**
	 * Checks whether this TimeSpan is longer than the other.
	 * 
	 * @param 	other
	 * 			The TimeSpan to compare to.
	 * @return	True if this span is longer.
	 * 			False if other == null
	 */
	public boolean isLonger(TimeSpan other) {
		if(other == null)
			return false;
		return this.getSpanMinutes()>=other.getSpanMinutes();
	}
	
	/**
	 * Checks whether this TimeSpan is shorter or equal to the other.
	 * 
	 * @param 	other
	 * 			The TimeSpan to compare to.
	 * @return	True if this span is shorter or equal.
	 * 			False if other == null
	 */
	public boolean isShorter(TimeSpan other){
		if(other == null)
			return false;
		return this.getSpanMinutes()<=other.getSpanMinutes();
	}
	
	/**
	 * Checks whether this TimeSpan has a zero value.
	 * 
	 * @return	True if there is a zero value.
	 */
	public boolean isZero() {
		for(int slot : span) {
			if(slot != 0)
				return false;
		}
		return true;
	}
	
	/**
	 * Get the difference between to TimeSpan objects in minutes.
	 * 
	 * @param 	other
	 * 			The other TimeSpan object.
	 * @return	The difference in minutes.
	 * 			-1 if other == null
	 */
	public int getDifferenceMinute(TimeSpan other) {
		if (other == null)
			return -1;
		return Math.abs(this.getSpanMinutes() - other.getSpanMinutes());
	}
	
	/**
	 * Returns the difference between 2 TimeSpan objects.
	 * 
	 * @param 	other
	 * 			The other TimeSpan object
	 * @return	The difference as int array.
	 * @throws	IllegalArgumentException
	 * 			if other == null
	 */
	public int[] minus(TimeSpan other) throws IllegalArgumentException {
		if(other == null)
			throw new IllegalArgumentException("Invalid other");
		if(other.isLonger(this)){
			int newSpan = other.getSpanMinutes()-this.getSpanMinutes();
			return new TimeSpan(newSpan).getSpan();
		}
		else{
			int newSpan = this.getSpanMinutes()-other.getSpanMinutes();
			return new TimeSpan(newSpan).getSpan();
		}
			
	}
	
	/**
	 * Returns the amount of working minutes between two timestamps.
	 * A working day is from 8 to 17 on weekdays.
	 * @param	startTime
	 * 			The given start time
	 * @param	endTime
	 * 			The given end time
	 * @return	Returns the amount of working minutes between two timestamps
	 * @throws	IllegalArgumentException
	 * 			Throws an exception when a null pointer is found in the timestamps
	 */
	public static int getDifferenceWorkingMinutes(LocalDateTime startTime,
			LocalDateTime endTime) throws IllegalArgumentException {
		
		if(startTime == null)
			throw new IllegalArgumentException("Invalid start time");
		if(endTime == null)
			throw new IllegalArgumentException("Invalid end time");
		if(endTime.isBefore(startTime)){
			return 0;
		}
		
		int workDayStart = 8;
		int workDayEnd = 16;
		
		if(startTime.equals(endTime)) {
			return 0;
		}

		LocalDateTime timeToCallItADayEnd = LocalDateTime.of(endTime.getYear(), endTime.getMonth(), endTime.getDayOfMonth(), workDayEnd, 0);
		if(endTime.isAfter(timeToCallItADayEnd)) {
			return getDifferenceWorkingMinutes(startTime, timeToCallItADayEnd);
		}

		LocalDateTime timeToBeginStart = LocalDateTime.of(startTime.getYear(), startTime.getMonth(), startTime.getDayOfMonth(), workDayStart, 0);
		if(startTime.isBefore(timeToBeginStart)) {
			return getDifferenceWorkingMinutes(timeToBeginStart, endTime);
		}
		
		if(    startTime.getYear() 		 == endTime.getYear() 
			&& startTime.getMonthValue() == endTime.getMonthValue()
			&& startTime.getDayOfMonth() == endTime.getDayOfMonth()) {
			// TODO hier kan voor pauze worden gecontroleerd
			return (int) startTime.until(endTime, ChronoUnit.MINUTES);
		}

		long minutesLeftToday;
		LocalDateTime timeToCallItADayStart = LocalDateTime.of(startTime.getYear(), startTime.getMonth(), startTime.getDayOfMonth(), workDayEnd, 0);
		LocalDateTime midnightStart = LocalDateTime.of(startTime.getYear(), startTime.getMonth(), startTime.getDayOfMonth(), 0, 0).plusDays(1);
		switch(startTime.getDayOfWeek()) {
		case SATURDAY:
			minutesLeftToday = 0;
			break;
		case SUNDAY:
			minutesLeftToday = 0;
			break;
		default:
			minutesLeftToday = startTime.until(timeToCallItADayStart, ChronoUnit.MINUTES);
			break;
		}
		
		return (int) minutesLeftToday + getDifferenceWorkingMinutes(midnightStart.plusHours(workDayStart), endTime);
		
		
//		DayOfWeek beginDay = startTime.getDayOfWeek();
//		DayOfWeek endDay = endTime.getDayOfWeek();
//		int workDayStart = 8;
//		int workDayEnd = 17;
//
//		int beginDayValue = 0, endDayValue = 0;
//		switch (beginDay) {
//		case MONDAY:
//			beginDayValue = 1;
//			break;
//		case TUESDAY:
//			beginDayValue = 2;
//			break;
//		case WEDNESDAY:
//			beginDayValue = 3;
//			break;
//		case THURSDAY:
//			beginDayValue = 4;
//			break;
//		case FRIDAY:
//			beginDayValue = 5;
//			break;
//		case SATURDAY:
//			beginDayValue = 6;
//			break;
//		case SUNDAY:
//			beginDayValue = 7;
//			break;
//		}
//		switch (endDay) {
//		case MONDAY:
//			endDayValue = 1;
//			break;
//		case TUESDAY:
//			endDayValue = 2;
//			break;
//		case WEDNESDAY:
//			endDayValue = 3;
//			break;
//		case THURSDAY:
//			endDayValue = 4;
//			break;
//		case FRIDAY:
//			endDayValue = 5;
//			break;
//		case SATURDAY:
//			endDayValue = 6;
//			break;
//		case SUNDAY:
//			endDayValue = 7;
//			break;
//		}
//
//		int startDayHour, endDayHour;
//		startDayHour = startTime.getHour();
//		endDayHour = endTime.getHour();
//
//		int startDayMinute, endDayMinute;
//		startDayMinute = startTime.getMinute();
//		endDayMinute = endTime.getMinute();
//
//		long minutesBetweenTimeStamps;
//		minutesBetweenTimeStamps = startTime.until(endTime, ChronoUnit.MINUTES);
//		
//		if(minutesBetweenTimeStamps <= 24 * 60 && beginDay == endDay) {
//			if(startDayHour < workDayStart) {
//				startDayHour = workDayStart;
//				startDayMinute = 0;
//			}
//			if(endDayHour >= workDayEnd) {
//				endDayHour = workDayEnd;
//				endDayMinute = 0;
//			}
//			return new TimeSpan((endDayHour - startDayHour) * 60 + (endDayMinute - startDayMinute));
//		}
//
//		long workingMinutesFullWeeks;
//		int workingMinutesFullDaysAfterFullWeeks, workingMinutesFullDaysBeforeFullWeeks, workingMinutesInLastDay, workingMinutesInFirstDay;
//		int pb, pc, pd, pe;
//
//		pb = (endDayValue - 1) * 24 * 60; // amount of minutes in B
//		pc = (7 - beginDayValue) * 24 * 60; // amount of minutes in C
//		pd = (endDayHour * 60) + endDayMinute; // amount of minutes in D
//		pe = (24 - startDayHour) * 60 - startDayMinute; // amount of minutes in E
//
//		// A = amount of working minutes in full weeks
//		workingMinutesFullWeeks = ((minutesBetweenTimeStamps - pb - pc - pd - pe) * 5 * 8)
//				/ (7 * 24);
//		if(workingMinutesFullWeeks < 0)
//			workingMinutesFullWeeks = 0;
//
//		// B = amount of working minutes in full days after the full weeks
//		if (endDayValue == 1)
//			workingMinutesFullDaysAfterFullWeeks = 0;
//		else
//			workingMinutesFullDaysAfterFullWeeks = (endDayValue - 1) * 8 * 60;
//		if (workingMinutesFullDaysAfterFullWeeks == 6 * 8 * 60)
//			workingMinutesFullDaysAfterFullWeeks = 5 * 8 * 60;
//
//		// C = amount of working minutes in full days before the full weeks
//		if (beginDayValue == 5 || beginDayValue == 6 || beginDayValue == 7)
//			workingMinutesFullDaysBeforeFullWeeks = 0;
//		else
//			workingMinutesFullDaysBeforeFullWeeks = (5 - 1) * 8 * 60;
//
//		// D = amount of working minutes in the last day (even if it is a full
//		// day)
//		if (endDayValue == 6 || endDayValue == 7) {
//			workingMinutesInLastDay = 0;
//		} else if (endDayHour < workDayStart) {
//			workingMinutesInLastDay = 0;
//		} else {
//			workingMinutesInLastDay = (endDayHour - workDayStart) * 60
//					+ endDayMinute;
//		}
//		if (workingMinutesInLastDay > 8 * 60)
//			workingMinutesInLastDay = 8 * 60;
//
//		// E = amount of working minutes in the first day (even if it is a full
//		// day)
//		if (beginDayValue == 6 || beginDayValue == 7) {
//			workingMinutesInFirstDay = 0;
//		} else if (startDayHour >= workDayEnd) {
//			workingMinutesInFirstDay = 0;
//		} else {
//			workingMinutesInFirstDay = (workDayEnd - startDayHour) * 60
//					- startDayMinute;
//		}
//		if (workingMinutesInFirstDay > 8 * 60)
//			workingMinutesInFirstDay = 8 * 60;
//		
//		if(minutesBetweenTimeStamps <= 48 * 60) {
//			return new TimeSpan(workingMinutesInFirstDay
//					+ workingMinutesInLastDay);
//		}
//		
//		if(minutesBetweenTimeStamps - pe - pd <= 7 * 24 * 60) {
//			int amountOfDaysBetweenExtremeDays = (int) (minutesBetweenTimeStamps - pe - pd) / (24 * 60);
//			int amountOfWorkingDays = 0;
//			int testDay = beginDayValue + 1;
//			for(int i = 0 ; i < amountOfDaysBetweenExtremeDays ; i++) {
//				if(Math.floorMod(testDay,7) != 6 && Math.floorMod(testDay,7) != 0)
//					amountOfWorkingDays++;
//				testDay++;
//			}
//			return new TimeSpan((int) amountOfWorkingDays * 8 * 60 + workingMinutesInLastDay
//					+ workingMinutesInFirstDay);
//		}
//		
//		return new TimeSpan((int) workingMinutesFullWeeks
//				+ workingMinutesFullDaysAfterFullWeeks
//				+ workingMinutesFullDaysBeforeFullWeeks
//				+ workingMinutesInLastDay + workingMinutesInFirstDay);
	}
	
	public static LocalDateTime addSpanToLDT(LocalDateTime offset, TimeSpan span) {

		int minutes = span.getSpanMinutes();
		
		int workDayStart = 8;
		int workDayEnd = 16;
		if(minutes == 0) {
			return offset;
		}

		LocalDateTime timeToCallItADay = LocalDateTime.of(offset.getYear(), offset.getMonthValue(), offset.getDayOfMonth(), workDayEnd, 0);
		
		LocalDateTime midnight = LocalDateTime.of(offset.getYear(), offset.getMonthValue(), offset.getDayOfMonth(), 0, 0).plusDays(1);
		
		if(offset.isAfter(timeToCallItADay)) {
			return addSpanToLDT(midnight.plusHours(workDayStart), span);
		}

		long minutesLeftToday;
		switch(offset.getDayOfWeek()) {
		case SATURDAY:
			minutesLeftToday = 0;
			break;
		case SUNDAY:
			minutesLeftToday = 0;
			break;
		default:
			minutesLeftToday = offset.until(timeToCallItADay, ChronoUnit.MINUTES);
			break;
		}
		
		if(minutesLeftToday <= minutes) {
			return addSpanToLDT(midnight.plusHours(workDayStart), new TimeSpan((int) (minutes - minutesLeftToday)));
		} else {
			return addSpanToLDT(offset.plusMinutes(minutes),new TimeSpan(0));
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(span);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSpan other = (TimeSpan) obj;
		if (!Arrays.equals(span, other.span))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "" + getSpanMinutes();
	}
}

