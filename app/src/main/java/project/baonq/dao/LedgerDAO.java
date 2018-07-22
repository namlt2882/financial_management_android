package project.baonq.dao;

import android.app.Application;

import java.util.List;

import project.baonq.model.DaoSession;
import project.baonq.model.Ledger;
import project.baonq.model.LedgerDao;
import project.baonq.service.App;

public class LedgerDAO extends DAO {


    public LedgerDAO(Application application) {
        super(application);
    }

    public Long addLedger(Ledger ledger) {
        DaoSession daoSession = getDaoSession();
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        ledgerDao.insert(ledger);
        return ledgerDao.getKey(ledger);
    }

    public void updateLedger(Ledger ledger) {
        DaoSession daoSession = getDaoSession();
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        ledgerDao.update(ledger);
    }

    public List<Ledger> getAll() {
        DaoSession daoSession = getDaoSession();
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        return ledgerDao.loadAll();
    }

    public Ledger getledgerById(Long id) {
        DaoSession daoSession = getDaoSession();
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        return ledgerDao.load(id);
    }
}
