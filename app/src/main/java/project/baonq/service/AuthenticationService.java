package project.baonq.service;

import android.content.Context;
import android.content.res.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import project.baonq.dto.UserDto;
import project.baonq.menu.R;
import project.baonq.model.User;

public class AuthenticationService extends BaseAuthService {
    private static String loginUrl;
    private static String logoutUrl;
    private static String registerUrl;

    public AuthenticationService(Context context) {
        super(context);
        Resources resources = context.getResources();
        loginUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.login_url);
        logoutUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.logout_url);
        registerUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.register_url);
        if (getJwt() == null) {
            loadAuthenticationInfo();
        }
    }

    public String login(String username, String password) throws Exception {
        String jwt = null;
        //build connection
        URL url = new URL(loginUrl);
        HttpURLConnection conn = buildBasicConnection(url);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        BufferedReader in = null;
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream());) {
            //write parameter to request
            wr.writeBytes("username=" + username + "&password=" + password);
            //read response value
            if (conn.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                jwt = read(in);
                System.out.println("New jwt: " + jwt);
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                throw new Exception(read(in));
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        jwt = jwt.substring(1, jwt.length() - 1);
        System.out.println(jwt);
        //save authentication info to file
        saveAuthenticationInfo(jwt);
        return jwt;
    }


    public User register(UserDto user) throws Exception {
        User result = null;
        //build connection
        URL url = new URL(registerUrl);
        HttpURLConnection conn = buildBasicConnection(url);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        BufferedReader in = null;
        ObjectMapper om = new ObjectMapper();
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream());) {
            //write parameter to request
            wr.writeBytes(om.writeValueAsString(user));
            //read response value
            if (conn.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = om.readValue(read(in), User.class);
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                throw new Exception(read(in));

            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return result;
    }

    public void logout() {
//        URL url = new URL(logoutUrl);
//        HttpURLConnection conn = buildBasicConnection(url, true);
//        conn.setRequestMethod("POST");
//        BufferedReader in = null;
//        System.out.println(logoutUrl);
//        try {
//            //read response value
//            if (conn.getResponseCode() == 200) {
//                System.out.println("Logout successfully!");
//                //invalidate jwt
//            } else {
//                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//                throw new Exception(read(in));
//            }
//        } finally {
//            if (in != null) {
//                in.close();
//            }
//        }
        saveAuthenticationInfo("");
    }

    public boolean isLoggedIn() {
        return getJwt() != null && !"".equals(getJwt());
    }


}
