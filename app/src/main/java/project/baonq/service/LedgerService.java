package project.baonq.service;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import project.baonq.menu.R;

public class LedgerService extends BaseAuthService {
    private static String ledgerUrl;
    private String jwt;

    public LedgerService(Resources resources) {
        ledgerUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.ledger_url);
    }

    public String addLedger(String name, String currency, float currentBalance) throws Exception {
        jwt = null;
        System.out.println("Ledger url" + ledgerUrl);
        URL url = new URL(ledgerUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            BufferedReader in;
            wr.writeBytes("name=" + name + "&currency=" + currency + "&currentBalance=" + currentBalance);
            if(conn.getResponseCode() == 200){
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                jwt = read(in);
                System.out.println("New jwt: " + jwt);
            }else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                throw new Exception(read(in));
            }
        }
        jwt = jwt.substring(1, jwt.length() - 1);
        System.out.println(jwt);
        return jwt;
    }
}
