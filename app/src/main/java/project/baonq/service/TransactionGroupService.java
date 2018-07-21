package project.baonq.service;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

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

    public Long addTransactionGroup(Long ledgerId, String name, int transactionType) {
        TransactionGroup transactionGroup = new TransactionGroup();
        transactionGroup.setLedger_id(ledgerId);
        transactionGroup.setName(name);
        transactionGroup.setTransaction_type(transactionType);
        transactionGroup.setStatus(TransactionGroupStatus.ENABLE.getStatus());
        Long lastUpdate = System.currentTimeMillis();
        transactionGroup.setInsert_date(lastUpdate);
        transactionGroup.setLast_update(lastUpdate);
        return new TransactionGroupDAO(application).addTransactionGroup(transactionGroup);
    }

    public Long getTransactionGroupID(Long ledger_id, int transaction_type, String name) {
        return new TransactionGroupDAO(application).getTransactionGroupID(ledger_id, transaction_type, name);
    }

    public TransactionGroup getTransactionGroupByID(Long id) {
        return new TransactionGroupDAO(application).getTransactionGroupByID(id);
    }

    public Long getLastUpdateTime() {
        SharedPreferences sharedPreferences = application.getSharedPreferences("sync", Context.MODE_PRIVATE);
        return sharedPreferences.getLong(TransactionGroupSyncService.TRANC_GROUP_LASTUPDATE, Long.parseLong("0"));
    }

    public Long getLastUpdateTimeFromDb() {
        TransactionGroup group = new TransactionGroupDAO(application).findLastUpdateGroup();
        if (group != null) {
            return group.getLast_update();
        } else {
            return Long.parseLong("0");
        }
    }

    public void insertOrUpdate(List<TransactionGroup> groups) {
        new TransactionGroupDAO(application).insertOrUpdate(groups);
    }
}
