package me.gensh.helloustb.http;

import org.unbescape.html.HtmlEscape;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author gensh
 */
public class PostProcess {

    //验证用户名和密码是否错误，错误返回-1，否则返回一个正整数；
    public static ResolvedData MainProcess(BufferedReader br, int id) {
        ResolvedData resolvedData = new ResolvedData();
        switch (id) {
            case Tags.POST.ID_SEAM_LOGIN:
                resolvedData.code = validateEduPass(br);
                resolvedData.data = new ArrayList<>();    //// TODO: 2017/9/22   better implement
                break;
            case Tags.POST.ID_E_LEARNING_LOGIN:
                resolvedData.code = validateElePass(br);    //本科教学网验证
                resolvedData.data = new ArrayList<>();   //// TODO: 2017/9/22
                break;
            case Tags.POST.ID_E_LEARNING_GET_TIMETABLE:
                resolvedData.data = getTimeTable(br);    //获得课程表
                break;
            case Tags.POST.ID_E_LEARNING_EXAM_LIST:
                resolvedData.data = getExamList(br);    //考试时间地点
                break;
            case Tags.POST.ID_NETWORK_LOGIN:   //校园网验证
                resolvedData.code = validateNetPass(br);
                resolvedData.data = new ArrayList<>();   //// TODO: 2017/9/22
                break;
            case Tags.POST.ID_VOLUNTEER_LOGIN:
                resolvedData.code = validateVolPass(br);
                resolvedData.data = new ArrayList<>();  //// TODO: 2017/9/22
                break;
            case Tags.POST.ID_E_LOGIN:    //e.ustb.edu.cn
                resolvedData.code = validateE(br);
                resolvedData.data = new ArrayList<>();  //// TODO: 2017/9/22
                break;
        }
        return resolvedData;
    }

    private static ArrayList<String> getTimeTable(BufferedReader br) {
        ArrayList<String> data_list = new ArrayList<>();
        try {
            data_list.add(br.readLine());
            br.close();
            return data_list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 考试成绩与地点
     *
     * @param br BufferedReader
     * @return ArrayList<String>
     */
    private static ArrayList<String> getExamList(BufferedReader br) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains("<td>")) {
//           eg: <td>2050415 </td><td>操作系统(实验)</td><td>&nbsp;&nbsp;</td><td></td><td>该课程考试时间地点由任课老师课上公布，本系统不发布。</td>
                    line = HtmlEscape.unescapeHtml(line);
                    String split_str[] = line.split("<|>");
                    process_result.add(split_str[2]);
                    process_result.add(split_str[6]);
                    process_result.add(split_str[10]);
                    process_result.add(split_str[14]);
                    process_result.add(split_str[18]);
                }
            }
            br.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int validateElePass(BufferedReader br) {
        String line;
        try {
            line = br.readLine();    //读取第一行
            br.close();
//			System.out.println("success message:"+line);
            if (line.contains("success")) {
                return ResolvedData.OK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResolvedData.ERROR_PASSWORD;
    }

    private static int validateEduPass(BufferedReader br) {
        String line;
        try {
            line = br.readLine();    //读取第一行
            br.close();
            if (!line.contains("VBScript")) {
                return ResolvedData.OK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResolvedData.ERROR_PASSWORD;
    }

    private static int validateNetPass(BufferedReader br) {
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains("successfully")) {
                    br.close();
                    return ResolvedData.OK;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResolvedData.ERROR_PASSWORD;
    }

    //自服务登录验证  目前的校园网登录进不去自服务了
    @Deprecated
    private static void validateZifuwuPass(BufferedReader br, ResolvedData resolvedData) {
        String line;
        try {
            line = br.readLine();
            if (line.contains("html")) {  //包含“html”，登录成功
                resolvedData.data = getFlowInfo(br);
                br.close();
                return;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resolvedData.code = ResolvedData.ERROR_PASSWORD;
    }

    @Deprecated
    private static ArrayList<String> getFlowInfo(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            String regex = ".+<font.+</font>.+";
            while ((line = br_html.readLine()) != null) {
                //<td width="35%" align="left" valign="center"><font color="blue"> 计费组:&nbsp </font>互联网学生</td>
                //String regex="\\w+valign=\"center\">\\s+<font\\s+color=\"blue\">\\s+\\w+&nbsp\\s+</font>\\w+";
                if (line.matches(regex)) {
//				    response=response.replaceFirst("<B>",'1');
                    line = line.replaceAll("<B>", "");
                    String split_str[] = line.split("<|>|&");
                    process_result.add(split_str[4] + split_str[7] + '\n');
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int validateVolPass(BufferedReader br) {
        try {
            String line = br.readLine();
            if (line.contains("errDiv")) {
                br.close();
                return ResolvedData.ERROR_PASSWORD;
            }
            br.close();
            return ResolvedData.OK;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResolvedData.ERROR_PASSWORD;
    }

    private static int validateE(BufferedReader br) {
        String line;
        try {
            line = br.readLine();
            br.close();
            if (line.contains("Successed")) {
                return ResolvedData.OK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResolvedData.ERROR_PASSWORD;
    }

    // 本函数调试用
    private static void saveBr(BufferedReader br) {
        String line;
//        String all = "";
        try {
            while ((line = br.readLine()) != null)    //测试代码，发布时应删除或注释
            {
//                all += (line + "\n");
                System.out.println(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        SdCardPro.write(all, "/MyUstb/e_error.log");
    }
}
