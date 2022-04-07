package com.apitest;

import com.apitest.config.AppConfig;
import com.apitest.dataModel.User;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.apitest.matchers.CorrespondToPatternMatcher.correspondToPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class CRUDUserTest extends BasePetStoreTest {

    @Test
    @DisplayName("Create one user")
    public void createOneUser() {
        User newUser = prepareBasicUser();
        ResponseEntity<User> response = restService.postForObject(endpoints.getBaseUrl(), headers, newUser, User.class, endpoints.getUserEndpoint("user"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain correct message", response.getBody(), equalTo(newUser));
    }

    @Test
    @DisplayName("Create users with list")
    public void createUsersWithList() {
        List<User> newUsers = prepareUsers(3);
        ResponseEntity<User[]> response = restService.postForObject(endpoints.getBaseUrl(), headers, newUsers, User[].class, endpoints.getUserEndpoint("createWithList"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain correct messages", Sets.newHashSet(response.getBody()), equalTo(Sets.newHashSet(newUsers)));
    }

    @Test
    @DisplayName("Create users without content")
    public void createUsersWithoutContent() {
        List<User> newUsers = prepareUsers(3);
        ResponseEntity<String> response = restService.postForObject(endpoints.getBaseUrl(), headers, Collections.EMPTY_LIST, String.class, endpoints.getUserEndpoint("createWithList"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response doesn't contain correct messages", response.getBody(), equalTo("No User provided. Try again?"));
    }

    @Test
    @DisplayName("Login users")
    public void loginUsers() {
        User newUser = prepareBasicUser();
        restService.postForObject(endpoints.getBaseUrl(), headers, newUser, User.class, endpoints.getUserEndpoint("user"));
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("username", newUser.getUsername());
        queryParam.add("password", newUser.getPassword());
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, queryParam, String.class, endpoints.getUserEndpoint("login"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain correct message", response.getBody(), correspondToPattern("Logged in user session: \\d*"));
        assertThat("Response doesn't contain correct x-expires-after header", response.getHeaders().get("x-expires-after").get(0), not(emptyString()));
        assertThat("Response doesn't contain correct x-expires-after header", response.getHeaders().get("x-rate-limit").get(0), equalTo("5000"));
    }

    @Test
    @DisplayName("Login users wrong password")
    public void loginUsersWrongPassword() {
        User newUser = prepareBasicUser();
        restService.postForObject(endpoints.getBaseUrl(), headers, newUser, User.class, endpoints.getUserEndpoint("user"));
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("username", newUser.getUsername());
        queryParam.add("password", RandomStringUtils.randomAlphanumeric(10));
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, queryParam, String.class, endpoints.getUserEndpoint("login"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    @DisplayName("Login users wrong login")
    public void loginUsersWrongLogin() {
        User newUser = prepareBasicUser();
        restService.postForObject(endpoints.getBaseUrl(), headers, newUser, User.class, endpoints.getUserEndpoint("user"));
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("username", RandomStringUtils.randomAlphanumeric(10));
        queryParam.add("password", newUser.getPassword());
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, queryParam, String.class, endpoints.getUserEndpoint("login"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    @DisplayName("Logout users")
    public void logoutUser() {
        User newUser = prepareBasicUser();
        restService.postForObject(endpoints.getBaseUrl(), headers, newUser, User.class, endpoints.getUserEndpoint("user"));
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, String.class, endpoints.getUserEndpoint("logout"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain correct message", response.getBody(), equalTo("User logged out"));
    }

    @Test
    @DisplayName("Get user by username")
    public void getUserByUsername() {
        User newUser = prepareBasicUser();
        restService.postForObject(endpoints.getBaseUrl(), headers, newUser, User.class, endpoints.getUserEndpoint("user"));
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", newUser.getUsername());
        ResponseEntity<User> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, User.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain correct message", response.getBody(), equalTo(newUser));
    }

    @Test
    @DisplayName("Get user by incorrect username")
    public void getUserByIncorrectUsername() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", RandomStringUtils.randomAlphanumeric(10));
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response message is not as expected", response.getBody(), equalTo("User not found"));
    }

    @Test
    @DisplayName("Get user without username")
    public void getUserWithoutUsername() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", "");
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response message is not as expected", response.getBody(), equalTo("No username provided. Try again?"));
    }

    @Test
    @DisplayName("Update user by username")
    public void updateUserByUsername() {
        User newUser = prepareBasicUser();
        restService.postForObject(endpoints.getBaseUrl(), headers, newUser, User.class, endpoints.getUserEndpoint("user"));
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", newUser.getUsername());
        newUser.setUserStatus(User.UserStatuses.CLOSED.getStatus());
        ResponseEntity<User> response = restService.putForObject(endpoints.getBaseUrl(), headers, newUser, urlParams, User.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain updated user", response.getBody(), equalTo(newUser));
    }

    @Test
    @DisplayName("Update user by incorrect username")
    public void updateUserIncorrectUsername() {
        User newUser = prepareBasicUser();
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", newUser.getUsername());
        ResponseEntity<String> response = restService.putForObject(endpoints.getBaseUrl(), headers, newUser, urlParams, String.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response message is not as expected", response.getBody(), equalTo("User not found"));
    }

    @Test
    @DisplayName("Update user without username")
    public void updateUserWithoutUsername() {
        User newUser = prepareBasicUser();
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", "");
        ResponseEntity<String> response = restService.putForObject(endpoints.getBaseUrl(), headers, newUser, urlParams, String.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response message is not as expected", response.getBody(), equalTo("No Username provided. Try again?"));
    }

    @Test
    @DisplayName("Update user without body")
    public void updateUserWithoutBody() {
        User newUser = prepareBasicUser();
        restService.postForObject(endpoints.getBaseUrl(), headers, newUser, User.class, endpoints.getUserEndpoint("user"));
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", newUser.getUsername());
        ResponseEntity<String> response = restService.putForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response message is not as expected", response.getBody(), equalTo("No Username provided. Try again?"));
    }

    @Test
    @DisplayName("Delete user by username")
    public void deleteUserByUsername() {
        User newUser = prepareBasicUser();
        restService.postForObject(endpoints.getBaseUrl(), headers, newUser, User.class, endpoints.getUserEndpoint("user"));
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", newUser.getUsername());
        ResponseEntity<User> response = restService.deleteForObject(endpoints.getBaseUrl(), headers, urlParams, User.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response is not empty", response.getBody(), nullValue());
    }

    @Test
    @DisplayName("Delete user by incorrect username")
    public void deleteUserByIncorrectUsername() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", RandomStringUtils.randomAlphanumeric(10));
        ResponseEntity<String> response = restService.deleteForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response message is not as expected", response.getBody(), equalTo("User not found"));
    }

    @Test
    @DisplayName("Delete user without username")
    public void deleteUserWithoutUsername() {
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("username", "");
        ResponseEntity<String> response = restService.deleteForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getUserEndpoint("userByName"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response message is not as expected", response.getBody(), equalTo("No username provided. Try again?"));
    }

    public List<User> prepareUsers(int number) {
        Random rnd = new Random();
        return IntStream.range(0, number).mapToObj(i -> prepareBasicUser()).collect(Collectors.toList());
    }

    public User prepareBasicUser() {
        Random rnd = new Random();
        return User.builder().id(rnd.nextLong())
                .email(RandomStringUtils.randomAlphanumeric(10))
                .firstName(RandomStringUtils.randomAlphanumeric(10))
                .lastName(RandomStringUtils.randomAlphanumeric(10))
                .phone(RandomStringUtils.randomAlphanumeric(10))
                .username(RandomStringUtils.randomAlphanumeric(10))
                .password(RandomStringUtils.randomAlphanumeric(10))
                .userStatus(User.UserStatuses.ACTIVE.getStatus())
                .build();
    }
}
