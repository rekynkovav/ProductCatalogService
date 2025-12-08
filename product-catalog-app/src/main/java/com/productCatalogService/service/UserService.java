package com.productCatalogService.service;


import com.productCatalogService.dto.BasketDTO;

import com.productCatalogService.dto.UserDTO;

import com.productCatalogService.entity.Product;

import com.productCatalogService.entity.User;


import java.util.List;

import java.util.Map;

import java.util.Optional;


/**
 * Сервисный интерфейс для управления пользователями.
 * <p>
 * Определяет контракт для работы с пользователями, их корзинами покупок и профилями.
 *
 *
 *
 * <p>Сервис предоставляет полный набор операций для управления пользовательскими данными:</p>
 *
 * <ul>
 *
 *   <li>Регистрация и аутентификация пользователей</li>
 *
 *   <li>Управление профилями пользователей</li>
 *
 *   <li>Работа с корзинами покупок</li>
 *
 *   <li>Административные функции</li>
 *
 *   <li>Преобразование данных между слоями приложения</li>
 *
 * </ul>
 *
 *
 *
 * <p>Интерфейс разделяет методы на две категории:</p>
 *
 * <ol>
 *
 *   <li>Методы для работы с доменными объектами ({@link User}, {@link Product})</li>
 *
 *   <li>Методы для работы с DTO ({@link UserDTO}, {@link BasketDTO})</li>
 *
 * </ol>
 *
 * @see User
 * @see Product
 * @see UserDTO
 * @see BasketDTO
 * @see com.productCatalogService.service.impl.UserServiceImpl
 * @since 1.0
 */

public interface UserService {

    /**
     * Сохраняет пользователя в системе.
     * <p>
     * Выполняет валидацию данных пользователя и может генерировать уникальный идентификатор.
     *
     * @param user объект пользователя для сохранения (не может быть null)
     * @return сохраненный пользователь с присвоенным идентификатором
     * @throws IllegalArgumentException                    если user равен null
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    User saveUser(User user);

    /**
     * Проверяет существование пользователя с указанным именем.
     * <p>
     * Используется для предотвращения дублирования имен пользователей при регистрации.
     *
     * @param userName имя пользователя для проверки
     * @return true если пользователь существует, false в противном случае
     * @throws IllegalArgumentException если userName равен null или пуст
     */
    boolean isContainsUser(String userName);

    /**
     * Находит пользователя по имени пользователя.
     * <p>
     * Используется для аутентификации и получения информации о пользователе.
     *
     * @param username имя пользователя для поиска
     * @return Optional с найденным пользователем или пустой Optional если пользователь не найден
     * @throws IllegalArgumentException если username равен null или пуст
     */
    Optional<User> findByUsername(String username);

    /**
     * Получает корзину покупок пользователя.
     * <p>
     * Возвращает ассоциативный массив товаров в корзине, где ключ - ID товара.
     *
     * @param userId идентификатор пользователя
     * @return Map товаров в корзине пользователя, где ключ - ID товара, значение - объект товара
     * @throws IllegalArgumentException если userId равен null
     */
    Map<Long, Product> getUserBasket(Long userId);

    /**
     * Возвращает список всех пользователей системы.
     * <p>
     * Используется для административных целей и отчетности.
     *
     * @return список всех пользователей; если пользователей нет, возвращается пустой список
     * @throws org.springframework.dao.DataAccessException при ошибках доступа к данным
     */
    List<User> showAllUser();

    /**
     * Очищает всю корзину пользователя.
     * <p>
     * Удаляет все товары из корзины указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @throws IllegalArgumentException если userId равен null
     */
    void clearUserBasket(Long userId);

    /**
     * Добавляет товар в корзину пользователя.
     * <p>
     * Выполняет проверку наличия товара и доступного количества.
     *
     * @param userId    идентификатор пользователя
     * @param productId идентификатор товара
     * @param quantity  количество товара для добавления (должно быть положительным)
     * @throws IllegalArgumentException если параметры некорректны
     * @throws IllegalStateException    если товар недоступен или недостаточно на складе
     */
    void addToBasket(Long userId, Long productId, int quantity);

    /**
     * Удаляет товар из корзины пользователя.
     *
     * @param userId    идентификатор пользователя
     * @param productId идентификатор товара
     * @throws IllegalArgumentException если параметры некорректны
     */
    void removeFromBasket(Long userId, Long productId);

    /**
     * Получает профиль текущего авторизованного пользователя.
     *
     * @param token токен авторизации пользователя
     * @return DTO с информацией о профиле пользователя
     */

    UserDTO.UserInfo getCurrentUserProfile(String token);

    /**
     * Получает корзину пользователя в формате DTO.
     *
     * @param token токен авторизации пользователя
     * @return DTO корзины пользователя
     */

    BasketDTO getUserBasketDto(String token);

    /**
     * Добавляет товар в корзину пользователя (версия с DTO).
     *
     * @param token     токен авторизации пользователя
     * @param productId идентификатор товара
     * @param request   DTO с данными для добавления в корзину
     * @return результат операции в формате ключ-значение
     * @throws IllegalArgumentException если параметры некорректны
     */
    Map<String, String> addToBasketDto(String token, Long productId, BasketDTO.AddToBasketRequest request);

    /**
     * Удаляет товар из корзины пользователя (версия с DTO).
     *
     * @param token     токен авторизации пользователя
     * @param productId идентификатор товара
     * @return результат операции в формате ключ-значение
     */
    Map<String, Object> removeFromBasketDto(String token, Long productId);

    /**
     * Очищает корзину пользователя (версия с DTO).
     *
     * @param token токен авторизации пользователя
     * @return результат операции в формате ключ-значение
     */
    Map<String, String> clearUserBasketDto(String token);

    /**
     * Получает список всех пользователей для администратора.
     * <p>
     * Включает дополнительную информацию, недоступную обычным пользователям.
     *
     * @param token токен авторизации администратора
     * @return список DTO пользователей
     */
    List<UserDTO> getAllUsersForAdmin(String token);

    /**
     * Получает сводную информацию о корзине пользователя.
     *
     * @param token токен авторизации пользователя
     * @return DTO с сводной информацией о корзине
     */
    BasketDTO.BasketSummary getBasketSummary(String token);

    /**
     * Валидирует корзину пользователя, проверяя доступность товаров.
     *
     * @param token токен авторизации пользователя
     * @return Map с информацией о проблемах валидации (ключ - ID товара, значение - описание проблемы)
     */
    Map<Long, String> validateBasket(String token);

    /**
     * Обновляет количество товара в корзине пользователя.
     *
     * @param token     токен авторизации пользователя
     * @param productId идентификатор товара
     * @param request   DTO с данными для обновления
     * @return результат операции
     */
    Map<String, String> updateBasketItem(String token, Long productId, BasketDTO.UpdateBasketItemRequest request);

}