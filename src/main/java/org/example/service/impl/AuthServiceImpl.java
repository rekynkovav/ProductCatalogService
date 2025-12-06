package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserDTO;
import org.example.exception.AuthenticationException;
import org.example.exception.ConflictException;
import org.example.mapper.UserMapper;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.AuthService;
import org.example.service.UserService;
import org.example.util.AuthUtil;
import org.example.util.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthUtil authUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO.AuthResponse register(UserDTO.RegisterRequest request) {
        log.info("Попытка регистрации пользователя: {}", request.getUserName());

        Optional<User> byUsername = userService.findByUsername(request.getUserName());
        if (byUsername.isEmpty()) {
            throw new ConflictException("Пользователь с таким именем уже существует");
        }

        User user = userMapper.toEntity(request);
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userService.saveUser(user);

        String token = authUtil.generateToken(savedUser.getUserName());
        authUtil.addSession(token, savedUser);

        log.info("Пользователь успешно зарегистрирован: {}", savedUser.getUserName());

        return createAuthResponse(
                "Пользователь успешно зарегистрирован",
                token,
                userMapper.toUserInfo(savedUser)
        );
    }

    @Override
    public UserDTO.AuthResponse login(UserDTO.LoginRequest request) {
        log.info("Попытка входа пользователя: {}", request.getUsername());

        Optional<User> userOptional = userService.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            throw new AuthenticationException("Неверное имя пользователя или пароль");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Неверное имя пользователя или пароль");
        }

        String token = authUtil.generateToken(user.getUserName());
        authUtil.addSession(token, user);

        log.info("Пользователь успешно вошел: {}", user.getUserName());

        return createAuthResponse(
                "Успешный вход в систему",
                token,
                userMapper.toUserInfo(user)
        );
    }

    @Override
    public Map<String, String> logout(String token) {
        log.info("Попытка выхода из системы");

        User removedUser = authUtil.removeSession(token);
        if (removedUser == null) {
            throw new AuthenticationException("Сессия не найдена");
        }

        log.info("Пользователь успешно вышел: {}", removedUser.getUserName());
        return Map.of("message", "Успешный выход из системы");
    }

    @Override
    public Map<String, Boolean> checkUserExists(String username) {
        boolean exists = userService.findByUsername(username).isEmpty();
        log.debug("Проверка существования пользователя {}: {}", username, exists);
        return Map.of("exists", exists);
    }

    @Override
    public UserDTO.UserInfo validateToken(String token) {
        User user = authUtil.getUserByToken(token);
        if (user == null) {
            throw new AuthenticationException("Неверный или просроченный токен");
        }
        return userMapper.toUserInfo(user);
    }

    @Override
    public UserDTO.AuthResponse refreshToken(String oldToken) {
        User user = authUtil.getUserByToken(oldToken);
        if (user == null) {
            throw new AuthenticationException("Неверный или просроченный токен");
        }

        String newToken = authUtil.generateToken(user.getUserName());
        authUtil.removeSession(oldToken);
        authUtil.addSession(newToken, user);

        log.info("Токен обновлен для пользователя: {}", user.getUserName());

        return createAuthResponse(
                "Токен успешно обновлен",
                newToken,
                userMapper.toUserInfo(user)
        );
    }

    private UserDTO.AuthResponse createAuthResponse(String message, String token, UserDTO.UserInfo userInfo) {
        UserDTO.AuthResponse response = new UserDTO.AuthResponse();
        response.setMessage(message);
        response.setToken(token);
        response.setUser(userInfo);
        return response;
    }
}