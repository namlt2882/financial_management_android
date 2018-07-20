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

    public Ledger findById(Long id){
        return daoSession.getLedgerDao().queryBuilder()
                .where(LedgerDao.Properties.Id.eq(id)).unique();
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

    public void updateLedger(Ledger ledger) {
        Ledger origin = findById(ledger.getId());
        origin.setStatus(ledger.getStatus());
        origin.setCurrency(ledger.getCurrency());
        origin.setName(ledger.getName());
        origin.setCounted_on_report(ledger.getCounted_on_report());
        origin.setLast_update(System.currentTimeMillis());
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        ledgerDao.update(origin);
    }

    public List<Ledger> getAll() {
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        return ledgerDao.loadAll();
    }
}
