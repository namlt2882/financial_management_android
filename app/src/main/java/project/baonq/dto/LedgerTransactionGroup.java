/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.baonq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import project.baonq.model.TransactionGroup;

public class LedgerTransactionGroup implements Serializable {

    @JsonProperty("server_id")
    private Long serverId;

    private List<TransactionGroup> transactionGroups = new LinkedList<>();

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public List<TransactionGroup> getTransactionGroups() {
        return transactionGroups;
    }

    public void setTransactionGroups(List<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups;
    }
}
