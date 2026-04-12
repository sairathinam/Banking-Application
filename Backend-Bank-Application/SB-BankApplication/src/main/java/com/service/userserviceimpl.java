
package com.service;

import com.DTO.LoginRequest;
import com.DTO.LoginResponse;
import com.DTO.RegisterRequest;
import com.Repo.AccountRepository;
import com.Repo.Userrepo;
import com.model.Account;
import com.model.User;
import com.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class userserviceimpl implements Userservice {

    private final Userrepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

   
    public userserviceimpl(Userrepo userRepo,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           AccountRepository accountRepository) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.accountRepository = accountRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(userserviceimpl.class);

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed - email already exists: {}", request.getEmail());
            return "Email already registered";
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setRole("USER");
        userRepo.save(user);

        
        Account account = new Account();
        account.setAccountNumber("ACC" + System.currentTimeMillis());
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        accountRepository.save(account);

        logger.info("User registered and account created: {}", request.getEmail());
        return "User registered successfully";
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setMessage("Login successful");

        return response;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepo.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepo.deleteById(id);
        logger.info("User deleted: id {}", id);
    }
}
