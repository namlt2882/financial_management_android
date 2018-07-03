package project.baonq.service;

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

    public HttpURLConnection buildBasicConnection(URL url) {
        return buildBasicConnection(url, false);
    }

    public HttpURLConnection buildBasicConnection(URL url, boolean authenticated) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (authenticated) {
            conn.setRequestProperty("Authorization", jwt);
        }
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setDoOutput(true);
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

    protected String read(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private JWTClaimsSet getClaimsFromToken(String token) {
        JWTClaimsSet claims = null;
        try {
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
