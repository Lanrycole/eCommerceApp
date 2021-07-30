package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testpasswd")).thenReturn("hashed");
        CreateUserRequest r = createUserRequest();
        final ResponseEntity<User> response = userController.createUser(r);
        assertEquals("test", response.getBody().getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("hashed", u.getPassword());

    }

    @Test
    public void findUserByUsername() throws Exception {

        User user = createUser();
        when(userRepo.findByUsername("test")).thenReturn(user);
        ResponseEntity<User> responseUser = userController.findByUserName("test");
        assertNotNull(responseUser);
        assertEquals(200, responseUser.getStatusCode().value());
        assertEquals("test", Objects.requireNonNull(responseUser.getBody()).getUsername());

    }

    @Test
    public void findUserByUserId() throws Exception {

        User user = createUser();
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<User> responseUser = userController.findById(user.getId());
        assertNotNull(responseUser);
        assertEquals(200, responseUser.getStatusCode().value());
        assertEquals("test", Objects.requireNonNull(responseUser.getBody()).getUsername());
        assertEquals(1L, responseUser.getBody().getId());

    }

    private static CreateUserRequest createUserRequest() {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("test");
        user.setPassword("testpasswd");
        user.setConfirmPassword("testpasswd");
        return user;
    }

    private static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testpasswd");
        return user;
    }
}
