package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.Product;
import org.example.model.entity.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean isContainsUser(String userName) {
        return userRepository.existsByUsername(userName);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Map<Long, Product> getUserBasket(Long userId) {
        return userRepository.getBasket(userId);
    }

    @Override
    public List<User> showAllUser() {
        return userRepository.findAllUser();
    }

    @Override
    public void clearUserBasket(Long userId) {
        userRepository.clearBasket(userId);
    }

    @Override
    public void addToBasket(Long userId, Long productId, int quantity) {
        userRepository.addToBasket(userId, productId, quantity);
    }

    @Override
    public void removeFromBasket(Long userId, Long productId) {
        userRepository.removeFromBasket(userId, productId);
    }
}
