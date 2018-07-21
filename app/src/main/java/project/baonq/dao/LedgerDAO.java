package project.baonq.dao;

import android.app.Application;

import java.util.LinkedList;
import java.util.List;

import project.baonq.model.DaoSession;
import project.baonq.model.Ledger;
import project.baonq.model.LedgerDao;
import project.baonq.service.App;

public class LedgerDAO {
    private final Application application;

    public LedgerDAO(Application application) {
        this.application = application;
    }

    private DaoSession getDaoSession() {
        DaoSession daoSession = ((App) application).getDaoSession();
        daoSession.clear();
        return daoSession;
    }

    public List<Ledger> insertOrUpdate(List<Ledger> ledgers) {
        if (ledgers != null && !ledgers.isEmpty()) {
            DaoSession daoSession = getDaoSession();
            LedgerDao ledgerDao;
            ledgerDao = daoSession.getLedgerDao();
            for (Ledger ledger : ledgers) {
                long id = ledgerDao.insertOrReplace(ledger);
                ledger.setId(id);
            }
            return ledgers;
        } else {
            return new LinkedList<>();
        }
    }

    public List<Ledger> findByServerId(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return getDaoSession().getLedgerDao().queryBuilder()
                    .where(LedgerDao.Properties.Server_id.in(ids)).list();
        } else {
            return new LinkedList<>();
        }
    }

    public Ledger findByServerId(Long id) {
        return getDaoSession().getLedgerDao().queryBuilder()
                .where(LedgerDao.Properties.Server_id.eq(id)).unique();
    }

    public List<Ledger> findByIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return getDaoSession().getLedgerDao().queryBuilder()
                    .where(LedgerDao.Properties.Id.in(ids)).list();
        } else {
            return new LinkedList<>();
        }
    }

    public Ledger findLastUpdateLedger() {
        return getDaoSession().getLedgerDao().queryBuilder()
                .orderDesc(LedgerDao.Properties.Last_update).limit(1).unique();
    }

    public List<Ledger> findCreatableLedgers() {
        return getDaoSession().getLedgerDao().queryBuilder()
                .where(LedgerDao.Properties.Server_id.isNull()).list();
    }

    public List<Ledger> findUpdatableLedgers(Long lastUpdate) {
        return getDaoSession().getLedgerDao().queryBuilder()
                .where(LedgerDao.Properties.Server_id.isNotNull(),
                        LedgerDao.Properties.Last_update.gt(lastUpdate)).list();
    }
}
