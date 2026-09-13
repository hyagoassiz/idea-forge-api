package com.idea_forge.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idea_forge.modules.user.dto.CreateUserRequestDTO;
import com.idea_forge.modules.user.dto.CreateUserResponseDTO;
import com.idea_forge.modules.user.dto.UserResponseDTO;
import com.idea_forge.modules.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("users")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        @PostMapping
        public CreateUserResponseDTO createUser(
                        @Valid @RequestBody CreateUserRequestDTO createUserRequestDTO) {
                return userService.createUser(createUserRequestDTO);
        }

        @GetMapping("/me")
        public ResponseEntity<UserResponseDTO> getAuthenticatedUser() {
                UserResponseDTO response = userService.getAuthenticatedUser();
                return ResponseEntity.ok(response);
        }

}
