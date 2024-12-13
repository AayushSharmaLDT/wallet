package com.wallet_demo.controller;
import com.wallet_demo.entity.User;
import com.wallet_demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) throws Exception {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/{id}/wallet/add")
    public ResponseEntity<String> addMoneyToWallet(@PathVariable Long id, @RequestParam BigDecimal amount) {
        userService.updateWalletBalance(id, amount);
        return ResponseEntity.ok("Wallet balance updated successfully");
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> sendMoney(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam BigDecimal amount) {
        try {
            userService.sendMoney(senderId, receiverId, amount);
            return ResponseEntity.ok("Money transferred successfully from user " + senderId + " to user " + receiverId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        try {
            String message = userService.login(email, password);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

