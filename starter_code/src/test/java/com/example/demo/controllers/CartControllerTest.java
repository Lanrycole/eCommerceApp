package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);


    @BeforeEach
    public void setUp() {
        cartController = new CartController();


        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        User user = createUser();
        Cart cart = new Cart();
        Item item = createItems();
        user.setCart(cart);
        when(userRepo.findByUsername("test")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(java.util.Optional.of(item));
    }


    @Test
    public void testAddToCart() {

        ModifyCartRequest modifyCartRequest = modifyCartRequest();

        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);
        User byUsername = userRepo.findByUsername(modifyCartRequest.getUsername());
        assertEquals("test", byUsername.getUsername());


        assertNotNull(cartResponse);
        assertEquals(200, cartResponse.getStatusCodeValue());

        Cart cart = cartResponse.getBody();
        assertNotNull(cart);
        Optional<Item> findItem = itemRepo.findById(1L);
        BigDecimal cartTotal = findItem.get().getPrice();
        BigDecimal itemTotal = BigDecimal.valueOf(modifyCartRequest.getQuantity()).multiply(cartTotal);
        assertEquals(itemTotal, cart.getTotal());

    }


    @Test
    public void removeFromCart() {

        ModifyCartRequest modifyCartRequest = modifyCartRequest();
        assertEquals(1, modifyCartRequest.getQuantity());

        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);
        System.out.println("Cart Response" + cartResponse.getBody().getItems());
        assertEquals(200, cartResponse.getStatusCodeValue());
        assertNotNull(cartResponse.getBody());

        ResponseEntity<Cart> removeCartResponse = cartController.removeFromcart(modifyCartRequest);

        assertEquals(200, removeCartResponse.getStatusCodeValue());
        assertEquals(0, Objects.requireNonNull(removeCartResponse.getBody()).getItems().toArray().length);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testpasswd");
        return user;
    }

    private Item createItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Chevy");
        item.setPrice(BigDecimal.valueOf(4.00));
        item.setDescription("1998 Chevy Camaro");
        return item;

    }

    private ModifyCartRequest modifyCartRequest() {

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("test");
        return modifyCartRequest;
    }
}
