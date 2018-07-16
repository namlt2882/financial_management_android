package project.baonq.service;

import java.util.List;

import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionDao;


public class TransactionService {
    private static DaoSession daoSession;
    private static Transaction transaction;
    private static TransactionDao transactionDao;

    public TransactionService(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public static void setDaoSession(DaoSession daoSession) {
        TransactionService.daoSession = daoSession;
    }


    public static Long addTransaction(Long ledger_id, Long group_id, double balance, long insert_date, int status) {
        transaction = new Transaction();
        transaction.setLedger_id(ledger_id);
        transaction.setGroup_id(group_id);
        transaction.setBalance(balance);
        transaction.setInsert_date(insert_date);
        transaction.setStatus(status);
        transactionDao = daoSession.getTransactionDao();
        transactionDao.insert(transaction);
        return transactionDao.getKey(transaction);
    }

    public static void addTransaction(double amount, String txtNote, String date) {
//        transaction = new Transaction();
//        transaction.setBalance(amount);
//        transaction.setNote(txtNote);
//        transaction.setTdate(date);
//        transaction.setInsert_date(LocalDate.now().toString());
    }

    public static void updateTransaction(Long ledger_id, Long group_id, double balance) {
        Transaction transactionForUpdate = getTransactionNeedForUpdate(ledger_id, group_id);
        transactionForUpdate.setBalance(balance);
        transactionDao.update(transactionForUpdate);
    }

    public static List<Transaction> getTransactionByLedger_Id(Long ledger_id) {
        transactionDao = daoSession.getTransactionDao();
        return transactionDao.queryBuilder().where(TransactionDao.Properties.Ledger_id.eq(ledger_id)).list();
    }


    private static Transaction getTransactionNeedForUpdate(Long ledger_id, Long group_id) {
        transactionDao = daoSession.getTransactionDao();
        return transactionDao.queryBuilder()
                .where(TransactionDao.Properties.Ledger_id.eq(ledger_id), TransactionDao.Properties.Group_id.eq(group_id))
                .unique();
    }
}
