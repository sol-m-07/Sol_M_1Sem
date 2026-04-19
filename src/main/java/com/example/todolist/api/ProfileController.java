package com.example.todolist.api;

import com.example.todolist.dto.ProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> profile(Authentication authentication) {
        return ResponseEntity.ok(new ProfileResponse(authentication.getName(), "Profile is available for USER role"));
    }

    @GetMapping("/docs")
    public ResponseEntity<String> docs() {
        return ResponseEntity.ok("Secure docs endpoint for READ_PRIVILEGE authority");
    }
}
