package me.gensh.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by gensh on 2017/9/19.
 */
@Entity(
        tableName = Config.TIMETABLE_DB_TABLE_NAME
)
public class DBTimetable implements Serializable {
    private static final long serialVersionUID = 0x1000L;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TimetableInfo._ID)
    private Long id; //id

    @ColumnInfo(name = TimetableInfo.STUDENT_NUM)
    int studentNum; //学生数目

    @ColumnInfo(name = TimetableInfo.LEARN_TIME)
    int learnTime;  //学时

    @ColumnInfo(name = TimetableInfo.CREDIT)
    int credit;  //学分

    @ColumnInfo(name = TimetableInfo.WEEK_ID)
    int weekId;  // 哪些周有课,比特为1表示该周有课

    @ColumnInfo(name = TimetableInfo.WEEK_DAY)
    int weekDay;  // 周几有课

    @ColumnInfo(name = TimetableInfo.LESSON_NO)
    int lessonNo;   //第几节课

    @ColumnInfo(name = TimetableInfo.COURSE_ID)
    String courseID;  // 课程ID

    @ColumnInfo(name = TimetableInfo.COURSE_NAME)
    String courseName;  // 课程名称

    @ColumnInfo(name = TimetableInfo.COURSE_TYPE)
    String courseType;  // 课程类型

    @ColumnInfo(name = TimetableInfo.TEACHERS)
    String teachers;  // 授课教师

    @ColumnInfo(name = TimetableInfo.TIME_PLACE)
    String timeAndPlace;  // 时间地点

    @ColumnInfo(name = TimetableInfo.PLACE)
    String place;  //地点

    @ColumnInfo(name = TimetableInfo.TIME)
    String time;    //上课时间
    @ColumnInfo(name = TimetableInfo.WEEKS)
    String weeks;     //哪几周都有课

    public DBTimetable(Long id, int studentNum, int learnTime, int credit,
                       int weekId, int weekDay, int lessonNo, String courseID,
                       String courseName, String courseType, String teachers,
                       String timeAndPlace, String place, String time, String weeks) {
        this.id = id;
        this.studentNum = studentNum;
        this.learnTime = learnTime;
        this.credit = credit;
        this.weekId = weekId;
        this.weekDay = weekDay;
        this.lessonNo = lessonNo;
        this.courseID = courseID;
        this.courseName = courseName;
        this.courseType = courseType;
        this.teachers = teachers;
        this.timeAndPlace = timeAndPlace;
        this.place = place;
        this.time = time;
        this.weeks = weeks;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStudentNum() {
        return this.studentNum;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }

    public int getLearnTime() {
        return this.learnTime;
    }

    public void setLearnTime(int learnTime) {
        this.learnTime = learnTime;
    }

    public int getCredit() {
        return this.credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getWeekId() {
        return this.weekId;
    }

    public void setWeekId(int weekId) {
        this.weekId = weekId;
    }

    public int getWeekDay() {
        return this.weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getLessonNo() {
        return this.lessonNo;
    }

    public void setLessonNo(int lessonNo) {
        this.lessonNo = lessonNo;
    }

    public String getCourseID() {
        return this.courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseType() {
        return this.courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getTeachers() {
        return this.teachers;
    }

    public void setTeachers(String teachers) {
        this.teachers = teachers;
    }

    public String getTimeAndPlace() {
        return this.timeAndPlace;
    }

    public void setTimeAndPlace(String timeAndPlace) {
        this.timeAndPlace = timeAndPlace;
    }

    public String getPlace() {
        return this.place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeeks() {
        return this.weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    final public static class TimetableInfo {
        final public static String TABLE_NAME = Config.TIMETABLE_DB_TABLE_NAME;
        final public static String _ID = "_id";
        final public static String STUDENT_NUM = "student_num";
        final public static String LEARN_TIME = "learn_time";
        final public static String CREDIT = "credit";
        final public static String WEEK_ID = "week_id";
        final public static String WEEK_DAY = "week_day";
        final public static String LESSON_NO = "lesson_no";
        final public static String COURSE_ID = "course_id";
        final public static String COURSE_NAME = "course_name";
        final public static String COURSE_TYPE = "course_type";
        final public static String TEACHERS = "teachers";
        final public static String TIME_PLACE = "time_place";
        final public static String PLACE = "place";
        final public static String TIME = "time";
        final public static String WEEKS = "weeks";
    }
}
