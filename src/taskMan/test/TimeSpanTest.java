package taskMan.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

import taskMan.util.TimeSpan;

public class TimeSpanTest {
	
	private final LocalDateTime monday152980 = LocalDateTime.of(2015,  2, 9, 8, 0),
			monday1529810 = LocalDateTime.of(2015, 2, 9, 8, 10),
			monday1521280 = LocalDateTime.of(2015, 2, 12, 8, 0),
			monday15213160 = LocalDateTime.of(2015, 2, 13, 16, 0),
			monday1521680 = LocalDateTime.of(2015, 2, 16, 8, 0);

	@Test
	public void constructorFromDurationSuccestest() {

		int threeDays = 3*24*60;

		TimeSpan goodTSZero = new TimeSpan(0);
		assertEquals(0,goodTSZero.getMinutes());
		assertEquals(0,goodTSZero.getHours());
		assertEquals(0,goodTSZero.getDays());
		assertEquals(0,goodTSZero.getMonths());
		assertEquals(0,goodTSZero.getYears());
		assertEquals(0,goodTSZero.getSpanMinutes());
		assertTrue(goodTSZero.isZero());
		
		TimeSpan goodTS10Minutes = new TimeSpan(10);
		assertEquals(10,goodTS10Minutes.getMinutes());
		assertEquals(0,goodTS10Minutes.getHours());
		assertEquals(0,goodTS10Minutes.getDays());
		assertEquals(0,goodTS10Minutes.getMonths());
		assertEquals(0,goodTS10Minutes.getYears());
		assertEquals(10,goodTS10Minutes.getSpanMinutes());
		assertFalse(goodTS10Minutes.isZero());
		
		TimeSpan goodTS3days = new TimeSpan(threeDays);
		assertEquals(0,goodTS3days.getMinutes());
		assertEquals(0,goodTS3days.getHours());
		assertEquals(3,goodTS3days.getDays());
		assertEquals(0,goodTS3days.getMonths());
		assertEquals(0,goodTS3days.getYears());
		assertEquals(threeDays,goodTS3days.getSpanMinutes());
		assertFalse(goodTS3days.isZero());
	}
	
	@Test
	public void constructorFromIntArraySuccestest() {

		int threeDays = 3*24*60;
		int[] zeros = new int[]{0, 0, 0, 0, 0};

		TimeSpan goodTSZero = new TimeSpan(zeros);
		assertEquals(0,goodTSZero.getMinutes());
		assertEquals(0,goodTSZero.getHours());
		assertEquals(0,goodTSZero.getDays());
		assertEquals(0,goodTSZero.getMonths());
		assertEquals(0,goodTSZero.getYears());
		assertEquals(0,goodTSZero.getSpanMinutes());
		assertTrue(goodTSZero.isZero());
		
		int[] tenMinutes = new int[]{0, 0, 0, 0, 10};
		TimeSpan goodTS10Minutes = new TimeSpan(tenMinutes);
		assertEquals(10,goodTS10Minutes.getMinutes());
		assertEquals(0,goodTS10Minutes.getHours());
		assertEquals(0,goodTS10Minutes.getDays());
		assertEquals(0,goodTS10Minutes.getMonths());
		assertEquals(0,goodTS10Minutes.getYears());
		assertEquals(10,goodTS10Minutes.getSpanMinutes());
		assertFalse(goodTS10Minutes.isZero());

		int[] threeDaysArray = new int[]{0, 0, 3, 0, 0};
		TimeSpan goodTS3days = new TimeSpan(threeDaysArray);
		assertEquals(0,goodTS3days.getMinutes());
		assertEquals(0,goodTS3days.getHours());
		assertEquals(3,goodTS3days.getDays());
		assertEquals(0,goodTS3days.getMonths());
		assertEquals(0,goodTS3days.getYears());
		assertEquals(threeDays,goodTS3days.getSpanMinutes());
		assertFalse(goodTS3days.isZero());
	}
	
	@Test
	public void constructorFromLocalDateTimeSuccesTest() {
		
		int threeDays = 3*24*60;
		
		TimeSpan goodTSZeroFromLDT = new TimeSpan(monday152980,monday152980);
		assertEquals(0,goodTSZeroFromLDT.getMinutes());
		assertEquals(0,goodTSZeroFromLDT.getHours());
		assertEquals(0,goodTSZeroFromLDT.getDays());
		assertEquals(0,goodTSZeroFromLDT.getMonths());
		assertEquals(0,goodTSZeroFromLDT.getYears());
		assertEquals(0,goodTSZeroFromLDT.getSpanMinutes());
		assertTrue(goodTSZeroFromLDT.isZero());
		
		TimeSpan goodTS10MinutesFromLDT = new TimeSpan(monday152980,monday1529810);
		assertEquals(10,goodTS10MinutesFromLDT.getMinutes());
		assertEquals(0,goodTS10MinutesFromLDT.getHours());
		assertEquals(0,goodTS10MinutesFromLDT.getDays());
		assertEquals(0,goodTS10MinutesFromLDT.getMonths());
		assertEquals(0,goodTS10MinutesFromLDT.getYears());
		assertEquals(10,goodTS10MinutesFromLDT.getSpanMinutes());
		assertFalse(goodTS10MinutesFromLDT.isZero());
		
		TimeSpan goodTS10MinutesFromReverseLDT = new TimeSpan(monday1529810,monday152980);
		assertEquals(10,goodTS10MinutesFromReverseLDT.getMinutes());
		assertEquals(0,goodTS10MinutesFromReverseLDT.getHours());
		assertEquals(0,goodTS10MinutesFromReverseLDT.getDays());
		assertEquals(0,goodTS10MinutesFromReverseLDT.getMonths());
		assertEquals(0,goodTS10MinutesFromReverseLDT.getYears());
		assertEquals(10,goodTS10MinutesFromReverseLDT.getSpanMinutes());
		assertFalse(goodTS10MinutesFromReverseLDT.isZero());
		
		TimeSpan goodTS3daysFromLDT = new TimeSpan(monday152980,monday1521280);
		assertEquals(0,goodTS3daysFromLDT.getMinutes());
		assertEquals(0,goodTS3daysFromLDT.getHours());
		assertEquals(3,goodTS3daysFromLDT.getDays());
		assertEquals(0,goodTS3daysFromLDT.getMonths());
		assertEquals(0,goodTS3daysFromLDT.getYears());
		assertEquals(threeDays,goodTS3daysFromLDT.getSpanMinutes());
		assertFalse(goodTS3daysFromLDT.isZero());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromDurationFailTest() {
		TimeSpan badTS = new TimeSpan(-1);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromIntArrayBigArrayFailTest() {
		int[] badArray = new int[]{0, 0, 0, 0, 0, 0};
		TimeSpan badTS = new TimeSpan(badArray);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromIntArraySmallArrayFailTest() {
		int[] badArray = new int[]{0, 0, 0, 0};
		TimeSpan badTS = new TimeSpan(badArray);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromIntArrayBadValuesTest() {
		int[] badArray = new int[]{0, -1, 0, 0, 0};
		TimeSpan badTS = new TimeSpan(badArray);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromLocalDateTimeBadLDTFailTest() {
		TimeSpan badTS = new TimeSpan(monday152980,null);
		
	}
	
	@Test
	public void GetDiffWorkingMinSuccesTest() {
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void GetDiffWorkingMinBadLDTFailTest() {
		TimeSpan.getDifferenceWorkingMinutes(monday1521280, null);
		
	}
	

}
