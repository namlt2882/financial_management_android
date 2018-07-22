package project.baonq.service;

import java.util.List;

import project.baonq.enumeration.TransactionStatus;
import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionDao;
import project.baonq.model.TransactionGroup;
import project.baonq.model.TransactionGroupDao;


public class TransactionService {
    private DaoSession daoSession;

    public TransactionService(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public Long addTransaction(Long ledger_id, Long group_id, double balance) {
        long insert_date = System.currentTimeMillis();
        Transaction transaction = new Transaction();
        transaction.setLedger_id(ledger_id);
        transaction.setGroup_id(group_id);
        transaction.setBalance(balance);
        transaction.setInsert_date(insert_date);
        transaction.setLast_update(insert_date);
        transaction.setStatus(TransactionStatus.ENABLE.getStatus());
        TransactionDao transactionDao = daoSession.getTransactionDao();
        transactionDao.insert(transaction);
        return transactionDao.getKey(transaction);
    }

    public void addTransaction(double amount, String txtNote, String date) {
//        transaction = new Transaction();
//        transaction.setBalance(amount);
//        transaction.setNote(txtNote);
//        transaction.setTdate(date);
//        transaction.setInsert_date(LocalDate.now().toString());
    }

    public void updateTransaction(Long ledger_id, Long group_id, double balance) {
        Transaction transactionForUpdate = getTransactionNeedForUpdate(ledger_id, group_id);
        transactionForUpdate.setBalance(balance);
        TransactionDao transactionDao = daoSession.getTransactionDao();
        transactionDao.update(transactionForUpdate);
    }

    public List<Transaction> getTransactionByLedger_Id(Long ledger_id) {
        TransactionDao transactionDao = daoSession.getTransactionDao();
        return transactionDao.queryBuilder().where(TransactionDao.Properties.Ledger_id.eq(ledger_id)).list();
    }


    private Transaction getTransactionNeedForUpdate(Long ledger_id, Long group_id) {
        TransactionDao transactionDao = daoSession.getTransactionDao();
        return transactionDao.queryBuilder()
                .where(TransactionDao.Properties.Ledger_id.eq(ledger_id), TransactionDao.Properties.Group_id.eq(group_id))
                .unique();
    }

    public List<Transaction> getAll() {
        TransactionDao transactionDao = daoSession.getTransactionDao();
        return transactionDao.loadAll();
    }
}
