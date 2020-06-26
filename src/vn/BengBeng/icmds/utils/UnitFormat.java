package vn.BengBeng.icmds.utils;

public class UnitFormat {
	
	private String second, minute, hour, day;
	
	
	public UnitFormat(String second, String minute, String hour, String day) {
		this.second = second;
		this.minute = minute;
		this.hour = hour;
		this.day = day;
	}
	
	
	/**
	 * Trả về định dạng giây.
	 * @return
	 */
	public String getSecond() {
		return second;
	}
	
	/**
	 * Trả về định dạng phút.
	 * @return
	 */
	public String getMinute() {
		return minute;
	}
	
	/**
	 * Trả về định dạng giờ.
	 * @return
	 */
	public String getHour() {
		return hour;
	}
	
	/**
	 * Trả về định dạng ngày.
	 * @return
	 */
	public String getDay() {
		return day;
	}
	
}
