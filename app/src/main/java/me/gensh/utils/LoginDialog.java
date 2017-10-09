package me.gensh.utils;

import android.support.annotation.StringRes;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import me.gensh.helloustb.R;
import me.gensh.helloustb.http.Tags;

public class LoginDialog {
    public int userType;
    public int loginSiteType;
    public int dialog_title = 0;
    public int post_address = 0;
    public int verify_id = 0;
    public Map<String, String> post_params = new LinkedHashMap<>();

    public static LoginDialog newInstanceForSEAM() {
        LoginDialog dialog = new LoginDialog(R.string.tea_login, R.string.alert_title_deu, LoginSiteType.EDU, UserType.EDU, Tags.POST.ID_SEAM_LOGIN);
        dialog.post_params.put("usertype", "student");
        dialog.post_params.put("btnlogon.x", "75");
        dialog.post_params.put("btnlogon.y", "75");
        //	"username"   "password"
        return dialog;
    }

    public static LoginDialog newInstanceForELearning() {
        LoginDialog dialog = new LoginDialog(R.string.ele_login, R.string.alert_title_ele, LoginSiteType.ELE, UserType.ELE, Tags.POST.ID_E_LEARNING_LOGIN);
//			j_username=41355059%2Cundergraduate&j_password=233333
        return dialog;
    }

    public static LoginDialog newInstanceForE() {
        LoginDialog dialog = new LoginDialog(R.string.e_login, R.string.alert_title_e, LoginSiteType.E, UserType.E, Tags.POST.ID_E_LOGIN);
        dialog.post_params.put("goto", "http://e.ustb.edu.cn/loginSuccess.portal");
        dialog.post_params.put("gotoOnFail", "http://e.ustb.edu.cn/loginFailure.portal");
        return dialog;
    }

    public static LoginDialog newInstanceForVolunteer() {
        LoginDialog dialog = new LoginDialog(R.string.vol_login, R.string.alert_title_volunteer, LoginSiteType.VOL, UserType.VOL, Tags.POST.ID_VOLUNTEER_LOGIN);
        dialog.post_params.put("lastUrl", "");
        return dialog;
    }

    public static LoginDialog newInstanceForNetwork() {
        LoginDialog dialog = new LoginDialog(R.string.sch_net, R.string.alert_title_net, LoginSiteType.NET, UserType.NET, Tags.POST.ID_NETWORK_LOGIN);
        dialog.post_params.put("v6ip", getLocalIpv6Address());
        dialog.post_params.put("0MKKey", "123456789");
        return dialog;
    }

    @Deprecated
    public static LoginDialog newInstanceForZFW() { //todo
        LoginDialog dialog = new LoginDialog(0, 0, 0, 0, 0);
        dialog.post_params.put("Submit", "%B5%C7%C2%BC+Login");
        dialog.verify_id = 71;
        return dialog;
    }

    private LoginDialog(@StringRes int postAddress, @StringRes int dialogTitle, int loginSiteType, int userType, int verifyId) {
        this.post_address = postAddress;
        this.loginSiteType = loginSiteType;
        this.dialog_title = dialogTitle;
        this.userType = userType;
        this.verify_id = verifyId;
    }

    //special construct method for login network
    @Deprecated
    public LoginDialog(String ip6) {
        userType = UserType.NET;
        dialog_title = R.string.alert_title_net;
        post_address = R.string.sch_net;
        verify_id = Tags.POST.ID_NETWORK_LOGIN;
        post_params.put("v6ip", ip6);
        post_params.put("0MKKey", "123456789");
    }

    public void setAccount(String username, String password) {
        switch (loginSiteType) {
            case LoginSiteType.ZFW:
                post_params.put("name", username);
                post_params.put("password", password);
            case LoginSiteType.VOL:
            case LoginSiteType.EDU:
                post_params.put("username", username);
                post_params.put("password", password);
                break;
            case LoginSiteType.NET:
                post_params.put("DDDDD", username);
                post_params.put("upass", password);
                break;
            case LoginSiteType.E:
                post_params.put("Login.Token1", username);
                post_params.put("Login.Token2", password);
                break;
            case LoginSiteType.ELE:
                post_params.put("j_username", username + ",undergraduate");
                post_params.put("j_password", password);
                break;
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
                if (ips.length >= 2) {
                    Log.v("ipv6", ips[1]);
                    return ips[1];
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public final static class LoginSiteType {
        final public static int EDU = 0;
        final public static int NET = 1; // different website compared to {ZFW}
        final public static int ZFW = 2;
        final public static int E = 3;
        final public static int ELE = 4;
        final public static int VOL = 5;
        final public static int LIB = 6;
    }

    public final static class UserType {
        final public static int EDU = 0;
        final public static int NET = 1;  //the same user as {ZFW}
        final public static int E = 3;
        final public static int ELE = 4;
        final public static int VOL = 5;
        final public static int LIB = 6;
    }

}
