package project.baonq.dao;

import android.app.Application;

import java.util.List;

import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionDao;

public class TransactionDAO extends DAO {


    public TransactionDAO(Application application) {
        super(application);
    }

    public Long addTransaction(Transaction transaction) {
        DaoSession daoSession = getDaoSession();
        TransactionDao transactionDao = daoSession.getTransactionDao();
        return transactionDao.insert(transaction);
    }

    public void updateTransaction(Transaction transaction) {
        DaoSession daoSession = getDaoSession();
        TransactionDao transactionDao = daoSession.getTransactionDao();
        transactionDao.update(transaction);
    }

    public List<Transaction> getTransactionByLedgerId(Long ledger_id){
        DaoSession daoSession = getDaoSession();
        TransactionDao transactionDao = daoSession.getTransactionDao();
        return transactionDao.queryBuilder().where(TransactionDao.Properties.Ledger_id.eq(ledger_id)).list();
    }

    public List<Transaction> getAll() {
        DaoSession daoSession = getDaoSession();
        TransactionDao transactionDao = daoSession.getTransactionDao();
        return transactionDao.loadAll();
    }

    public Transaction getTransactionNeedForUpdate(Long ledger_id, Long group_id) {
        DaoSession daoSession = getDaoSession();
        TransactionDao transactionDao = daoSession.getTransactionDao();
        Transaction result = transactionDao.queryBuilder()
                .where(TransactionDao.Properties.Ledger_id.eq(ledger_id), TransactionDao.Properties.Group_id.eq(group_id))
                .unique();
        return result;
    }
}
