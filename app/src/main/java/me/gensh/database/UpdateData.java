package me.gensh.database;

import java.util.List;

import me.gensh.helloustb.Timetable;

/**
 * Created by gensh on 2016/3/26.
 */
public class UpdateData {
    //todo
    public static boolean submitCourseInfoEdit(DB session, long id, String name, String teachers, String place, String weeks,
                                               int week_id, int week_day, int lesson_id) {
        try {
            //select * from course_info  where _id = id{val}
            List<DBTimetable> tbs = session.getTimetableDao().getUniqueTimetable(id);
            if (tbs.size() > 1 || tbs.size() <= 0) { // make it unique
                return false;
            }

            DBTimetable timetable = tbs.get(0);
            timetable.setCourseName(name);
            timetable.setTeachers(teachers);
            timetable.setPlace(place);
            timetable.setWeeks(weeks);
            timetable.setWeekId(week_id);
            timetable.setWeekDay(week_day);
            timetable.setLessonNo(lesson_id);   //todo need update lesson time.
            session.getTimetableDao().updateTimetable(timetable);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
