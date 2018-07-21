package project.baonq.service;

import android.app.Application;

import org.greenrobot.greendao.query.QueryBuilder;

import project.baonq.dao.TransactionGroupDAO;
import project.baonq.enumeration.TransactionGroupStatus;
import project.baonq.enumeration.TransactionStatus;
import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionGroup;
import project.baonq.model.TransactionGroupDao;

public class TransactionGroupService extends Service {

    public TransactionGroupService(Application application) {
        super(application);
    }

    public Long addTransactionGroup(Long ledgerId, String name, int transactionType, int status) {
        TransactionGroup transactionGroup = new TransactionGroup();
        transactionGroup.setLedger_id(ledgerId);
        transactionGroup.setName(name);
        transactionGroup.setTransaction_type(transactionType);
        transactionGroup.setStatus(status);
        transactionGroup.setStatus(TransactionGroupStatus.ENABLE.getStatus());
        return new TransactionGroupDAO(application).addTransactionGroup(transactionGroup);
    }

    public Long getTransactionGroupID(Long ledger_id, int transaction_type, String name) {
        return new TransactionGroupDAO(application).getTransactionGroupID(ledger_id, transaction_type, name);
    }

    public TransactionGroup getTransactionGroupByID(Long id) {
        return new TransactionGroupDAO(application).getTransactionGroupByID(id);
    }
}
