package com.holo.base;

import java.util.Calendar;
public class BaseDate {
	public static int getweek(){//蔡勒公式
		Calendar cal = Calendar.getInstance();
//		Date date =new Date();
		int year = cal.get(Calendar.YEAR);//date.getYear();
		int month = cal.get(Calendar.MONTH)+1;//date.getMonth();
		int day = cal.get(Calendar.DATE);//date.getDay();
		//System.out.println(year+"-"+month+"-"+day+"=");
		if(month==1||month==2){
			year--;
			month+=12;
		}
		int c = year/100;
		int y = year%100;
		int week = (c/4)-2*c+(y+y/4)+(13*(month+1))/5+day-1;
		week%=7;
		if(week<=0){week+=7;};
		return week;
	}

	public static String getMyday(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR)+"-"+( cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE);
	}

	/*成绩查询中生成结束年级*/
	public static int getEndYear()
	{
		Calendar cal=Calendar.getInstance();//使用日历类
		int to_year=cal.get(Calendar.YEAR);//得到年
		int to_month=cal.get(Calendar.MONTH)+1;
		if(to_month < 8)	//和秋季学期保持一致
		{
			to_year--;
		}
		return to_year;
	}

	/*导入课表生成年份*/
	public static String getTimetableYear()
	{
//		"2014-2015-3"
		Calendar cal=Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH)+1;
		if(month < 6)	//6月开始就可以导入小学期课表
		{
			return (year-1)+"-"+year+"-2";
		}else if(month < 8) //8月开始就可以导入下学期课表
		{
			return (year-1)+"-"+year+"-3";
		}else{
			return (year)+"-"+ (year+1) +"-1";
		}
	}

}
