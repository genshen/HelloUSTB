package me.gensh.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by gensh on 2017/9/19.
 */
@Entity(
        nameInDb = "time_table_dbs"
)
public class DBTimetable implements Serializable {
    private static final long serialVersionUID = 0x1000L;

    @Id(autoincrement = true)
    @Property(nameInDb = TimetableInfo._ID)
    private Long id; //id

    @Property(nameInDb = TimetableInfo.STUDENT_NUM)
    int studentNum; //学生数目

    @Property(nameInDb = TimetableInfo.LEARN_TIME)
    int learnTime;  //学时

    @Property(nameInDb = TimetableInfo.CREDIT)
    int credit;  //学分

    @Property(nameInDb = TimetableInfo.WEEK_ID)
    int weekId;  // 哪些周有课,比特为1表示该周有课

    @Property(nameInDb = TimetableInfo.WEEK_DAY)
    int weekDay;  // 周几有课

    @Property(nameInDb = TimetableInfo.LESSON_NO)
    int lessonNo;   //第几节课

    @Property(nameInDb = TimetableInfo.COURSE_ID)
    String courseID;  // 课程ID

    @Property(nameInDb = TimetableInfo.COURSE_NAME)
    String courseName;  // 课程名称

    @Property(nameInDb = TimetableInfo.COURSE_TYPE)
    String courseType;  // 课程类型

    @Property(nameInDb = TimetableInfo.TEACHERS)
    String teachers;  // 授课教师

    @Property(nameInDb = TimetableInfo.TIME_PLACE)
    String timeAndPlace;  // 时间地点

    @Property(nameInDb = TimetableInfo.PLACE)
    String place;  //地点

    @Property(nameInDb = TimetableInfo.TIME)
    String time;    //上课时间
    @Property(nameInDb = TimetableInfo.WEEKS)
    String weeks;     //哪几周都有课

    @Generated(hash = 103522139)
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

    @Generated(hash = 1232475460)
    public DBTimetable() {
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
        final public static String TABLE_NAME = "time_table_dbs";
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
