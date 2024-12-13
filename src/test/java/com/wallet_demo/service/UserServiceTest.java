package com.wallet_demo.service;
import com.wallet_demo.entity.User;
import com.wallet_demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        User user = new User();
        user.setEmail("john@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testTransferMoney_InsufficientBalance() {
        User sender = new User();
        sender.setWalletBalance(new BigDecimal("10.00"));
        User receiver = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        assertThrows(IllegalArgumentException.class, () -> userService.sendMoney(1L, 2L, new BigDecimal("50.00")));
    }
}

