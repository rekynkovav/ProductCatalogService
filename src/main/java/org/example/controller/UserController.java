package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.BasketDTO;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * Контроллер для работы с пользовательскими данными.
 * Предоставляет REST API для управления профилем пользователя и корзиной покупок.
 * Все операции требуют наличия валидного токена авторизации в заголовке запроса.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Получает профиль текущего авторизованного пользователя.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @return ResponseEntity с информацией о профиле пользователя.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDTO.UserInfo> getCurrentUserProfile(@RequestHeader("Authorization") String token) {
        UserDTO.UserInfo userInfo = userService.getCurrentUserProfile(token);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Получает корзину покупок текущего пользователя.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @return ResponseEntity с DTO корзины покупок.
     */
    @GetMapping("/basket")
    public ResponseEntity<BasketDTO> getUserBasket(@RequestHeader("Authorization") String token) {
        BasketDTO basketDTO = userService.getUserBasketDto(token);
        return ResponseEntity.ok(basketDTO);
    }

    /**
     * Добавляет товар в корзину пользователя.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @param productId Идентификатор товара для добавления в корзину.
     * @param request DTO с количеством добавляемого товара.
     * @return ResponseEntity с сообщением о результате операции.
     */
    @PostMapping("/basket/add/{productId}")
    public ResponseEntity<Map<String, String>> addToBasket(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId,
            @Valid @RequestBody BasketDTO.AddToBasketRequest request) {

        Map<String, String> response = userService.addToBasketDto(token, productId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Удаляет товар из корзины пользователя.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @param productId Идентификатор товара для удаления из корзины.
     * @return ResponseEntity с информацией о результате операции.
     */
    @DeleteMapping("/basket/remove/{productId}")
    public ResponseEntity<Map<String, Object>> removeFromBasket(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId) {

        Map<String, Object> response = userService.removeFromBasketDto(token, productId);
        return ResponseEntity.ok(response);
    }

    /**
     * Очищает всю корзину пользователя.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @return ResponseEntity с сообщением о результате операции.
     */
    @DeleteMapping("/basket/clear")
    public ResponseEntity<Map<String, String>> clearUserBasket(@RequestHeader("Authorization") String token) {
        Map<String, String> response = userService.clearUserBasketDto(token);
        return ResponseEntity.ok(response);
    }
}