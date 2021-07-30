package com.example.demo.security;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserAuthTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JacksonTester<CreateUserRequest> jacksonTester;

    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";


    @Before
    public void setup() throws Exception {


    }

    private CreateUserRequest createUser() {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("test");
        user.setPassword("testpassword");
        user.setConfirmPassword("testpassword");

        return user;
    }


    @Test
    public void sign_userUp_logIn_User_getUser() throws Exception {
        CreateUserRequest user = createUser();
        String authenticatedUser = "{\"username\": \"test\",\"password\" : \"testpassword\"}";

        //Signing up
        mvc.perform(post(new URI("/api/user/create"))
                .content(jacksonTester.write(user).getJson())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{}"))
                .andExpect(status().isOk());


        //Logging user In
        ResultActions resultActions = mvc.perform(post(new URI("/login"))
                .content(authenticatedUser)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        String issueToken = resultActions.andReturn().getResponse().getHeader(HEADER_STRING);
        Assert.assertNotNull(issueToken);

//Getting user
        mvc.perform(
                get(new URI("/api/user/test"))
                        .header(HEADER_STRING, TOKEN_PREFIX + " " + issueToken)
                        .content(authenticatedUser)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());




    }



    @Test
    public void verifying_password_match() throws Exception {

        CreateUserRequest user = createUser();
        user.setPassword("wrongpassword");

        mvc.perform(
                post(new URI("/api/user/create"))
                        .content(jacksonTester.write(user).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void verifying_password_lenght() throws Exception {

        CreateUserRequest user = createUser();
        user.setPassword("short");
        user.setConfirmPassword("short");


        mvc.perform(
                post(new URI("/api/user/create"))
                        .content(jacksonTester.write(user).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());


    }

    //User with No JWT can not add Items to cart
    @Test
    public void cannot_use_api_unless_with_jwt() throws Exception {

        String userWithoutJWT = "{\"username\": \"test\",\"password\" : \"testpassword\"}";

        //Cannot add items to cart
        mvc.perform(
                get(new URI("api/cart/addToCart"))
                        .header(HEADER_STRING, TOKEN_PREFIX)
                        .content(userWithoutJWT)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(403));

    }

    //User with No JWT can not add Items to cart
    @Test
    public void cannot_see_Items_history_without_jwt() throws Exception {

        String userWithoutJWT = "{\"username\": \"test\",\"password\" : \"testpassword\"}";

        //cannot see purchase history
        mvc.perform(
                get(new URI("/api/order/history/test"))
                        .header(HEADER_STRING, TOKEN_PREFIX)
                        .content(userWithoutJWT)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(403));

    }

    //User with No JWT can not add Items to cart
    @Test
    public void cannot_see_cart_without_jwt() throws Exception {

        String userWithoutJWT = "{\"username\": \"test\",\"password\" : \"testpassword\"}";

        //cannot see purchase history
      ResultActions resultActions=  mvc.perform(
                get(new URI("/api/item"))
                        .header(HEADER_STRING, TOKEN_PREFIX)
                        .content(userWithoutJWT)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(403));





    }

}




