package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.exception.AccountNotFound;
import com.techelevator.tenmo.exception.UserNotFound;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests {
    protected static final User USER_1 = new User(1001, "user1", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user2", "user2", "USER");
    private static final User USER_3 = new User(1003, "user3", "user3", "USER");
    private static final int[] USER_ACCOUNT_IDS = {2001, 2002, 2003};
    private static final BigDecimal[] USER_BALANCES = {new BigDecimal("900.00"), new BigDecimal("1000.00"), new BigDecimal("1100.00")};

    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findIdByUsername_given_null_throws_exception() {
        sut.findIdByUsername(null);
    }

    @Test(expected = UserNotFound.class)
    public void findIdByUsername_given_invalid_username_throws_exception() {
        sut.findIdByUsername("invalid");
    }

    @Test
    public void findIdByUsername_given_valid_user_returns_user_id() {
        int actualUserId = sut.findIdByUsername(USER_1.getUsername());

        Assert.assertEquals(USER_1.getId(), actualUserId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByUsername_given_null_throws_exception() {
        sut.findByUsername(null);
    }

    @Test(expected = UserNotFound.class)
    public void findByUsername_given_invalid_username_throws_exception() {
        sut.findByUsername("invalid");
    }

    @Test
    public void findByUsername_given_valid_user_returns_user() {
        User actualUser = sut.findByUsername(USER_1.getUsername());

        Assert.assertEquals(USER_1, actualUser);
    }

    @Test
    public void getUserById_given_invalid_user_id_returns_null() {
        User actualUser = sut.getUserById(-1);

        Assert.assertNull(actualUser);
    }

    @Test
    public void getUserById_given_valid_user_id_returns_user() {
        User actualUser = sut.getUserById(USER_1.getId());

        Assert.assertEquals(USER_1, actualUser);
    }

    @Test
    public void findAll_returns_all_users() {
        List<User> users = sut.findAll();

        Assert.assertNotNull(users);
        Assert.assertEquals(3, users.size());
        Assert.assertEquals(USER_1, users.get(0));
        Assert.assertEquals(USER_2, users.get(1));
        Assert.assertEquals(USER_3, users.get(2));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void create_user_with_null_username() {
        sut.create(null, USER_3.getPassword());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void create_user_with_existing_username() {
        sut.create(USER_1.getUsername(), USER_3.getPassword());
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_user_with_null_password() {
        sut.create(USER_3.getUsername(), null);
    }

    @Test
    public void create_user_creates_a_user() {
        User newUser = new User(-1, "new", "user", "USER");

        boolean userWasCreated = sut.create(newUser.getUsername(), newUser.getPassword());

        Assert.assertTrue(userWasCreated);

        User actualUser = sut.findByUsername(newUser.getUsername());
        newUser.setId(actualUser.getId());

        actualUser.setPassword(newUser.getPassword()); // reset password back to unhashed password for testing
        Assert.assertEquals(newUser, actualUser);
    }

    // Added by our group

    @Test(expected = AccountNotFound.class)
    public void get_balance_by_account_id_invalid_throws_exception() {
        sut.getBalanceByAccountId(4000);
    }

    @Test
    public void get_balance_by_account_id_returns_amount() {
        Assert.assertEquals(USER_BALANCES[0], sut.getBalanceByAccountId(USER_ACCOUNT_IDS[0]));
    }

    @Test
    public void find_all_username_returns_list() {
        List<String> usernames = sut.findAllUsernames();

        Assert.assertEquals(3, usernames.size());
        Assert.assertEquals(USER_1.getUsername(), usernames.get(0));
        Assert.assertEquals(USER_2.getUsername(), usernames.get(1));
        Assert.assertEquals(USER_3.getUsername(), usernames.get(2));
    }

    @Test
    public void search_all_usernames_returns_differently_depending_on_input() {
        List<String> users = sut.searchUsernames("user");
        List<String> space = sut.searchUsernames(" ");
        List<String> one = sut.searchUsernames("1");

        Assert.assertEquals(3, users.size());
        Assert.assertEquals(0, space.size());
        Assert.assertEquals(1, one.size());
    }

    @Test(expected = AccountNotFound.class)
    public void find_username_by_account_id_invalid_throws_exception(){
        sut.findUsernameByAccountId(4000);
    }

    @Test
    public void find_username_by_account_id_returns_username(){
        String username1 = sut.findUsernameByAccountId(USER_ACCOUNT_IDS[0]);
        String username2 = sut.findUsernameByAccountId(USER_ACCOUNT_IDS[1]);

        Assert.assertEquals(username1, USER_1.getUsername());
        Assert.assertEquals(username2, USER_2.getUsername());
    }

    @Test(expected = UserNotFound.class)
    public void get_user_balance_invalid_id_throws_exception(){
        sut.getUserBalance(4000);
    }

    @Test
    public void get_user_balance_returns_balance(){
        BigDecimal user1 = sut.getUserBalance(USER_1.getId());
        BigDecimal user3 = sut.getUserBalance(USER_3.getId());

        Assert.assertEquals(user1, USER_BALANCES[0]);
        Assert.assertEquals(user3, USER_BALANCES[2]);
    }

    @Test(expected = UserNotFound.class)
    public void user_to_account_invalid_id_throws_exception(){
        sut.userToAccount(4000);
    }

    @Test
    public void user_to_account_returns_account_id(){
        int account1 = sut.userToAccount(USER_1.getId());
        int account2 = sut.userToAccount(USER_2.getId());

        Assert.assertEquals(USER_ACCOUNT_IDS[0], account1);
        Assert.assertEquals(USER_ACCOUNT_IDS[1], account2);
    }
}
