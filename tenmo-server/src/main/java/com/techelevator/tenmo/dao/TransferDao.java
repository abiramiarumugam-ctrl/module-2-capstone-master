package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface TransferDao {

    Transfer getTransferById(int transferId);
    void doTransfer(Transfer transfer, int id);
    int createTransfer(Transfer transfer);
    void updateTransferStatus (Integer transferStatusId, int transferId);
    List<Transfer> transferHistory(int id);
    List<Transfer> pendingRequests(int accountFrom);

    List<Transfer> getAllUserTransfers(int userId);
}
