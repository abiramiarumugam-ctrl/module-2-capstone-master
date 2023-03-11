package com.techelevator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techelevator.tenmo.controller.UserController;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private UserDao mockUserDao;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetUserBalance() throws Exception {
        int userId = 1;
        BigDecimal balance = new BigDecimal("100.00");

        when(mockUserDao.getUserBalance(userId)).thenReturn(balance);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + userId + "/balance"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(balance.toString()));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(new User(), new User());

        when(mockUserDao.findAll()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(users)));
    }

    @Test
    public void getAllUsernames_returnsListOfUsernames() {
        // Arrange
        List<String> expectedUsernames = Arrays.asList("user1", "user2", "user3");
        when(mockUserDao.findAllUsernames()).thenReturn(expectedUsernames);

        // Act
        List<String> actualUsernames = userController.getAllUsernames();

        // Assert
        assertEquals(expectedUsernames, actualUsernames);
    }

}
