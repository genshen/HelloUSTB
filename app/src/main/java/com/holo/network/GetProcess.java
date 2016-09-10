package com.holo.network;

/**
 * Created by 根深 on 2015/11/13.
 */

import org.unbescape.html.HtmlEscape;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author 根深
 */
public class GetProcess {
    static String Error = "error";

    public static ArrayList<String> MainProcess(BufferedReader br_html, int id) {
        switch (id) {
            case 0: //print  all html for test
                return printHtml(br_html);
            case 1:
                return getHomeContent(br_html);
            case 2:
                return getScore(br_html);
            case 3:
                return getEleScore(br_html);
            case 4:
                return getTimeTable(br_html);
            case 6:
                return resolveIp6Address(br_html);
            case 8:    //获得校园网信息
                return getCampusNetworkInfo(br_html);
            case 9:
                return getWifiState(br_html);
            case 10:
                return joinVolunteerActivity(br_html);
            case 11:
                return getInnovationCredits(br_html);
            case 13:
                return getCampuscardConsumption(br_html);
            case 14:
                return getMyInfo(br_html);
            case 15:
                return volunteer_home(br_html);
            case 16:
                return get_volunteer_list(br_html);
            case 17:
                return get_volunteer_search_data(br_html);
            case 18:
                return get_volunteer_detail(br_html);
            case 19:
                return get_libaray_search(br_html);
            case 20:
                return get_library_book_detail(br_html);
            case 21:
                return get_book_douban(br_html);
            default:
                return null;
        }
    }


    private static ArrayList<String> getHomeContent(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<String>();
        try {
            while ((line = br_html.readLine()) != null) {
                //<TD vAlign=center height=22><IMG height=6 src=/images/default/index_26.gif width=6>
                //<A href=bencandy.php?fid=80&id=4124 target=_blank>2014-2015-1本科生必生必修课学校统一补考安排</A></TD>
                if (line.contains("width=6>")) {
//						response.replaceAll("><", "@");
                    line = HtmlEscape.unescapeHtml(line);
                    String[] split_str = line.split(">");
//						String[] sec_Split = split_str[2].split(">|<");
                    process_result.add(split_str[2].substring(9, split_str[2].length() - 14));
                    process_result.add(split_str[3].substring(0, split_str[3].length() - 3));
                } else if (line.contains("<F")) {
                    process_result.add(line.split("<|>")[4]);
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> getScore(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                //<TD colspan="1" align="center" style="LINE-HEIGHT: 14pt">91</TD>
                String regex = ".+colspan.+align.+LINE-HEIGHT.+";
                if (line.matches(regex)) {
                    String split_str[] = line.split(">|<");
                    //return split_str[1].replaceAll("</TD","")+"\n";
                    process_result.add(split_str[2]);
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get score from elearning.ustb.edu.cn/
     *
     * @param br_html bufferedReader from web
     * @return StringList process by this functon
     */
    private static ArrayList<String> getEleScore(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                if (line.contains("td>")) {
                    String split_str[] = line.split(">|<");
                    if (split_str.length > 30) {
                        process_result.add(split_str[2]);
                        process_result.add(split_str[6]);
                        process_result.add(split_str[10]);
                        process_result.add(split_str[14]);
                        process_result.add(split_str[18]);
                        process_result.add(split_str[22]);
                        process_result.add(split_str[26]);
                        process_result.add(split_str[30]);
                    }
                } else if (line.contains("red")) {
                    String split_str[] = line.split(">|<");
                    if (split_str.length > 6) {
                        process_result.add(split_str[6].substring(2));
                    }
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 课表 该方法已废弃
     **/
    private static ArrayList<String> getTimeTable(BufferedReader br_html) {
        String line = null;
        ArrayList<String> process_result = new ArrayList<>();
        try {
//				while ((line = br_html.readLine()) != null)
//				{
//					//<td class="haveclass">&nbsp;</td>
//					String regex=".+<td.+haveclass.+";
//					if(line!=null&&line.matches(regex)){
//						String split_str[]=line.split("\"|&");
//						split_str[2]=split_str[2].replaceAll("<br>",">");
//						process_result.add(split_str[2]);
//					}
//				}
            process_result.add(br_html.readLine());
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询创新学分
     */
    private static ArrayList<String> getInnovationCredits(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                if (line.contains("td>")) {
                    line = HtmlEscape.unescapeHtml(line);
                    String split_str[] = line.split("<|>");
                    process_result.add(split_str[6]);
                    process_result.add(split_str[10]);
                    process_result.add(split_str[14]);
                    process_result.add(split_str[18]);
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> getCampuscardConsumption(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                //<td align="center" class="table_text" >2015-03-06 17:06:01</td>
                String regex = ".+align.+table_text.+";
                if (line.matches(regex)) {
                    String split_str[] = line.split(">|<");
                    process_result.add(split_str[2]);
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> getMyInfo(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                //<TD colspan="1" align="center" style="LINE-HEIGHT: 14pt">男</TD>
                if (line.contains("colspan")) {
                    String split_str[] = line.split(">|<");
                    process_result.add(split_str[2]);
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    private static ArrayList<String> getWifiState(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            line = br_html.readLine();    //返回读取的第一行
            process_result.add(line);
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> resolveIp6Address(BufferedReader br_html) {
        String line = "";
        ArrayList<String> process_result = new ArrayList<>();
        try {
            String[] arr = br_html.readLine().split("'");
            if (arr.length == 3) {
                line = arr[1];
            }
            br_html.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        process_result.add(line);
        return process_result;
    }

    private static ArrayList<String> getCampusNetworkInfo(BufferedReader br) {
        String line;
        ArrayList<String> data = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
//                time='7837      ';flow='4432040   ';fsele = 1;fee='49300     ';xsele=0;xip='000.000.000.000.';
//                pwm=1;v6af=54478848;v6df=51228484;uid='41355059';pwd='';v46m=4;v4ip='10.24.27.93';v6ip='2001:da8:208:10bf:1576:3a59:9f47:3b33';
                if (line.startsWith("time=")) {
                    String[] values = line.split("\'");
                    if (values.length >= 6) {
                        data.add(values[1].trim());  //time
                        data.add(values[3].trim());  //flow
                        data.add(values[5].trim());  //fee
                    }
                    line = br.readLine();
                    values = line.split("=|;");
                    if (values.length >= 8) {
                        data.add(values[3].trim()); //v6
                        data.add(values[7].trim()); //user id
                    }
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    private static ArrayList<String> joinVolunteerActivity(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            line = br_html.readLine();
            process_result.add(line);
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> volunteer_home(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                String regex = ".+ul.+jj.+";
                String regex2 = ".+326,.+";
                if (line.contains("hy")) {
                    br_html.readLine();
                    process_result.add(br_html.readLine().trim());
                } else if (line.matches(regex)) {
                    br_html.readLine();
                    br_html.readLine();
                    br_html.readLine();
                    process_result.add(br_html.readLine().trim()); //去空格，得到个人信息
                } else if (line.matches(regex2)) {
                    String split_str[] = line.split("<|>");
                    process_result.add(split_str[2]);
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //todo check the length of array
    private static ArrayList<String> get_volunteer_list(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                if (line.contains("cjhd_mc")) {
                    String[] str_arr = line.split("<|>");
                    process_result.add(str_arr[4]);     // 标题
                    process_result.add(str_arr[8]);    //活动时间
                } else if (line.contains("an></")) {   // including </span></li>
                    String other_data = line.split("<|>")[4];
                    process_result.add(other_data);
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> get_volunteer_search_data(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                if (line.contains("<a id=")) {
                    process_result.add(line.split("<|>")[2]);     // 标题
                } else if (line.contains("an></")) {   // including </span></li>
                    if (line.trim().startsWith("<!--")) {
                        process_result.add(line.split("<|>")[5]);
                    } else {
                        process_result.add(line.split("<|>")[4]);
                    }
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // todo check index
    private static ArrayList<String> get_volunteer_detail(BufferedReader br_html) {
        String line;
        int joinFlag = 0;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                if (line.contains("<pre")) {
                    process_result.add(line.split("<|>")[2]);
                } else if (line.contains("an></")) {   // including </span></li>
                    if (line.trim().startsWith("<!--")) {
                        process_result.add(line.split("<|>")[5]);
                    } else {
                        process_result.add(line.split("<|>")[4]);
                    }
                } else if (line.contains("=\"cancel")) {
                    joinFlag |= 4;
                    String str[] = line.split("\\(|\\)");
                    if (str.length == 3) {
                        String s[] = str[1].split(",");
                        int cancel_id = s.length == 3 ? Integer.parseInt(s[1]) : 0;
                        joinFlag += cancel_id << 3;
                    }
                } else if (line.contains("id=\"joinB")) { // join status
                    line = br_html.readLine() + br_html.readLine(); // read 2 lines
                    if (line.contains("已参加")) {
                        joinFlag |= 2;
                    } else if (line.contains("参加")) {
                        joinFlag |= 1;
                    }
                }
                // for more information like keywords
//                else  if (  line.trim().startsWith("<p><b>")) {
//                    process_result.add(line.split("<|>")[6]);
//                }
            }
            br_html.close();
            process_result.add(joinFlag + "");
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> get_libaray_search(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<>();
        try {
            while ((line = br_html.readLine()) != null) {
                if (line.contains("list_")) {
                    {    //书的类别(//"eg:中文图书")书名和编号
                        line = HtmlEscape.unescapeHtml(br_html.readLine());    //字符实体
                        String[] aplit_arr = line.split("<|>|\"");
                        process_result.add(aplit_arr[4]);    //类别
                        process_result.add(aplit_arr[8]);    //链接
                        process_result.add(aplit_arr[10]);    //书名
                        process_result.add(aplit_arr[12].trim());    //编号
                    }
                    {    //馆藏复本
                        line = br_html.readLine();
                        String[] aplit_arr = line.split("<|>");
                        process_result.add(aplit_arr[4].trim());
                    }
                    {    //可借副本 与作者
                        line = HtmlEscape.unescapeHtml(br_html.readLine());
                        String[] aplit_arr = line.split("<|>");
                        process_result.add(aplit_arr[0].trim());
                        process_result.add(aplit_arr[2].trim());    //作者
                    }
                    {    //出版社   时间
                        line = HtmlEscape.unescapeHtml(br_html.readLine().replaceFirst("&nbsp;", "<"));
                        String[] aplit_arr = line.split("<");
                        process_result.add(aplit_arr[0].trim());
                        process_result.add(aplit_arr[1].trim());
                    }
                }
            }
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> get_library_book_detail(BufferedReader br_html) {
        String line;
        ArrayList<String> process_result = new ArrayList<String>();
        boolean isset = true;
        try {
            while ((line = br_html.readLine()) != null) {
                if (line.contains("booklist") && !(line = br_html.readLine()).contains("none")) {
                    process_result.add(line.split("<|>")[2]);    //项的名

                    line = HtmlEscape.unescapeHtml(br_html.readLine());    //项对应的内容；
                    String[] aplit_arr = line.split("<|>");
                    if (aplit_arr.length == 4) {   //内容无链接
                        process_result.add(aplit_arr[2]);
                    } else if (aplit_arr.length == 8) {
                        process_result.add(aplit_arr[4] + aplit_arr[6]);
                    } else if (aplit_arr.length == 12) {
                        process_result.add(aplit_arr[4] + aplit_arr[8] + aplit_arr[10]);
                    }
                } else if (line.contains("d  w")) {
                    if (isset) {    //第一次的时候添加null
                        process_result.add(null);
                        isset = false;
                    }

                    String[] aplit_arr = line.split("<|>");
                    if (aplit_arr.length == 4) {   //内容无链接
                        process_result.add(aplit_arr[2].replaceAll("&nbsp;", ""));
                    } else if (aplit_arr.length == 6) {
                        process_result.add(aplit_arr[4]);
                    }
                } else if (line.contains("ajax_douban")) {
                    process_result.add(line.split("\"")[1]);
                }
            }
            return process_result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<String> get_book_douban(BufferedReader br_html) {
        ArrayList<String> process_result = new ArrayList<String>();
        try {
            String line = br_html.readLine();
            line = line.split("\"")[15].replace("\\", "");
            process_result.add(line);
            br_html.close();
            return process_result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<String> printHtml(BufferedReader br_html) {
        String line = null;
        System.out.println(">>>>>>>>>>>>>>>>>>>>>Html here>>>>>>>>>>>>>>>>>>>>>>>>>");
        try {
            while ((line = br_html.readLine()) != null) {
                System.out.println(line);
            }
            br_html.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
