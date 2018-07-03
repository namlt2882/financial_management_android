package project.baonq.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Ledger implements Serializable {

    private Integer id;
    private Long server_id;
    private String name;
    private String currency;
    private float currentBalance;
    private boolean countedOnReport;
    private Date insertDate;
    private Date lastUpdate;
    private int status;
    private Set transactions = new HashSet(0);
    private Set transactionGroups = new HashSet(0);

    public Ledger() {
    }

    public float getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(float currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getServer_id() {
        return server_id;
    }

    public void setServer_id(Long server_id) {
        this.server_id = server_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isCountedOnReport() {
        return countedOnReport;
    }

    public void setCountedOnReport(boolean countedOnReport) {
        this.countedOnReport = countedOnReport;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Set getTransactions() {
        return transactions;
    }

    public void setTransactions(Set transactions) {
        this.transactions = transactions;
    }

    public Set getTransactionGroups() {
        return transactionGroups;
    }

    public void setTransactionGroups(Set transactionGroups) {
        this.transactionGroups = transactionGroups;
    }
}
