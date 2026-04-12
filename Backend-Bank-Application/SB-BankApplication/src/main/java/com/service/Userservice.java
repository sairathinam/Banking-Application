package com.service;

import com.DTO.LoginRequest;
import com.DTO.LoginResponse;
import com.DTO.RegisterRequest;
import com.model.User;
import java.util.Optional;

public interface Userservice {

   
    String register(RegisterRequest request);   
  
    Optional<User> getUserByEmail(String email);
    void delete(Long id);
    
    LoginResponse login(LoginRequest request);
    
    
}