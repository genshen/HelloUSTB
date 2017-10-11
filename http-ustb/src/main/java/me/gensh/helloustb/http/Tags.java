package me.gensh.helloustb.http;

/**
 * Created by gensh on 2017/10/9.
 */

public class Tags {

    public final static String LIB = "lib";
    public final static String NETWORK = "network";
    public final static String VOLUNTEER = "volunteer";
    public final static String TEACH = "teach";
    public final static String E_LEARNING = "ele";
    public final static String E = "e";

    public final static class GET {
        public final static int
                ID_HTML_OUT = 0,  //print out html string.
                ID_TEACH_NET_HOME = 1, // home page of http://teach.ustb.edu.cn.
                ID_SEAM_SCORE = 2, // get student's score from  http://seam.ustb.edu.cn:8080/jwgl/
                ID_E_LEARNING_SCORE_QUERY = 3, // get student'a score from  http://elearning.ustb.edu.cn/
                ID_E_LEARNING_TIMETABLE = 4, // get student's timetable from  http://elearning.ustb.edu.cn/
                ID_IPV6_ADDRESS = 6,
                ID_NETWORK_INFO = 8,  //get user information of campus network.
                ID_VOLUNTEER_JOIN_EXIT_COLLECT_ACTIVITY = 10, //  join or exit or collect an volunteer activity
                ID_E_LEARNING_INNOVATION_CREDIT = 11, //  get student'a innovation credit
                ID_E_CAMPUS_CARD = 13, //request campus card data from http://e.ustb.edu.cn
                ID_VOLUNTEER_USER_INFORMATION = 15,  //get volunteer user self information
                ID_VOLUNTEER_ACTIVITIES_LIST = 16,  //get volunteer user self activities list.
                ID_VOLUNTEER_ACTIVITIES_SEARCH = 17, //volunteer activities search
                ID_VOLUNTEER_ACTIVITY_DETAIL = 18, //get volunteer activity detail information
                ID_LIB_SEARCH = 19, //library book search
                ID_LIB_BOOK_DETAIL = 20, //library book detail
                ID_LIB_BOOK_DOUBAN = 21; //get dou-ban book meta
    }

    public final static class POST {
        public final static int
                ID_SEAM_LOGIN = 2,  //login to http://seam.ustb.edu.cn:8080/jwgl/
                ID_E_LEARNING_LOGIN = 4, //login to http://elearning.ustb.edu.cn/
                ID_E_LEARNING_GET_TIMETABLE = 5,  //get timetable from http://elearning.ustb.edu.cn/
                ID_E_LEARNING_EXAM_LIST = 6, // get exam time & place information fro http://elearning.ustb.edu.cn/
                ID_NETWORK_LOGIN = 7,  // get user information of campus network after signing in.
                ID_VOLUNTEER_LOGIN = 11,  //login to volunteer website
                ID_E_LOGIN = 12;  // login to http://e.ustb.edu.cn/
    }


}
