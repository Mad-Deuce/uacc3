package com.dms_uz.auth.controller;

import com.dms_uz.auth.dto.UserDTO;
import com.dms_uz.auth.exception.NoEntityException;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

//import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql(executionPhase = AFTER_TEST_METHOD,
            scripts = "/delete_test_user.sql")
    void addUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test_username");
        userDTO.setPassword("test_password");
        userDTO.setPasswordConfirm("test_password");

        ResponseEntity<UserDTO> response = restTemplate
                .withBasicAuth("postgres", "postgres")
                .postForEntity("/api/admin/users/", userDTO, UserDTO.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    @Sql(executionPhase = BEFORE_TEST_METHOD,
            scripts = "/insert_test_user.sql")
    @Sql(executionPhase = AFTER_TEST_METHOD,
            scripts = "/delete_test_user.sql")
    void getAllUsers() {
        ResponseEntity<List<UserDTO>> response = restTemplate
                .withBasicAuth("postgres", "postgres")
                .exchange("/api/admin/users/", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<UserDTO>>() {
                });
        List<UserDTO> users = response.getBody();
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(users, hasItem(hasProperty("username", equalTo("test_username"))));

    }

    @Test
    void getUser() {
        ResponseEntity<UserDTO> response = restTemplate
                .withBasicAuth("postgres", "postgres")
                .getForEntity("/api/admin/users/{id}/", UserDTO.class, 5);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), hasProperty("username", equalTo("postgres")));
    }

    @Test
    void deleteUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test_username");
        userDTO.setPassword("test_password");
        userDTO.setPasswordConfirm("test_password");

        Long id = restTemplate
                .withBasicAuth("postgres", "postgres")
                .postForObject("/api/admin/users/", userDTO, UserDTO.class)
                .getId();

        restTemplate
                .withBasicAuth("postgres", "postgres")
                .delete("/api/admin/users/{id}/", id);

        ResponseEntity<UserDTO> response = restTemplate
                .withBasicAuth("postgres", "postgres")
                .getForEntity("/api/admin/users/{id}/", UserDTO.class, id);
        assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    void updateUser() {
    }


    @Test
    void exportToExcel() {
    }


    @After
    public void resetDb() {
//        repository.deleteAll();
    }

}