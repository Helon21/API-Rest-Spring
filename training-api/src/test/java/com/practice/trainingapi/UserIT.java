package com.practice.trainingapi;

import com.practice.trainingapi.web.dto.UserCreateDTO;
import com.practice.trainingapi.web.dto.UserResponseDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class UserIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUser_WithValidUsernameAndPassword_ReturnCreatedUserWithStatus200() {
        UserResponseDTO responseBody = testClient
                .post()
                .uri("/api/v1/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("helen215@gmail.com", "123456"))
                .exchange()
                .expectStatus()
                .isCreated().expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("helen@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");
    }
}
