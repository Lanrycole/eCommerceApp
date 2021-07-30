package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderControllerTest {


    private UserController userController;

    private OrderController orderController;
    private OrderRepository orderRepo = mock(OrderRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);

    @Before
    public void Setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        TestUtils.injectObjects(orderController, "userRepository", userRepo);

        User user = new User();
        user.setUsername("test");
        user.setPassword("testpassword");
        user.setId(1L);
        when(userRepo.findByUsername("test")).thenReturn(user);

        Cart cart = createCart();
        cart.setUser(user);
        user.setCart(cart);


    }

    @Test
    public void submitOrderForUser() {
        ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder user = response.getBody();

        assertEquals("test", user.getUser().getUsername());

    }


    @Test
    public void getOrderHistoryForUser() {
        ResponseEntity<UserOrder> submittedOrder = orderController.submit("test");
        assertNotNull(submittedOrder);

        UserOrder user = submittedOrder.getBody();
        assertEquals("test", user.getUser().getUsername());

        ResponseEntity<List<UserOrder>> test = orderController.getOrdersForUser("test");
        assertEquals(200, test.getStatusCodeValue());


    }


    private List<Item> createItems() {
        List<Item> itemList = new ArrayList<Item>();
        itemList.add(new Item(1L, "Pencil", new BigDecimal("4.00"), "HD Pencil"));
        return itemList;
    }

    private static CreateUserRequest createUserRequest() {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("test");
        user.setPassword("testpasswd");
        user.setConfirmPassword("testpasswd");
        return user;
    }

    private Cart createCart() {
        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = createItems();
        items.forEach(cart::addItem);
        return cart;
    }


}