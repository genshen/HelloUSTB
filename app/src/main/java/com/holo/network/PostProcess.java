/**
 *
 */
package com.holo.network;

import org.unbescape.html.HtmlEscape;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author cgs
 */
public class PostProcess {

    //验证用户名和密码是否错误，错误返回-1，否则返回一个正整数；
    public static DataInfo MainProcess(BufferedReader br, int id) {
        // TODO 自动生成的方法存根
        DataInfo dataInfo = new DataInfo();

        switch (id) {
            case 2:
                dataInfo.code = validateEduPass(br);
                break;
            case 4:
                dataInfo.code = validateElePass(br);    //本科教学网验证
                break;
            case 5:
                dataInfo.data = getTimeTable(br);    //获得课程表
                break;
            case 6:
                dataInfo.data = getExamList(br);    //考试时间地点
                break;
            case 7:        //校园网验证
                dataInfo.code = validateNetPass(br);
                break;
            case 10:
                dataInfo.data = getVolunteerSearchData(br);
                break;
            case 11:
                dataInfo.code = validateVolPass(br);
                break;
            case 12:    //e.ustb.edu.cn
                dataInfo.code = validateE(br);
                break;
            case 71:    //自服务验证
                dataInfo.code = validateZifuwuPass(br);
                break;
        }
        return dataInfo;
    }

    private static ArrayList<String> getTimeTable(BufferedReader br) {
        // TODO Auto-generated method stub
        ArrayList<String> data_list = new ArrayList<>();
        try {
            data_list.add(br.readLine());
            br.close();
            return data_list;
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
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
                return DataInfo.OK;
            }
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return DataInfo.ERROR_PASSWORD;
    }

    private static int validateEduPass(BufferedReader br) {
        String line;
        try {
            line = br.readLine();    //读取第一行
            br.close();
            if (!line.contains("VBScript")) {
                return DataInfo.OK;
            }
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return DataInfo.ERROR_PASSWORD;
    }

    private static int validateNetPass(BufferedReader br) {
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains("successfully")) {
                    br.close();
                    return DataInfo.OK;
                }
            }
            br.close();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return DataInfo.ERROR_PASSWORD;
    }

    private static int validateZifuwuPass(BufferedReader br) {
        String line;
        try {
            line = br.readLine();
            br.close();
            if (line.contains("html")) {  //包含“html”，登录成功
                return DataInfo.OK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DataInfo.ERROR_PASSWORD;
    }

    private static int validateVolPass(BufferedReader br) {
        String line;
        try {
            for (int i = 0; i < 6; i++){    //前面有5个空字符
                line = br.readLine();
                if (line.contains("script")) {
                    br.close();
                    return DataInfo.ERROR_PASSWORD;
                }
            }
            br.close();
            return DataInfo.OK;
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return DataInfo.ERROR_PASSWORD;
    }

    private static ArrayList<String> getVolunteerSearchData(BufferedReader br) {
        // TODO Auto-generated method stub
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
//					all += line;
                if (line.contains("b>"))    //活动名称与链接
                {
                    String split_str[] = line.split(">|<");
                    process_result.add(split_str[4]);
//						process_result.add(split_str[6]);
                } else if (line.contains("br")) {
                    String split_str[] = line.split(">|<");
                    switch (split_str.length)//招募方式等
                    {
                        case 2:    //活动编号	活动类型：等
                            process_result.add(split_str[0]);
                            break;
                        case 3:    //成员招募方式
                            process_result.add(split_str[2]);
                            break;
                        case 4:
                            process_result.add(split_str[0] + split_str[2].replaceAll("&nbsp;", ""));
                            break;
                    }
                } else if (line.contains("fon"))    //参与人数 与感兴趣人数
                {
                    String split_str[] = line.split(">|<");
                    process_result.add(split_str[2]);
                    process_result.add(split_str[6]);
                }
            }
//				SdCardPro.write(all, "/MyUstb/vol_error.html");
            br.close();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return process_result;
    }

    private static int validateE(BufferedReader br) {
        // TODO 自动生成的方法存根
        String line;
        try {
            line = br.readLine();
            br.close();
            if (line.contains("Successed")) {
                return DataInfo.OK;
            }
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return DataInfo.ERROR_PASSWORD;
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        SdCardPro.write(all, "/MyUstb/e_error.log");
    }
}
