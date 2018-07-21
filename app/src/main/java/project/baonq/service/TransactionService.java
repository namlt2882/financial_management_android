package project.baonq.service;

import android.app.Application;

import java.util.List;

import project.baonq.dao.TransactionDAO;
import project.baonq.enumeration.TransactionStatus;
import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionDao;


public class TransactionService extends Service {


    public TransactionService(Application application) {
        super(application);
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
        return new TransactionDAO(application).addTransaction(transaction);
    }

    public void updateTransaction(Long ledger_id, Long group_id, double balance) {
        Transaction transactionForUpdate = getTransactionNeedForUpdate(ledger_id, group_id);
        transactionForUpdate.setBalance(balance);
        new TransactionDAO(application).updateTransaction(transactionForUpdate);
    }

    public List<Transaction> getTransactionByLedgerId(Long ledger_id) {
        return new TransactionDAO(application).getTransactionByLedgerId(ledger_id);
    }


    private Transaction getTransactionNeedForUpdate(Long ledger_id, Long group_id) {
        return new TransactionDAO(application).getTransactionNeedForUpdate(ledger_id, group_id);
    }
}
