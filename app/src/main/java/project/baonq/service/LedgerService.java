package project.baonq.service;


import android.app.Application;

import java.util.List;

import project.baonq.dao.LedgerDAO;
import project.baonq.enumeration.LedgerStatus;
import project.baonq.model.DaoSession;
import project.baonq.model.Ledger;
import project.baonq.model.LedgerDao;

public class LedgerService {

    private Application application;

    public LedgerService(Application application) {
        this.application = application;
    }


    public Long addLedger(String name, String currency, boolean isReport) {
        Ledger ledger = new Ledger();
        ledger.setName(name);
        ledger.setCurrency(currency);
        ledger.setCounted_on_report(isReport);
        ledger.setStatus(LedgerStatus.ENABLE.getStatus());
        long now = System.currentTimeMillis();
        ledger.setInsert_date(now);
        ledger.setLast_update(now);
        return new LedgerDAO(application).addLedger(ledger);
    }

    public void updateLedger(Long id, String name, String currency, boolean isChecked) {
        Ledger ledger = new Ledger();
        ledger.setId(id);
        ledger.setName(name);
        ledger.setCurrency(currency);
        ledger.setCounted_on_report(isChecked);
        long now = System.currentTimeMillis();
        ledger.setLast_update(now);
        new LedgerDAO(application).updateLedger(ledger);
    }

    public List<Ledger> getAll() {
        return new LedgerDAO(application).getAll();
    }
}
