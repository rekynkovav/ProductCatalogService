package com.productCatalogService.service.impl;

import com.productCatalogService.dto.BasketDTO;
import com.productCatalogService.dto.UserDTO;
import com.productCatalogService.entity.Product;
import com.productCatalogService.entity.Role;
import com.productCatalogService.entity.User;
import com.productCatalogService.exception.AccessDeniedException;
import com.productCatalogService.exception.AuthenticationException;
import com.productCatalogService.exception.BadRequestException;
import com.productCatalogService.mapper.BasketMapper;
import com.productCatalogService.mapper.UserMapper;
import com.productCatalogService.repository.UserRepository;
import com.productCatalogService.service.ProductService;
import com.productCatalogService.service.UserService;
import com.productCatalogService.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
    private final ProductService productService;
    private final BasketMapper basketMapper;

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

        // Получаем количество товара из корзины через репозиторий
        Optional<User> userWithBasket = userRepository.findById(user.getId());
        int quantityToReturn = 0;

        if (userWithBasket.isPresent()) {
            User fullUser = userWithBasket.get();
            quantityToReturn = fullUser.getBasket().getOrDefault(productId, 0);
        }

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

    @Override
    public BasketDTO.BasketSummary getBasketSummary(String token) {
        User user = authUtil.getUserByToken(token);
        if (user == null) {
            throw new AuthenticationException("Неавторизованный доступ");
        }

        Map<Long, Product> basket = getUserBasket(user.getId());
        return basketMapper.toSummaryFromProducts(basket);
    }

    @Override
    public Map<Long, String> validateBasket(String token) {
        User user = authUtil.getUserByToken(token);
        if (user == null) {
            throw new AuthenticationException("Неавторизованный доступ");
        }

        Map<Long, Product> basket = getUserBasket(user.getId());
        Map<Long, String> validationResult = new HashMap<>();

        for (Map.Entry<Long, Product> entry : basket.entrySet()) {
            Long productId = entry.getKey();
            Product basketProduct = entry.getValue();

            Optional<Product> currentProduct = productService.findById(productId);

            if (currentProduct.isEmpty()) {
                validationResult.put(productId, "Товар больше не доступен");
            } else if (currentProduct.get().getQuantity() < basketProduct.getQuantity()) {
                validationResult.put(productId,
                        String.format("Доступно только %d единиц товара, в корзине %d",
                                currentProduct.get().getQuantity(), basketProduct.getQuantity()));
            }
        }

        return validationResult;
    }

    @Override
    public Map<String, String> updateBasketItem(String token, Long productId, BasketDTO.UpdateBasketItemRequest request) {
        User user = authUtil.getUserByToken(token);
        if (user == null) {
            throw new AuthenticationException("Неавторизованный доступ");
        }

        int newQuantity = request.getQuantity();
        if (newQuantity < 0) {
            throw new BadRequestException("Количество не может быть отрицательным");
        }

        // Получаем текущую корзину пользователя с товарами
        Map<Long, Product> userBasket = getUserBasket(user.getId());

        // Получаем текущее количество товара в корзине
        int currentQuantity = 0;
        if (userBasket.containsKey(productId)) {
            currentQuantity = userBasket.get(productId).getQuantity();
        }

        // Если товар не в корзине и пытаемся добавить
        if (currentQuantity == 0 && newQuantity > 0) {
            // Проверяем доступность товара
            Optional<Product> product = productService.findById(productId);
            if (product.isEmpty() || product.get().getQuantity() < newQuantity) {
                throw new BadRequestException("Недостаточно товара на складе");
            }

            productService.decreaseQuantity(productId, newQuantity);
            addToBasket(user.getId(), productId, newQuantity);

        } else if (currentQuantity > 0) {
            // Товар уже в корзине, обновляем количество
            int quantityDifference = newQuantity - currentQuantity;

            // Проверяем доступность товара для увеличения количества
            if (quantityDifference > 0) {
                Optional<Product> product = productService.findById(productId);
                if (product.isEmpty() || product.get().getQuantity() < quantityDifference) {
                    throw new BadRequestException("Недостаточно товара на складе");
                }
                productService.decreaseQuantity(productId, quantityDifference);
            } else if (quantityDifference < 0) {
                // Увеличиваем количество на складе (возвращаем товар)
                productService.increaseQuantity(productId, -quantityDifference);
            }

            // Обновляем количество в корзине
            if (newQuantity == 0) {
                userRepository.removeFromBasket(user.getId(), productId);
            } else {
                userRepository.addToBasket(user.getId(), productId, newQuantity);
            }
        }

        return Map.of("message", "Количество товара обновлено");
    }
}
