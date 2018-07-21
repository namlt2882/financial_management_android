package project.baonq.dao;

import android.app.Application;

import org.greenrobot.greendao.query.QueryBuilder;

import project.baonq.model.DaoSession;
import project.baonq.model.TransactionDao;
import project.baonq.model.TransactionGroup;
import project.baonq.model.TransactionGroupDao;

public class TransactionGroupDAO extends  DAO {

    public TransactionGroupDAO(Application application) {
        super(application);
    }

    public Long addTransactionGroup(TransactionGroup transactionGroup){
        DaoSession daoSession = getDaoSession();
        TransactionGroupDao transactionGroupDao = daoSession.getTransactionGroupDao();
        return transactionGroupDao.insert(transactionGroup);
    }

    public Long getTransactionGroupID(Long ledger_id, int transaction_type, String name){
        DaoSession daoSession = getDaoSession();
        TransactionGroupDao transactionGroupDao = daoSession.getTransactionGroupDao();
        QueryBuilder<TransactionGroup> queryBuilder = transactionGroupDao.queryBuilder();
        return queryBuilder.where(TransactionGroupDao.Properties.Ledger_id.eq(ledger_id),
                TransactionGroupDao.Properties.Transaction_type.eq(transaction_type),
                TransactionGroupDao.Properties.Name.eq(name))
                .unique()
                .getId();
    }

    public TransactionGroup getTransactionGroupByID(Long id) {
        DaoSession daoSession = getDaoSession();
        TransactionGroupDao transactionGroupDao = daoSession.getTransactionGroupDao();
        return transactionGroupDao.load(id);
    }
}
