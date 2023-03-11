package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFound;
import com.techelevator.tenmo.exception.TransferNotFound;
import com.techelevator.tenmo.exception.UserNotFound;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");

        int userId;
        try {
            userId = jdbcTemplate.queryForObject("SELECT user_id FROM tenmo_user WHERE username = ?", int.class, username);
        } catch (NullPointerException | EmptyResultDataAccessException e) {
            throw new UserNotFound("User " + username + " was not found.");
        }

        return userId;
    }



    @Override
    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            return mapRowToUser(results);
        } else {
            return null;
        }
    }

    @Override
    public BigDecimal getBalanceByAccountId(int accountId) {
        String sql = "SELECT balance FROM account " +
                     "WHERE account_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, accountId);

        } catch(NullPointerException | EmptyResultDataAccessException e){
            throw new AccountNotFound("Account id " + accountId + " not found.");
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }

        return users;
    }

    @Override
    public List<String> findAllUsernames(){
        String sql = "SELECT username FROM tenmo_user";

        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public List<String> searchUsernames(String searchTerm){
        String sql = "SELECT username " +
                "FROM tenmo_user " +
                "WHERE username ILIKE ? " +
                "ORDER BY username";
        return jdbcTemplate.queryForList(sql, String.class, "%" + searchTerm + "%");
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");

        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()) {
            return mapRowToUser(rowSet);
        }
        throw new UserNotFound("User " + username + " was not found.");
    }

    @Override
    public String findUsernameByAccountId(int accountId) {
       String sql = "SELECT username FROM tenmo_user " +
               "JOIN account USING (user_id) " +
               "WHERE account_id = ?";
       try {
           return jdbcTemplate.queryForObject(sql, String.class, accountId);

       } catch (NullPointerException | EmptyResultDataAccessException e){
            throw new AccountNotFound("Account id " + accountId + " was not found.");
       }

    }

    // Move to transfer?
    public Transfer findTransferById(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT * FROM transfer " +
                "WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, Transfer.class, transferId);

        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        if(Objects.isNull(transfer)){
            throw new TransferNotFound("Transfer id " + transferId + " was not found.");
        }
        return transfer;
    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username.toLowerCase(), password_hash);

        } catch (NullPointerException e){
            throw new DataIntegrityViolationException("Cannot be null.");
        }

//        if (newUserId == null) return false;

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }

    @Override
    public BigDecimal getUserBalance(int id) {
        String sql = "SELECT balance " +
                "FROM account " +
                "WHERE user_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, id);

        } catch (NullPointerException | EmptyResultDataAccessException e) {
            throw new UserNotFound("User with the id " + id + " was not found.");

        }
    }

    // Move to transfer?
    public void doTransfer(Transfer transfer, int id) {

        String sqlDoTransfer = "BEGIN; " +
                "UPDATE account SET balance = balance - ? " +
                "WHERE account_id = ?; " +
                "UPDATE account SET balance = balance + ? " +
                "WHERE account_id = ?; " +
                "COMMIT;";

            jdbcTemplate.update(sqlDoTransfer, transfer.getAmount(), transfer.getAccountFrom(), transfer.getAmount(), transfer.getAccountTo());

    }

    // Move to transfer?
    public int createTransfer(Transfer transfer) {
        // update the balance where account from id = account id and account to id = account id
        String createTransfer = "INSERT INTO transfer (transfer_status_id, transfer_type_id, account_from, account_to, amount) " +
                    "values(?,?,?,?,?) returning transfer_id";
        int transferStatus = transfer.getTransferStatus();
        int transferType = transfer.getTransferType();
        int senderId = transfer.getAccountFrom();
        int recipientId = transfer.getAccountTo();
        BigDecimal amount = transfer.getAmount();


        return jdbcTemplate.queryForObject(createTransfer, int.class, transferStatus, transferType, senderId, recipientId, amount);

    }

    public int userToAccount(int id) {
        String sql = "SELECT account_id " +
                "FROM account " +
                "WHERE user_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, int.class, id);

        } catch(NullPointerException | EmptyResultDataAccessException e){
            throw new UserNotFound("User with the id " + id + " was not found.");
        }
    }

    // Move to Transfer?
    public List<Transfer> transferHistory(int id) {
        String sql = "SELECT * " +
                "FROM transfer " +
                "WHERE account_from = ? or account_to = ?";
        List<Transfer> listOfAccountTransfers = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id, id);
        while (rowSet.next()){
            Transfer transfer = mapRowToTransfer(rowSet);
            listOfAccountTransfers.add(transfer);
        }
        if (listOfAccountTransfers.size() < 1){
            throw new AccountNotFound("Account id " + " was not found or has no history.");
        }
        return listOfAccountTransfers;
    }

    public void addUserToContacts(Integer userId, Integer contactId){
        String sql = "INSERT INTO user_contacts (user_id, contact_user_id) " +
                     "VALUES (?,?) RETURNING id";
        jdbcTemplate.queryForObject(sql, Integer.class, userId, contactId);

    };

    public void removeUserFromContacts(Integer userId, Integer contactId) {

        String sql = "DELETE FROM user_contacts " +
                    "WHERE user_id = ? AND contact_user_id = ? ";

        jdbcTemplate.update(sql, userId, contactId);
    }


    public List<Integer> getContactsList(int userId){
        String sql = "SELECT contact_user_id FROM " +
                     "user_contacts WHERE " +
                     "user_id = ? ";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);

    }

    // Map to Object Methods
    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
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
