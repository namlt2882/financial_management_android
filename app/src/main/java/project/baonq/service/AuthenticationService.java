package project.baonq.service;

import android.content.Context;
import android.content.res.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import project.baonq.dto.UserDto;
import project.baonq.menu.R;
import project.baonq.model.User;

public class AuthenticationService extends BaseAuthService {
    private static String loginUrl;
    private static String logoutUrl;
    private static String registerUrl;
    private Context context;
    public static String authenticationFileName = "auth.properties";

    public AuthenticationService(Context context) {
        this.context = context;
        Resources resources = context.getResources();
        loginUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.login_url);
        logoutUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.logout_url);
        registerUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.register_url);
        if (getJwt() == null) {
            try {
                loadAuthenticationInfo();
            } catch (FileNotFoundException e) {
                saveAuthenticationInfo("");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public String login(String username, String password) throws Exception {
        String jwt = null;
        //build connection
        URL url = new URL(loginUrl);
        HttpURLConnection conn = buildBasicConnection(url);
        conn.setRequestMethod("POST");
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
        conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        conn.setRequestMethod("POST");
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

    public void logout() throws Exception {
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

    private void loadAuthenticationInfo() throws Exception {
        String jwt = null;
        setJwt(null);
        setUser(null);
        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(
                context.openFileInput(authenticationFileName));) {
            properties.load(in);
            jwt = properties.getProperty("jwt");
            if (jwt != null && !jwt.equals("")) {
                setJwt(jwt);
                setUser(getUserFromToken(jwt));
            }
        }
    }

    private void saveAuthenticationInfo(String jwt) {
        if ("".equals(jwt)) {
            setJwt(null);
        } else {
            setJwt(jwt);
        }
        Properties properties = new Properties();
        try (OutputStreamWriter out = new OutputStreamWriter(
                context.openFileOutput(authenticationFileName, Context.MODE_PRIVATE));
             InputStreamReader in = new InputStreamReader(
                     context.openFileInput(authenticationFileName));) {
            properties.load(in);
            properties.setProperty("jwt", jwt);
            properties.store(out, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
