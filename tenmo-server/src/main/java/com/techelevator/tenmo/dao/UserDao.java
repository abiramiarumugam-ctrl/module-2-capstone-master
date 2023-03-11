package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    BigDecimal getBalanceByAccountId(int accountId);

    List<User> findAll();

    User getUserById(int id);

    List<String> findAllUsernames();

    List<String> searchUsernames(String searchTerm);

    User findByUsername(String username);

    String findUsernameByAccountId(int accountId);

//    Transfer findTransferById(int transferId);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    BigDecimal getUserBalance(int id);

//    void doTransfer(Transfer transfer, int id);

//    int createTransfer(Transfer transfer);

    int userToAccount(int id);

    void addUserToContacts(Integer userId, Integer contactId);

    void removeUserFromContacts(Integer userId, Integer contactId);

    List<Integer> getContactsList(int userId);







//    List<Transfer> transferHistory(int id);

}
