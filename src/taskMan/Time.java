package taskMan;
/**
 * @Deprecated Use LocalTime, LocalDate and LocalDateTime instead
 */
@Deprecated
public class Time {
	int time;
	
	/**
	 * @param minutes Create a Time object with the minutes format already supplied
	 */
	public Time(int minutes) { this.time = minutes; }
	
	public int getTime() { return time; }
	
	public String toString() { 
		//TODO
		return Integer.toString(time); }
}
