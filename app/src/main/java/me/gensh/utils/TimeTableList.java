package me.gensh.utils;

import me.gensh.database.DBTimetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeTableList {
    private ArrayList<ArrayList<Map<String, Object>>> course;
    private static short MaxCourse = 6;

    public TimeTableList(List<DBTimetable> listSrc) {
        course = new ArrayList<>();
        for (short i = 0; i < MaxCourse; i++) {
            ArrayList<Map<String, Object>> c = new ArrayList<>();
            course.add(c);
        }

        for (DBTimetable item : listSrc) {
            addCourse(item);
        }
    }

    private void addCourse(DBTimetable item) {
        int position = item.getLessonNo();

        HashMap<String, Object> course = new HashMap<>();
        course.put(DBTimetable.TimetableInfo._ID, item.getId());
        course.put(DBTimetable.TimetableInfo.WEEK_ID, item.getWeekId());
        // course.put("course_id", cursor.getString(cursor.getColumnIndex("course_id")));
        course.put(DBTimetable.TimetableInfo.COURSE_NAME, item.getCourseName());
        course.put(DBTimetable.TimetableInfo.PLACE, item.getPlace());
        course.put(DBTimetable.TimetableInfo.TEACHERS, item.getTeachers());
        course.put(DBTimetable.TimetableInfo.WEEKS, item.getWeeks());
        course.put(DBTimetable.TimetableInfo.COURSE_TYPE, item.getCourseType());

        (this.course.get(position)).add(course);
    }

    public ArrayList<Map<String, Object>> getCourseListByLessonNo(int lessonNo) {
        return course.get(lessonNo);
    }

}
