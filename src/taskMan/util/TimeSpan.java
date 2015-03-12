package taskMan.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
	 */
	public TimeSpan(LocalDateTime time1, LocalDateTime time2 ){
		if(time1 == null || time2 == null)
			throw new IllegalStateException("Time objects are null");

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
	 * Creates a timeSpan based on a duration of minutes.
	 * 
	 * @param 	duration
	 * 			The number of minutes.
	 * @throws	IllegalArgumentException
	 * 			The duration must be a postive one.
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
	
	public TimeSpan(int[] newSpan) throws IllegalArgumentException{
		if(newSpan.length>5)
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
	 * Creates a new TimeSpan objects by adding this timeSpan object with another TimeObject.
	 * 
	 * @param 	newSpan
	 * 			The other TimeSpan object
	 * @return	A new TimeSpan object created by adding 2 timeSpan objects
	 */
	public TimeSpan add(TimeSpan newSpan){
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
	 */
	public TimeSpan getAcceptableSpan(int deviation){
		int span = this.getSpanMinutes();
		int acceptableSpan = span + deviation * (span/100);
		return new TimeSpan(acceptableSpan);
	}
	
	/**
	 * Checks whether this TimeSpan is longer than the other.
	 * 
	 * @param 	other
	 * 			The TimeSpan to compare to.
	 * @return	True if this span is longer.
	 */
	public boolean isLonger(TimeSpan other){
		return this.getSpanMinutes()>=other.getSpanMinutes();
	}
	
	/**
	 * Checks whether this TimeSpan is shorter or equal to the other.
	 * 
	 * @param 	other
	 * 			The TimeSpan to compare to.
	 * @return	True if this span is shorter or equal.
	 */
	public boolean isShorter(TimeSpan other){
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
	 * Get the difference between to TimeSpan's in minutes.
	 * 
	 * @param 	other
	 * 			The other TimeSpan object.
	 * @return	The difference in minutes.
	 */
	public int getDifferenceMinute(TimeSpan other){
		return Math.abs(this.getSpanMinutes() - other.getSpanMinutes());
	}
	
	/**
	 * Returns the difference between 2 TimeSpan objects.
	 * 
	 * @param 	other
	 * 			The other TimeSpan object
	 * @return	The difference as int array.
	 */
	public int[] minus(TimeSpan other){
		if(other.isLonger(this)){
			int newSpan = other.getSpanMinutes()-this.getSpanMinutes();
			return new TimeSpan(newSpan).getSpan();
		}
		else{
			int newSpan = this.getSpanMinutes()-other.getSpanMinutes();
			return new TimeSpan(newSpan).getSpan();
		}
			
	}
}

