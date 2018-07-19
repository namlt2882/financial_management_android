package project.baonq.service;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import project.baonq.enumeration.TransactionGroupStatus;
import project.baonq.enumeration.TransactionStatus;
import project.baonq.model.DaoSession;
import project.baonq.model.Ledger;
import project.baonq.model.LedgerDao;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionGroup;
import project.baonq.model.TransactionGroupDao;

public class TransactionGroupService {

    private DaoSession daoSession;

    public TransactionGroupService(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public Long addTransactionGroup(Long ledgerId, String name, int transactionType, int status) {
        TransactionGroup transactionGroup = new TransactionGroup();
        transactionGroup.setLedger_id(ledgerId);
        transactionGroup.setName(name);
        transactionGroup.setTransaction_type(transactionType);
        transactionGroup.setStatus(status);
        transactionGroup.setStatus(TransactionGroupStatus.ENABLE.getStatus());
        TransactionGroupDao transactionGroupDao = daoSession.getTransactionGroupDao();
        transactionGroupDao.insert(transactionGroup);
        return transactionGroupDao.getKey(transactionGroup);
    }

    public Long getTransactionGroupID(Long ledger_id, int transaction_type, String name) {
        TransactionGroupDao transactionGroupDao = daoSession.getTransactionGroupDao();
        QueryBuilder<TransactionGroup> queryBuilder = transactionGroupDao.queryBuilder();
        return queryBuilder.where(TransactionGroupDao.Properties.Ledger_id.eq(ledger_id),
                TransactionGroupDao.Properties.Transaction_type.eq(transaction_type),
                TransactionGroupDao.Properties.Name.eq(name))
                .unique()
                .getId();
    }

    public List<TransactionGroup> getAll() {
        TransactionGroupDao transactionGroupDao = daoSession.getTransactionGroupDao();
        return transactionGroupDao.loadAll();
    }
}
