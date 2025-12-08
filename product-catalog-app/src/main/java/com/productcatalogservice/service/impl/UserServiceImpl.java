package com.productcatalogservice.service.impl;

import com.productcatalogservice.dto.BasketDTO;
import com.productcatalogservice.dto.UserDTO;
import com.productcatalogservice.entity.Product;
import com.productcatalogservice.entity.Role;
import com.productcatalogservice.entity.User;
import com.productcatalogservice.exception.AccessDeniedException;
import com.productcatalogservice.exception.AuthenticationException;
import com.productcatalogservice.exception.BadRequestException;
import com.productcatalogservice.mapper.BasketMapper;
import com.productcatalogservice.mapper.UserMapper;
import com.productcatalogservice.repository.UserRepository;
import com.productcatalogservice.service.ProductService;
import com.productcatalogservice.service.UserService;
import com.productcatalogservice.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final UserMapper userMapper;
    private final BasketMapper basketMapper;
    private final ProductService productService;

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

    @Override
    public UserDTO.UserInfo getCurrentUserProfile(String token) {
        User user = authUtil.getUserByToken(token);
        if (user == null) {
            throw new AuthenticationException("Неавторизованный доступ");
        }
        return userMapper.toUserInfo(user);
    }

    @Override
    public BasketDTO getUserBasketDto(String token) {
        User user = authUtil.getUserByToken(token);
        if (user == null) {
            throw new AuthenticationException("Неавторизованный доступ");
        }

        Map<Long, Product> basket = getUserBasket(user.getId());
        return basketMapper.toBasketDTO(basket);
    }

    @Override
    public Map<String, String> addToBasketDto(String token, Long productId, BasketDTO.AddToBasketRequest request) {
        User user = authUtil.getUserByToken(token);
        if (user == null) {
            throw new AuthenticationException("Неавторизованный доступ");
        }

        int quantity = request.getQuantity();
        if (quantity <= 0) {
            throw new BadRequestException("Количество должно быть положительным");
        }

        Optional<Product> product = productService.findById(productId);
        if (product.isEmpty() || product.get().getQuantity() < quantity) {
            throw new BadRequestException("Недостаточно товара на складе");
        }

        productService.decreaseQuantity(productId, quantity);
        addToBasket(user.getId(), productId, quantity);

        return Map.of("message", "Товар добавлен в корзину");
    }

    @Override
    public Map<String, Object> removeFromBasketDto(String token, Long productId) {
        User user = authUtil.getUserByToken(token);
        if (user == null) {
            throw new AuthenticationException("Неавторизованный доступ");
        }

        Map<Long, Product> userBasket = getUserBasket(user.getId());
        if (!userBasket.containsKey(productId)) {
            throw new BadRequestException("Товар не найден в корзине");
        }

        Product basketProduct = userBasket.get(productId);
        int quantityToReturn = basketProduct.getQuantity();

        productService.increaseQuantity(productId, quantityToReturn);
        removeFromBasket(user.getId(), productId);

        return Map.of(
                "message", "Товар удален из корзины",
                "quantity_returned", quantityToReturn
        );
    }

    @Override
    public Map<String, String> clearUserBasketDto(String token) {
        User user = authUtil.getUserByToken(token);
        if (user == null) {
            throw new AuthenticationException("Неавторизованный доступ");
        }

        clearUserBasket(user.getId());
        return Map.of("message", "Корзина успешно очищена");
    }

    @Override
    public List<UserDTO> getAllUsersForAdmin(String token) {
        User adminUser = authUtil.getUserByToken(token);
        if (adminUser == null || !Role.ADMIN.equals(adminUser.getRole())) {
            throw new AccessDeniedException("Доступ запрещен. Требуется роль ADMIN");
        }

        List<User> users = showAllUser();
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}
