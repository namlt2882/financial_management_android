package project.baonq.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import project.baonq.model.User;

public class BaseAuthService {

    private static String jwt = null;
    private static User user = null;
    private Context context;

    public BaseAuthService(Context context) {
        this.context = context;
        if (getJwt() == null) {
            loadAuthenticationInfo();
        }
    }

    public static HttpURLConnection buildBasicConnection(URL url) {
        return buildBasicConnection(url, false);
    }


    public static HttpURLConnection buildBasicConnection(URL url, boolean authenticated) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (authenticated) {
            conn.setRequestProperty("Authorization", jwt);
        }
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        return conn;
    }

    public static String getJwt() {
        return jwt;
    }

    public static void setJwt(String jwt) {
        BaseAuthService.jwt = jwt;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        BaseAuthService.user = user;
    }

    public static String read(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    protected void loadAuthenticationInfo() {
        String jwt = null;
        setJwt(null);
        setUser(null);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        jwt = sharedPreferences.getString("jwt", null);
        if (jwt != null && !"".equals(jwt)) {
            setJwt(jwt);
            setUser(getUserFromToken(jwt));
        }
    }

    protected void saveAuthenticationInfo(String jwt) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if ("".equals(jwt) || jwt == null) {
            setJwt(null);
            editor.remove("jwt");
        } else {
            setJwt(jwt);
            editor.putString("jwt", jwt);
        }
        editor.commit();
    }

    private JWTClaimsSet getClaimsFromToken(String token) {
        JWTClaimsSet claims = null;
        try {
            token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6Ilt7XCJpZFwiOjIsXCJuYW1lXCI6XCJVU0VSXCIsXCJzdGF0dXNcIjoxfV0iLCJleHAiOjE1MzA3MDg4OTYsInVzZXJuYW1lIjoiTkFNREVQVFJBSSIsInN0YXR1cyI6MX0.PM0VLo7OfvYLpJ9ErVCo2Gsebkx6-LtCIb0dUhxhGxQ";
            SignedJWT signedJWT = SignedJWT.parse(token);
            claims = signedJWT.getJWTClaimsSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }

    private String getUsernameFromToken(String token) {
        String username = null;
        try {
            token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6Ilt7XCJpZFwiOjIsXCJuYW1lXCI6XCJVU0VSXCIsXCJzdGF0dXNcIjoxfV0iLCJleHAiOjE1MzA3MDg4OTYsInVzZXJuYW1lIjoiTkFNREVQVFJBSSIsInN0YXR1cyI6MX0.PM0VLo7OfvYLpJ9ErVCo2Gsebkx6-LtCIb0dUhxhGxQ";
            JWTClaimsSet claims = getClaimsFromToken(token);
            username = claims.getStringClaim("username");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }

    protected User getUserFromToken(String token) {
        User user = new User();
        user.setUsername(getUsernameFromToken(token));
        return user;
    }
}