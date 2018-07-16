package project.baonq.service;


import java.util.List;

import project.baonq.enumeration.LedgerStatus;
import project.baonq.model.DaoSession;
import project.baonq.model.Ledger;
import project.baonq.model.LedgerDao;

public class LedgerService {

    private DaoSession daoSession;

    public LedgerService(DaoSession daoSession) {
        this.daoSession = daoSession;
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
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        ledgerDao.insert(ledger);
        return ledgerDao.getKey(ledger);
    }

    public void updateLedger(Long id, String name, String currency, boolean isChecked) {
        Ledger ledger = new Ledger();
        ledger.setId(id);
        ledger.setName(name);
        ledger.setCurrency(currency);
        ledger.setCounted_on_report(isChecked);
        long now = System.currentTimeMillis();
        ledger.setLast_update(now);
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        ledgerDao.update(ledger);
    }

    public List<Ledger> getAll() {
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        return ledgerDao.loadAll();
    }
}
