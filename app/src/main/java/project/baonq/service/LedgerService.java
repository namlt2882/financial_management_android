package project.baonq.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

import project.baonq.model.DaoSession;
import project.baonq.model.Ledger;
import project.baonq.model.LedgerDao;

public class LedgerService {

    private static DaoSession daoSession;
    private static Ledger ledger;
    private static LedgerDao ledgerDao;

    public LedgerService(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public static void setDaoSession(DaoSession daoSession) {
        LedgerService.daoSession = daoSession;
    }

    public static Long addLedger(String name, String currency, boolean isReport) {
        ledger = new Ledger();
        ledger.setName(name);
        ledger.setCurrency(currency);
        ledger.setCounted_on_report(isReport);
        long now = System.currentTimeMillis();
        ledger.setInsert_date(now);
        ledgerDao = daoSession.getLedgerDao();
        ledgerDao.insert(ledger);
        return ledgerDao.getKey(ledger);
    }

    public static void updateLedger(Long id, String name, String currency, boolean isChecked) {
        ledger = new Ledger();
        ledger.setId(id);
        ledger.setName(name);
        ledger.setCurrency(currency);
        ledger.setCounted_on_report(isChecked);
        long now = System.currentTimeMillis();
        ledger.setLast_update(now);
        ledgerDao = daoSession.getLedgerDao();
        ledgerDao.update(ledger);
    }

    public static List<Ledger> getAll() {
        ledgerDao = daoSession.getLedgerDao();
        return ledgerDao.loadAll();
    }
}
