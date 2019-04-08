package com.mt.system.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理工具类
 * 
 * @author tangmignkun
 */

public class DateUtils
{

	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	/**
	 * 获得服务器当前日期及时间，以格式为：yyyy-MM-dd HH:mm:ss的日期字符串形式返回
	 */
	public static String getDateTime()
	{
		try
		{
			Calendar cale = Calendar.getInstance();
			return sdf3.format(cale.getTime());
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 获得服务器当前日期及时间 以格式为：pattern的日期字符串形式返回
	 * @param pattern 日期格式
	 * @return
	 */
	public static String getDateTime(String pattern)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Calendar cale = Calendar.getInstance();
			return sdf.format(cale.getTime());
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 获得服务器当前日期及时间，以格式为：yyyy-MM-dd HH:mm的日期字符串形式返回
	 */
	public static String getDateTimeMinute()
	{
		try
		{
			Calendar cale = Calendar.getInstance();
			return sdf4.format(cale.getTime());
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 获得服务器当前日期，以格式为：yyyy-MM-dd的日期字符串形式返回
	 */
	public static String getDate()
	{
		try
		{
			Calendar cale = Calendar.getInstance();
			return sdf1.format(cale.getTime());
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 获得服务器当前时间，以格式为：HH:mm:ss的日期字符串形式返回
	 */
	public static String getTime()
	{
		String temp = "";
		try
		{
			Calendar cale = Calendar.getInstance();
			temp += sdf2.format(cale.getTime());
			return temp;
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 获取系统时间的时间戳 精确到毫秒
	 */
	public static String currentTimeMillis()
	{
		try
		{
			return String.valueOf(System.currentTimeMillis());
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 时间戳转时间,以格式为：pattern的日期字符串形式返回
	 * @param timeStamp 时间戳
	 * @param pattern 转成格式
	 * @return
	 */
	public static String stampToDate(Long timeStamp,String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date(timeStamp));   // 时间戳转换成时间
	}

	/**
	 * 时间字符串（自定义格式）转时间字符串（自定义格式）
	 * @param time 时间字符串
	 * @param oldPattern 格式
	 * @param newPattern 转成格式
	 * @return
	 */
	public static String convertStringTime(String time,String oldPattern,String newPattern){
		try{
			Date date = new SimpleDateFormat(oldPattern).parse(time);
			return new SimpleDateFormat(newPattern).format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 验证日期格式：（00:00） or (0:00) or (00:00:00) or (0:00:00)
	 */
	public static boolean checkTime(String time){
		try{
			if (time .matches("(\\d):([0-5]\\d{1})"))
				return true;
			else if (time .matches("(0\\d{1}|1\\d{1}|2[0-3]):([0-5]\\d{1})"))
				return true;
			else if(time .matches("(\\d):([0-5]\\d{1}):([0-5]\\d{1})"))
				return true;
			else if(time .matches("(0\\d{1}|1\\d{1}|2[0-3]):([0-5]\\d{1}):([0-5]\\d{1})"))
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * 当前月份最后一天.
	 * @return yyyy-MM-dd
	 */
	public static String getCurrentMonthLastDay(){
		//获取当前月最后一天
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		return sdf1.format(ca.getTime());
	}

	/***
	 * 当前月份第一天.
	 * @return  yyyy-MM-dd
	 */
	public static String getCurrentMonthFirstDay(){
		//获取当前月最后一天
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
		return sdf1.format(c.getTime());
	}

	/**
	 * 时间字符串转Date类型
	 * @param time 时间字符串
	 * @param pattern 时间字符串格式
	 * @return
	 */
	public static Date convertStringDate(String time,String pattern){
		try{
			SimpleDateFormat sd1 = new SimpleDateFormat(pattern);
			return sd1.parse(time);
		}catch (ParseException pe){
			return null;
		}
	}

}
