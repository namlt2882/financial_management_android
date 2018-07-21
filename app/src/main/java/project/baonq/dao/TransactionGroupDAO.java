package project.baonq.dao;

import android.app.Application;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.LinkedList;
import java.util.List;

import project.baonq.model.DaoSession;
import project.baonq.model.Ledger;
import project.baonq.model.TransactionGroup;
import project.baonq.model.TransactionGroupDao;
import project.baonq.service.App;

public class TransactionGroupDAO {
    private final Application application;

    public TransactionGroupDAO(Application application) {
        this.application = application;
    }

    private DaoSession getDaoSession() {
        DaoSession daoSession = ((App) application).getDaoSession();
        daoSession.clear();
        return daoSession;
    }

    public List<TransactionGroup> insertOrUpdate(List<TransactionGroup> groups) {
        if (groups != null && !groups.isEmpty()) {
            TransactionGroupDao ledgerDao = getDaoSession().getTransactionGroupDao();
            for (TransactionGroup group : groups) {
                long id = ledgerDao.insertOrReplace(group);
                group.setId(id);
            }
            return groups;
        } else {
            return new LinkedList<>();
        }
    }

    public List<TransactionGroup> findByServerId(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return getDaoSession().getTransactionGroupDao().queryBuilder()
                    .where(TransactionGroupDao.Properties.Server_id.in(ids)).list();
        } else {
            return new LinkedList<>();
        }
    }

    public List<TransactionGroup> findByIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return getDaoSession().getTransactionGroupDao().queryBuilder()
                    .where(TransactionGroupDao.Properties.Id.in(ids)).list();
        } else {
            return new LinkedList<>();
        }
    }

    public TransactionGroup findLastUpdateGroup() {
        return getDaoSession().getTransactionGroupDao().queryBuilder()
                .orderDesc(TransactionGroupDao.Properties.Last_update).limit(1).unique();
    }

    public List<TransactionGroup> findCreatableGroups() {
        return getDaoSession().getTransactionGroupDao().queryBuilder()
                .where(TransactionGroupDao.Properties.Server_id.isNull()).list();
    }

    public List<TransactionGroup> findUpdatableGroups(Long lastUpdate) {
        return getDaoSession().getTransactionGroupDao().queryBuilder()
                .where(TransactionGroupDao.Properties.Server_id.isNotNull(),
                        TransactionGroupDao.Properties.Last_update.gt(lastUpdate)).list();
    }
}
