package com.toursignuplogin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.toursignuplogin.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public User register(User user) throws Exception {
        // Validate user input
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }

        // Check if user with the same email or username already exists
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new Exception("Email already in use");
        }

        existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new Exception("Username already in use");
        }

        // Save user to database
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setConfirmPassword(user.getPassword());
        user = userRepository.save(user);

        // Send OTP to user's email
        String otp = generateOTP();
        emailService.sendOTP(user.getEmail(), otp);

        return user;
    }

    public User login(String username, String password) throws Exception {
        // Check if user with the given username exists
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception("Invalid username or password");
        }

        // Verify password
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new Exception("Invalid username or password");
        }

        return user;
    }

    private String generateOTP() {
        // Generate and return OTP
    }
}