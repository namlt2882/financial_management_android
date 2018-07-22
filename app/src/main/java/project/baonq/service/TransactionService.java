package project.baonq.service;

import android.app.Application;

import java.util.List;

import project.baonq.dao.TransactionDAO;
import project.baonq.enumeration.TransactionStatus;
import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionDao;
import project.baonq.model.TransactionGroup;
import project.baonq.model.TransactionGroupDao;


public class TransactionService extends Service {

    private TransactionDAO transactionDAO;

    public TransactionService(Application application) {
        super(application);
        transactionDAO = new TransactionDAO(application);
    }

    public Long addTransaction(Long ledger_id, Long group_id, double balance, String tdate, String note) {
        Transaction transaction = new Transaction();
        transaction.setLedger_id(ledger_id);
        transaction.setGroup_id(group_id);
        transaction.setBalance(balance);
        transaction.setNote(note);
        transaction.setTdate(tdate);
        //insert date and last update time
        long insert_date = System.currentTimeMillis();
        transaction.setInsert_date(insert_date);
        transaction.setLast_update(insert_date);
        //status
        transaction.setStatus(TransactionStatus.ENABLE.getStatus());
        return transactionDAO.addTransaction(transaction);
    }

    public List<Transaction> getByLedgerId(Long ledger_id) {
        return transactionDAO.getTransactionByLedgerId(ledger_id);
    }

    private Transaction getTransactionNeedForUpdate(Long ledger_id, Long group_id) {
        TransactionDao transactionDao = ((App) application).getDaoSession().getTransactionDao();
        return transactionDao.queryBuilder()
                .where(TransactionDao.Properties.Ledger_id.eq(ledger_id), TransactionDao.Properties.Group_id.eq(group_id))
                .unique();
    }

    public List<Transaction> getAll() {
        TransactionDao transactionDao = ((App) application).getDaoSession().getTransactionDao();
        return transactionDao.loadAll();
    }

}
