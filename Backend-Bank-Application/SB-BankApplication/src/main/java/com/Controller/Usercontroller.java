package com.Controller;

import com.DTO.LoginRequest;
import com.DTO.LoginResponse;
import com.DTO.RegisterRequest;
import com.service.Userservice;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class Usercontroller {

    private final Userservice userService;

    public Usercontroller(Userservice userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String result = userService.register(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }
   
}                                                                          