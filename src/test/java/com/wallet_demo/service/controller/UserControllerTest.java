package com.wallet_demo.service.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet_demo.entity.User;
import com.wallet_demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }


    @Test
    void testRegisterUser() throws Exception {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("securepassword");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testLogin() throws Exception {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("securepassword");
        userRepository.save(user);

        mockMvc.perform(post("/api/users/login")
                        .param("email", "john@example.com")
                        .param("password", "securepassword"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful. Welcome, John Doe!"));
    }

    @Test
    void testTransferMoney() throws Exception {
        User sender = new User();
        sender.setName("Alice");
        sender.setEmail("alice@example.com");
        sender.setPassword("password");
        sender.setWalletBalance(new BigDecimal("100.00"));
        userRepository.save(sender);

        User receiver = new User();
        receiver.setName("Bob");
        receiver.setEmail("bob@example.com");
        receiver.setPassword("password");
        receiver.setWalletBalance(new BigDecimal("50.00"));
        userRepository.save(receiver);

        mockMvc.perform(post("/api/users/transfer")
                        .param("senderId", sender.getId().toString())
                        .param("receiverId", receiver.getId().toString())
                        .param("amount", "25.00"))
                .andExpect(status().isOk())
                .andExpect(content().string("Money transferred successfully from user " + sender.getId() + " to user " + receiver.getId()));

        User updatedSender = userRepository.findById(sender.getId()).get();
        User updatedReceiver = userRepository.findById(receiver.getId()).get();

        assert updatedSender.getWalletBalance().compareTo(new BigDecimal("75.00")) == 0;
        assert updatedReceiver.getWalletBalance().compareTo(new BigDecimal("75.00")) == 0;
    }
}

