package lk.klass.mcp.controller;

import lk.klass.mcp.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        return userDetailsService.findByUsername(username)
                .filter(userDetails -> passwordEncoder.matches(password, userDetails.getPassword()))
                .map(userDetails -> ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(username))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(401).body(Collections.emptyMap())));
    }

    @GetMapping("/ping")
    public Mono<String> ping() {
        return Mono.just("pong");
    }
}
