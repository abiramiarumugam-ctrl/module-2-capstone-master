package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFound;
import com.techelevator.tenmo.exception.TransferNotFound;
import com.techelevator.tenmo.model.Transfer;
import org.jboss.logging.BasicLogger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT * " +
                "FROM transfer " +
                "WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);

        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        if (Objects.isNull(transfer)){
            throw new TransferNotFound("Transfer id " + transferId + " was not found.");
        }
        return transfer;
    }

    @Override
    public void doTransfer(Transfer transfer, int id) {

        String sqlDoTransfer = "BEGIN; " +
                "UPDATE account SET balance = balance - ? " +
                "WHERE account_id = ?; " +
                "UPDATE account SET balance = balance + ? " +
                "WHERE account_id = ?; " +
                "COMMIT;";

        jdbcTemplate.update(sqlDoTransfer, transfer.getAmount(), transfer.getAccountFrom(), transfer.getAmount(), transfer.getAccountTo());

    }

    @Override
    public int createTransfer(Transfer transfer) {
        // update the balance where account from id = account id and account to id = account id
        String createTransfer = "INSERT INTO transfer (transfer_status_id, transfer_type_id, account_from, account_to, amount) " +
                "values(?,?,?,?,?) returning transfer_id";
        int senderId;
        int recipientId;
        int transferStatus = transfer.getTransferStatus();
        int transferType = transfer.getTransferType();
        if(transferType == 1) {
            senderId = transfer.getAccountFrom();
            recipientId = transfer.getAccountTo();
        } else {
            recipientId = transfer.getAccountFrom();
            senderId = transfer.getAccountTo();
        }
        BigDecimal amount = transfer.getAmount();


        return jdbcTemplate.queryForObject(createTransfer, int.class, transferStatus, transferType, senderId, recipientId, amount);

    }

    @Override
    public void updateTransferStatus(Integer transferStatusId, int transferId){
        String sql = "UPDATE transfer " +
                "SET transfer_status_id = ? " +
                "WHERE transfer_id = ?;";

            jdbcTemplate.update(sql, transferStatusId, transferId);
    }

    @Override
    public List<Transfer> transferHistory(int id) {
        String sql = "SELECT * " +
                "FROM transfer " +
                "WHERE account_from = ? or account_to = ? " +
                "ORDER BY transfer_id";
        List<Transfer> listOfAccountTransfers = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id, id);
        while (rowSet.next()){
            Transfer transfer = mapRowToTransfer(rowSet);
            listOfAccountTransfers.add(transfer);
        }
        if (listOfAccountTransfers.size() < 1){
            throw new AccountNotFound("Account id " + " was not found or transfer history is empty.");
        }
        return listOfAccountTransfers;
    }

    @Override
    public List<Transfer> pendingRequests(int accountFrom){
        List<Transfer> pendingRequests = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM transfer " +
                "WHERE account_from = ? " +
                "AND transfer_type_id = 1 " +
                "AND transfer_status_id = 1 " +
                "ORDER BY transfer_id;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountFrom);
        while(results.next()){
            pendingRequests.add(mapRowToTransfer(results));
        }
        if(pendingRequests.size() < 1){
            throw new AccountNotFound("Account with id " + accountFrom + " was not found or has no pending requests.");
        }
        return pendingRequests;
    }

    @Override
    public List<Transfer> getAllUserTransfers(int userAccountId) {
        List<Transfer> allUserTransfers = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM transfer " +
                "WHERE ? IN (account_to, account_from) " +
                "ORDER BY transfer_id DESC";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userAccountId);
        while(results.next()){
            allUserTransfers.add(mapRowToTransfer(results));
        }
        if (allUserTransfers.size() < 1){
            throw new AccountNotFound("Account id " + " doesn't exist or has no transfers.");
        }
        return allUserTransfers;
    }


    private Transfer mapRowToTransfer(SqlRowSet rs) {

        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferType(rs.getInt("transfer_type_id"));
        transfer.setTransferStatus(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));

        return transfer;
    }
}
