package project.baonq.service;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import project.baonq.dao.LedgerDAO;
import project.baonq.menu.R;
import project.baonq.model.Ledger;
import project.baonq.util.SyncActionImpl;

import static project.baonq.service.BaseAuthService.buildBasicConnection;
import static project.baonq.service.BaseAuthService.read;

public class LedgerSyncService implements Runnable {
    private final Application application;
    public String ledgerUrl;
    public static final String LEDGER_LASTUPDATE = "ledger_lastUpdate";

    public LedgerSyncService(Application application) {
        this.application = application;
        Resources resources = application.getBaseContext().getResources();
        ledgerUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.get_create_update_ledger_url);
    }

    @Override
    public void run() {
        try {
            FetchLedgerAction fetchLedgerAction = new FetchLedgerAction(this);
            fetchLedgerAction.doAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long getLastUpdateTime() {
        SharedPreferences sharedPreferences = application.getSharedPreferences("sync", Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LEDGER_LASTUPDATE, Long.parseLong("0"));
    }

    public void insertOrUpdate(List<Ledger> ledgers) {
        new LedgerDAO(application).insertOrUpdate(ledgers);
    }

    public void syncWithLocal(List<Ledger> ledgers) {
        LedgerDAO ledgerDAO = new LedgerDAO(application);
        //get origin ledger from db
        List<Ledger> origin = ledgerDAO.findByServerId(ledgers.stream()
                .map(ledger -> ledger.getServer_id()).collect(Collectors.toList()));
        //update local id
        Map<Long, Ledger> tmp = new HashMap<>();
        ledgers.forEach(ledger -> tmp.put(ledger.getServer_id(), ledger));
        origin.forEach(org -> {
            Ledger tmpLedger = tmp.get(org.getServer_id());
            tmpLedger.setId(org.getId());
        });
        //insert or update
        ledgerDAO.insertOrUpdate(ledgers);
    }

    public static class FetchLedgerAction extends SyncActionImpl {
        LedgerSyncService ledgerSyncService;

        public FetchLedgerAction(LedgerSyncService ledgerSyncService) {
            this.ledgerSyncService = ledgerSyncService;
        }

        @Override
        public void beforeSynchronize() {
        }

        @Override
        public void afterSynchronize() {
            Ledger[] syncData = (Ledger[]) getSyncData();
            List<Ledger> syncDataList = Arrays.asList(syncData);
            ledgerSyncService.syncWithLocal(syncDataList);
            ledgerSyncService.insertOrUpdate(syncDataList);
            //save last update time to preference
            Long lastUpdate = ledgerSyncService.getLastUpdateTime();
            SharedPreferences sharedPreferences = ledgerSyncService.application
                    .getSharedPreferences("sync", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(LedgerSyncService.LEDGER_LASTUPDATE, lastUpdate);
            editor.commit();
        }

        @Override
        public Object synchronize() {
            System.out.println("SENDING REQUEST TO URL:" + ledgerSyncService.ledgerUrl + ", method:GET");
            Long lastUpdate = ledgerSyncService.getLastUpdateTime();
            Ledger[] result = new Ledger[]{};
            URL url = null;
            try {
                url = new URL(ledgerSyncService.ledgerUrl + "?lastUpdate=" + lastUpdate);
                HttpURLConnection conn = buildBasicConnection(url, true);
                BufferedReader in = null;
                ObjectMapper om = new ObjectMapper();
                try {
                    //read response value
                    if (conn.getResponseCode() == 200) {
                        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String tmp = read(in);
                        result = om.readValue(tmp, new Ledger[]{}.getClass());
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
