package project.baonq.service;

import org.greenrobot.greendao.query.QueryBuilder;

import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionGroup;
import project.baonq.model.TransactionGroupDao;

public class TransactionGroupService {

    private static DaoSession daoSession;
    private static TransactionGroup transactionGroup;
    private static TransactionGroupDao transactionGroupDao;

    public static void setDaoSession(DaoSession daoSession) {
        TransactionGroupService.daoSession = daoSession;
    }

    public static Long addTransactionGroup(Long ledgerId, String name, int transactionType, int status) {
        transactionGroup = new TransactionGroup();
        transactionGroup.setLedger_id(ledgerId);
        transactionGroup.setName(name);
        transactionGroup.setTransaction_type(transactionType);
        transactionGroup.setStatus(status);
        transactionGroupDao = daoSession.getTransactionGroupDao();
        transactionGroupDao.insert(transactionGroup);
        return transactionGroupDao.getKey(transactionGroup);
    }

    public static Long getTransactionGroupID(Long ledger_id, int transaction_type, String name) {
        transactionGroupDao = daoSession.getTransactionGroupDao();
        QueryBuilder<TransactionGroup> queryBuilder = transactionGroupDao.queryBuilder();
        return queryBuilder.where(TransactionGroupDao.Properties.Ledger_id.eq(ledger_id),
                TransactionGroupDao.Properties.Transaction_type.eq(transaction_type),
                TransactionGroupDao.Properties.Name.eq(name))
                .unique()
                .getId();
    }
}
