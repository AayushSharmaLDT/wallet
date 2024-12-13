package com.wallet_demo.service;
import com.wallet_demo.entity.User;
import com.wallet_demo.repository.UserRepository;
import com.wallet_demo.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User registerUser(User user) throws Exception {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        String encryptedPassword = EncryptionUtil.encrypt(user.getPassword());
        user.setPassword(encryptedPassword);

        user.setWalletBalance(BigDecimal.ZERO);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void updateWalletBalance(Long userId, BigDecimal amount) {
        User user = getUserById(userId);
        user.setWalletBalance(user.getWalletBalance().add(amount));
        userRepository.save(user);
    }

    @Transactional
    public void sendMoney(Long senderId, Long receiverId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender with ID " + senderId + " does not exist"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver with ID " + receiverId + " does not exist"));


        if (sender.getWalletBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Sender has insufficient balance for this transaction");
        }


        sender.setWalletBalance(sender.getWalletBalance().subtract(amount));
        userRepository.save(sender);

        receiver.setWalletBalance(receiver.getWalletBalance().add(amount));
        userRepository.save(receiver);
    }

    //Login Api for testing saved credentials
    public String login(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String decryptedPassword = EncryptionUtil.decrypt(user.getPassword());
        if (!decryptedPassword.equals(password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return "Login successful. Welcome, " + user.getName() + "!";
    }

}
