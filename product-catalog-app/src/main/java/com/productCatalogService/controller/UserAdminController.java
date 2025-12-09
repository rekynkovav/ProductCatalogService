package com.productCatalogService.controller;

import com.productCatalogService.dto.UserDTO;
import com.productCatalogService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для административного управления пользователями.
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        List<UserDTO> users = userService.getAllUsersForAdmin(token);
        return ResponseEntity.ok(users);
    }
}