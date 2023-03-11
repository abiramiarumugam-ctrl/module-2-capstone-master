package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ActiveService {

    // Properties
    private final String BASE_URL;
    private AuthenticatedUser currentUser;
    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    // Constructor & Setter
    public ActiveService(String BASE_URL, AuthenticatedUser currentUser) {
        this.BASE_URL = BASE_URL;
        this.currentUser = currentUser;
    }

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    // Service Methods

    public User getUserByName(String name) {
        for (User user : getAllUsers(currentUser)) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }
        BasicLogger.log("Username not found");
        return null;
    }

    public BigDecimal getUserBalance(AuthenticatedUser currentUser, int id) {
        BigDecimal balance = null;

        try {
            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(BASE_URL + "/user/" + id + "/balance",
                            HttpMethod.GET,
                            makeAuthEntity(currentUser.getToken()),
                            BigDecimal.class);

            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public BigDecimal getAccountBalance(int accountId) {
        BigDecimal balance = null;

        try {
            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(BASE_URL + "/account/" + accountId + "/balance",
                            HttpMethod.GET,
                            makeAuthEntity(authToken),
                            BigDecimal.class);

            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public List<User> getAllUsers(AuthenticatedUser currentUser) {
        List<User> users = null;
        HttpEntity<Void> entity = makeAuthEntity(currentUser.getToken());

        try {
            ResponseEntity<List<User>> response =
                    restTemplate.exchange(BASE_URL + "/user",
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<>() {
                            });
            users = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    public List<String> getAllUsernames() {
        List<String> usernames = null;
        HttpEntity<Void> entity = makeAuthEntity(currentUser.getToken());

        try {
            ResponseEntity<List<String>> response =
                    restTemplate.exchange(BASE_URL + "/user/usernames",
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<>() {
                            });
            usernames = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return usernames;
    }

    public int userToAccount(User user) {
        int accountId = 0;
        HttpEntity<Void> entity = makeAuthEntity(currentUser.getToken());

        try {
            accountId = restTemplate.exchange(BASE_URL + "/account/" + user.getId(),
                    HttpMethod.GET,
                    entity,
                    int.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return accountId;
    }

    public String accountIdToUsername(int accountId) {
        String username = null;
        HttpEntity<Void> entity = makeAuthEntity(currentUser.getToken());
        try {
            username = restTemplate.exchange(BASE_URL + "/account/" + accountId + "/username",
                    HttpMethod.GET,
                    entity,
                    String.class).getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return username;
    }

    public Transfer[] transferHistory(int accountId) {
        Transfer[] history = null;
        HttpEntity<Void> entity = makeAuthEntity(currentUser.getToken());

        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(BASE_URL + "transfers/history/" + accountId,
                            HttpMethod.GET,
                            entity,
                            Transfer[].class);

            history = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return history;
    }

    public List<String> searchForUsernames(String searchTerm) {
        List<String> results = null;
        HttpEntity<Void> entity = makeAuthEntity(currentUser.getToken());

        try {
            ResponseEntity<List<String>> response =
                    restTemplate.exchange(BASE_URL + "/search/" + searchTerm,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<>() {
                            });

            results = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return results;
    }

    public List<Integer> getContactsList(int userId) {
        List<Integer> contactIds = new ArrayList<>();
        HttpEntity<Void> entity = makeAuthEntity(currentUser.getToken());

        try {
            ResponseEntity<List<Integer>> response =
                    restTemplate.exchange(BASE_URL + "/user/contacts/" + userId,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<>() {
                            });

            contactIds = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return contactIds;
    }

    public void addUserToContacts(Integer userId, Integer contactId) {
        HttpEntity<Void> entity = makeAuthEntity(currentUser.getToken());

        try {
            restTemplate.exchange(BASE_URL + "/user/addContact/" + userId + "/" + contactId,
                    HttpMethod.POST,
                    entity,
                    Integer.class);

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void deleteUserFromContacts(Integer userId, Integer contactId) {
        HttpEntity<Void> entity = makeAuthEntity(currentUser.getToken());

        try {
            restTemplate.exchange(BASE_URL + "/user/removeContact/" + userId + "/" + contactId,
                    HttpMethod.DELETE,
                    entity,
                    Integer.class);

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    // Entity Convenience Methods

    private HttpEntity<Void> makeAuthEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<User> userEntity(String token, User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(user, headers);
    }

}
