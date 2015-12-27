package com.holo.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;

public class TimeTableList {
	
	ArrayList<ArrayList<Map<String, Object>>> course ;
	private short MaxCourse = 6;
	
	public TimeTableList(Cursor cursor)
	{
		course = new ArrayList<>();
		for(short i=0;i<MaxCourse;i++)
		{
			ArrayList<Map<String, Object>> c = new ArrayList<>();
			course.add(c);
		}
		
		if(cursor.moveToFirst())
		{
			addCourse(cursor);
		}
		
		while(cursor.moveToNext())
		{
			addCourse(cursor);
		}
	}
	
	private void addCourse(Cursor cursor) 
	{
		int position = cursor.getInt(cursor.getColumnIndex("lesson_no"))-1;
		
		HashMap<String , Object> course_detail = new HashMap <>();
		course_detail.put( "course_id", cursor.getString(cursor.getColumnIndex("course_id")) );
		course_detail.put( "course_name",cursor.getString(cursor.getColumnIndex("course_name")) );
		course_detail.put( "place",cursor.getString(cursor.getColumnIndex("place")) );
		course_detail.put( "teachers",cursor.getString(cursor.getColumnIndex("teachers")) );
		course_detail.put( "weeks",cursor.getString(cursor.getColumnIndex("weeks")) );
		course_detail.put( "course_type",cursor.getString(cursor.getColumnIndex("course_type")) );
		
		(course.get(position)).add(course_detail);
	}

	public  ArrayList<Map<String, Object>> getCourseList(int lesson_id) {
		// TODO Auto-generated method stub
		return course.get(lesson_id);
	}
	
}
