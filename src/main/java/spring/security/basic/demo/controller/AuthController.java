package spring.security.basic.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import spring.security.basic.demo.entity.UserEntity;
import spring.security.basic.demo.security.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserEntity userEntity) {
       try{
           // authenticate user
           Authentication authentication = authenticationManager
                   .authenticate(new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword()));
           UserDetails userDetails =  (UserDetails) authentication.getPrincipal();

           String token = jwtUtil.generateToken(userDetails);
           return ResponseEntity.ok(Map.of("token",token));
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid credentials"));
       }
    }
}
