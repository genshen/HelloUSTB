package me.gensh.utils;

import android.util.Log;

import me.gensh.helloustb.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginDialog {
    final public static int LoginEdu = 1;
    final public static int LoginNet = 2;
    final public static int LoginZFW = 3;
    final public static int LoginE = 4;
    final public static int LoginEle = 5;
    final public static int LoginVol = 6;
    final public static int Timetable = 7;

    public String passFileName = "";
    public Map<String, String> post_params = new LinkedHashMap<>();
    public int dialog_title = 0;
    public int post_address = 0;
    public int verify_id = 0;

    int state = 0;

    public LoginDialog(int id) {
        state = id;
        switch (id) {
            case LoginEdu:
                passFileName = "/MyUstb/Pass_store/sch_edu_pass.ustb";
                dialog_title = R.string.alert_title_deu;
                post_address = R.string.tea_login;
                verify_id = 2;

                post_params.put("usertype", "student");
                post_params.put("btnlogon.x", "75");
                post_params.put("btnlogon.y", "75");
//			"username"   "password"
                break;
            case LoginNet:
                passFileName = "/MyUstb/Pass_store/sch_net_pass.ustb";
                dialog_title = R.string.alert_title_net;
                post_address = R.string.sch_net;
                verify_id = 7;

                post_params.put("v6ip", getLocalIpv6Address());
                post_params.put("0MKKey", "123456789");
                break;
            case LoginZFW:
                post_params.put("Submit", "%B5%C7%C2%BC+Login");
                verify_id = 71;
                break;
            case LoginE:
                passFileName = "/MyUstb/Pass_store/sch_e_pass.ustb";
//			dialog_title = R.string.alert_title_e;
//			post_address = R.string.e_login;
                post_params.put("goto", "http://e.ustb.edu.cn/loginSuccess.portal");
                post_params.put("gotoOnFail", "http://e.ustb.edu.cn/loginFailure.portal");
                verify_id = 12;
                break;
            case LoginVol:
                passFileName = "/MyUstb/Pass_store/sch_vol_pass.ustb";
                dialog_title = R.string.alert_title_volunteer;
                post_address = R.string.vol_login;
                post_params.put("lastUrl", "");
                verify_id = 11;
                break;
            case LoginEle:
                passFileName = "/MyUstb/Pass_store/sch_ele_pass.ustb";
                dialog_title = R.string.alert_title_ele;
                post_address = R.string.ele_login;
                verify_id = 4;
//			j_username=41355059%2Cundergraduate&j_password=233333
                break;
            default:
                ;
        }
    }

    //special construct method for login network
    @Deprecated
    public LoginDialog(int id, String ip6) {
        state = id;
        passFileName = "/MyUstb/Pass_store/sch_net_pass.ustb";
        dialog_title = R.string.alert_title_net;
        post_address = R.string.sch_net;
        verify_id = 7;
        post_params.put("v6ip", ip6);
        post_params.put("0MKKey", "123456789");
    }

    public void setAccount(String username, String password) {
        switch (state) {
            case LoginZFW:
                post_params.put("name", username);
                post_params.put("password", password);
            case LoginVol:
            case LoginEdu:
                post_params.put("username", username);
                post_params.put("password", password);
                break;
            case LoginNet:
                post_params.put("DDDDD", username);
                post_params.put("upass", password);
                break;
            case LoginE:
                post_params.put("Login.Token1", username);
                post_params.put("Login.Token2", password);
                break;
            case LoginEle:
                post_params.put("j_username", username + ",undergraduate");
                post_params.put("j_password", password);
                break;
            case Timetable:
//			listXnxq=&uid=41355059
                post_params.put("uid", username);
                post_params.put("listXnxq", password);
        }
    }


    public static String getLocalIpv6Address() {
        try {
            Process process = Runtime.getRuntime().exec("ip -6 addr show");
            InputStream input = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String line, ip6OutputLine = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("inet6") && line.endsWith("dynamic")) {
                    ip6OutputLine = line;
                    if (line.contains("temporary")) {
                        break;
                    }
                }
            }
            if (ip6OutputLine != null) {
                String[] ips = ip6OutputLine.split(" |/", 3);
                if(ips.length>=2){
                    Log.v("ipv6",ips[1]);
                    return ips[1];
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
