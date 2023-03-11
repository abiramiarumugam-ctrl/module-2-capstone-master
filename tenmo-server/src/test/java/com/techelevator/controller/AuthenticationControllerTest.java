package com.techelevator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techelevator.tenmo.TenmoApplication;
import com.techelevator.tenmo.controller.AuthenticationController;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.LoginDto;
import com.techelevator.tenmo.model.LoginResponseDto;
import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TenmoApplication.class)
public class AuthenticationControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private AuthenticationController authenticationController;
    private TokenProvider tokenProvider;
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;

    @Before
    public void setUp() {
        // Create the mock dependencies
        tokenProvider = mock(TokenProvider.class);
        userDao = mock(UserDao.class);

        // Create a real instance of AuthenticationManager
        authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);

        // Create the AuthenticationController with the dependencies
        authenticationController = new AuthenticationController(tokenProvider, authenticationManagerBuilder, userDao);

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testLogin() throws Exception{
        // have not figured out this one yet
        User testUser = new User();
        testUser.setUsername("sassy");
        testUser.setPassword("sosassy");

        LoginDto testDto = new LoginDto();
        testDto.setUsername(testUser.getUsername());
        testDto.setPassword(testUser.getPassword());

        String json = toJson(testDto);
        MvcResult thisResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk()).andReturn();

        String resultContent = thisResult.getResponse().getContentAsString();
        LoginResponseDto response = mapper.readValue(resultContent, LoginResponseDto.class);

        Assert.assertEquals(testDto.getUsername(), response.getUser().getUsername());
        Assert.assertNotNull(response.getToken());

//        RequestBuilder request = MockMvcRequestBuilders.post("/login");
//        MvcResult result = mockMvc.perform(request).andReturn();
//        Assert.assertNotNull(result);

//        LoginResponseDto testResponseDto = new LoginResponseDto("test", testUser);
//
//        when(authenticationController.login(testDto)).thenReturn(testResponseDto);
//
//        notNull(authenticationController.login(testDto));
    }

    @Test
    public void testRegister() {
        // Create a mock register user DTO object
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("username");
        registerUserDto.setPassword("password");

        // Mock the userDao's create method to return true
        when(userDao.create(registerUserDto.getUsername(), registerUserDto.getPassword())).thenReturn(true);

        // Call the register method
        authenticationController.register(registerUserDto);

        // Verify that the userDao's create method was called with the correct parameters
        verify(userDao, times(1)).create(registerUserDto.getUsername(), registerUserDto.getPassword());
    }

    private String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
