package taskMan.test.unit;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;

import taskMan.util.TimeSpan;

public class TimeSpanTest {
	
	private final LocalDateTime monday152980 = LocalDateTime.of(2015,  2, 9, 8, 0),
			monday1529810 = LocalDateTime.of(2015, 2, 9, 8, 10),
			thursday1521280 = LocalDateTime.of(2015, 2, 12, 8, 0),
			friday15213160 = LocalDateTime.of(2015, 2, 13, 16, 0),
			monday1521680 = LocalDateTime.of(2015, 2, 16, 8, 0),
			wednesday152181225 = LocalDateTime.of(2015,  2, 18, 12, 25),
			monday15291555 = LocalDateTime.of(2015, 2, 9, 15, 55),
			tuesday1521085 = LocalDateTime.of(2015, 2, 10, 8, 5);

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
		
		TimeSpan goodTS3daysFromLDT = new TimeSpan(monday152980,thursday1521280);
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
		new TimeSpan(-1);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromIntArrayBigArrayFailTest() {
		int[] badArray = new int[]{0, 0, 0, 0, 0, 0};
		new TimeSpan(badArray);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromIntArraySmallArrayFailTest() {
		int[] badArray = new int[]{0, 0, 0, 0};
		new TimeSpan(badArray);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromIntArrayBadValuesTest() {
		int[] badArray = new int[]{0, -1, 0, 0, 0};
		new TimeSpan(badArray);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromLocalDateTimeBadLDTFailTest() {
		new TimeSpan(monday152980,null);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constructorFromLocalDateTimeBadLDTFailTest2() {
		new TimeSpan(null,monday152980);
		
	}
	
	@Test
	public void GetDiffWorkingMinSuccesTest() {
		
		int threeDays = 3*8*60;
		int sevenDaysFourHours25Minutes = 7*8*60+4*60+25;
		
		assertEquals(10,TimeSpan.getDifferenceWorkingMinutes(
				monday152980, monday1529810));
		assertEquals(0,TimeSpan.getDifferenceWorkingMinutes(
				monday1529810, monday152980));
		assertEquals(0,TimeSpan.getDifferenceWorkingMinutes(
				monday152980, monday152980));
		assertEquals(threeDays,TimeSpan.getDifferenceWorkingMinutes(
				monday152980, thursday1521280));
		assertEquals(0,TimeSpan.getDifferenceWorkingMinutes(
				thursday1521280, monday152980));
		assertEquals(0,TimeSpan.getDifferenceWorkingMinutes(
				friday15213160, monday1521680));
		assertEquals(sevenDaysFourHours25Minutes - 60,TimeSpan.getDifferenceWorkingMinutes( // - 60 to take developer pauze into account
				monday152980, wednesday152181225));
		assertEquals(0,TimeSpan.getDifferenceWorkingMinutes(
				wednesday152181225, monday152980));
		assertEquals(10,TimeSpan.getDifferenceWorkingMinutes(
				monday15291555,tuesday1521085));
	}
	
	@Test
	public void GetDiffWorkingMinAfterWorkdayEndSuccesTest() {
		LocalDateTime end = LocalDateTime.of(2015,  2, 9, 18, 0);
		assertEquals(480,TimeSpan.getDifferenceWorkingMinutes(
				monday152980, end));
	}
	
	@Test
	public void GetDiffWorkingMinBeforeWorkdaySuccesTest(){
		LocalDateTime start = LocalDateTime.of(2015,  2, 9, 7, 0);
		assertEquals(10,TimeSpan.getDifferenceWorkingMinutes(
				start, monday1529810));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void GetDiffWorkingMinBadLDTFailTest() {
		TimeSpan.getDifferenceWorkingMinutes(thursday1521280, null);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void GetDiffWorkingMinBadLDTFailTest2() {
		TimeSpan.getDifferenceWorkingMinutes(null, thursday1521280);
		
	}
	
	@Test
	public void addSuccesTest() {
		TimeSpan original = new TimeSpan(0);
		TimeSpan toAdd1 = new TimeSpan(0);
		TimeSpan toAdd2 = new TimeSpan(new int[]{0,0,0,0,10});
		TimeSpan toAdd3 = new TimeSpan(monday152980,thursday1521280);
		
		TimeSpan result1 = original.add(toAdd1);
		
		assertEquals(0,result1.getSpanMinutes());
		assertEquals(0,result1.getMinutes());
		assertEquals(0,result1.getHours());
		assertEquals(0,result1.getDays());
		assertEquals(0,result1.getMonths());
		assertEquals(0,result1.getYears());
		assertEquals(0,result1.getSpanMinutes());
		assertTrue(result1.isZero());
		
		TimeSpan result2 = original.add(toAdd2);
		
		assertEquals(10,result2.getSpanMinutes());
		assertEquals(10,result2.getMinutes());
		assertEquals(0,result2.getHours());
		assertEquals(0,result2.getDays());
		assertEquals(0,result2.getMonths());
		assertEquals(0,result2.getYears());
		assertEquals(10,result2.getSpanMinutes());
		assertFalse(result2.isZero());

		int threeDays = 3*24*60;
		TimeSpan result3 = original.add(toAdd3);
		
		assertEquals(threeDays,result3.getSpanMinutes());
		assertEquals(0,result3.getMinutes());
		assertEquals(0,result3.getHours());
		assertEquals(3,result3.getDays());
		assertEquals(0,result3.getMonths());
		assertEquals(0,result3.getYears());
		assertEquals(threeDays,result3.getSpanMinutes());
		assertFalse(result3.isZero());
		
	}
	
	@Test
	public void addSuccesOverBorderValueTest(){
		TimeSpan original = new TimeSpan(50);
		TimeSpan toAdd1 = new TimeSpan(15);
		TimeSpan toAdd2 = new TimeSpan(60);
		TimeSpan toAdd3 = new TimeSpan(new int[]{0,0,0,22,0});
		TimeSpan toAdd4 = new TimeSpan(new int[]{0,0,30,0,0});
		TimeSpan toAdd5 = new TimeSpan(new int[]{0,12,0,0,0});
		
		assertEquals(50, original.getMinutes());
		TimeSpan result1 = original.add(toAdd1);
		assertEquals(1, result1.getHours());
		assertEquals(5, result1.getMinutes());
		
		TimeSpan result2 = result1.add(toAdd2);
		assertEquals(2, result2.getHours());
		assertEquals(5, result2.getMinutes());
		
		TimeSpan result3 = result2.add(toAdd3);
		assertEquals(1,result3.getDays());
		assertEquals(0, result3.getHours());
		assertEquals(5, result3.getMinutes());
		
		TimeSpan result4 = result3.add(toAdd4);
		assertEquals(1, result4.getMonths());
		assertEquals(1, result4.getDays());
		assertEquals(0,result4.getHours());
		assertEquals(5, result4.getMinutes());
		
		assertEquals(0, result4.getYears());
		TimeSpan result5 = result4.add(toAdd5);
		assertEquals(1, result5.getYears());
		assertEquals(1, result5.getMonths());
		assertEquals(1, result5.getDays());
		assertEquals(0,result5.getHours());
		assertEquals(5, result5.getMinutes());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addBadArgumentFailTest() {
		TimeSpan original = new TimeSpan(0);
		TimeSpan toAdd = null;
		
		assertEquals(0,original.add(toAdd).getSpanMinutes());
		
	}
	
	@Test
	public void getAcceptableTimeSpanSuccesTest() {
		TimeSpan original = new TimeSpan(10);

		assertEquals(10,original.getAcceptableSpan(0).getSpanMinutes());
		assertEquals(15,original.getAcceptableSpan(50).getSpanMinutes());
		assertEquals(20,original.getAcceptableSpan(100).getSpanMinutes());
		assertEquals(100,original.getAcceptableSpan(900).getSpanMinutes());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getAcceptableTimeSpanBadArgumentFailTest() {
		TimeSpan original = new TimeSpan(10);

		assertEquals(10,original.getAcceptableSpan(-10).getSpanMinutes());
		
	}
	
	@Test
	public void isShorterSuccesTest() {
		TimeSpan longer = new TimeSpan(10);
		TimeSpan shorter = new TimeSpan(5);
		TimeSpan equal = shorter;

		assertFalse(longer.isShorter(shorter));
		assertTrue(shorter.isShorter(longer));
		assertTrue(shorter.isShorter(equal));
		
	}
	
	@Test
	public void isLongerSuccesTest() {
		TimeSpan longer = new TimeSpan(10);
		TimeSpan shorter = new TimeSpan(5);
		TimeSpan equal = shorter;

		assertFalse(shorter.isLonger(longer));
		assertTrue(longer.isLonger(shorter));
		assertTrue(longer.isLonger(equal));
		
	}
	
	@Test
	public void isShorterBadArgumentFailTest() {
		TimeSpan original = new TimeSpan(10);

		assertFalse(original.isShorter(null));
		
	}
	
	@Test
	public void isLongerBadArgumentFailTest() {
		TimeSpan original = new TimeSpan(10);

		assertFalse(original.isLonger(null));
		
	}
	
	@Test
	public void getDifferenceMinuteSuccesTest() {
		TimeSpan original = new TimeSpan(10);

		assertEquals(10,original.getDifferenceMinute(new TimeSpan(0)));
		assertEquals(10,original.getDifferenceMinute(new TimeSpan(20)));
		assertEquals(0,original.getDifferenceMinute(new TimeSpan(10)));
		
	}
	
	@Test
	public void getDifferenceMinuteBadArgumentFailTest() {
		TimeSpan original = new TimeSpan(10);

		assertTrue(original.getDifferenceMinute(null) == -1);
		
	}
	
	@Test
	public void minusSuccessTest() {
		TimeSpan original = new TimeSpan(10);

		assertTrue(Arrays.equals(new int[]{0,0,0,0,10},original.minus(new TimeSpan(0))));
		assertTrue(Arrays.equals(new int[]{0,0,0,0,10},original.minus(new TimeSpan(20))));
		assertTrue(Arrays.equals(new int[]{0,0,0,0,0},original.minus(new TimeSpan(10))));
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void minusBadArgumentFailTest() {
		TimeSpan original = new TimeSpan(10);

		assertEquals(10,original.minus(null));
		
	}
	
}
