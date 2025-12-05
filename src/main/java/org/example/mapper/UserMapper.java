package org.example.mapper;

import org.example.dto.UserDTO;
import org.example.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashMap;
import java.util.List;

/**
 * Интерфейс маппера для преобразования между сущностью User и DTO объектами.
 * Содержит как автоматически генерируемые методы, так и пользовательские реализации.
 *
 * @see User
 * @see UserDTO
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразует сущность User в UserDTO.
     *
     * @param user исходная сущность пользователя
     * @return DTO объект пользователя
     */
    UserDTO toDTO(User user);

    /**
     * Преобразует UserDTO в сущность User.
     * Игнорирует поле mapBasket при преобразовании.
     *
     * @param userDTO DTO объект пользователя
     * @return сущность пользователя без корзины
     */
    @Mapping(target = "mapBasket", ignore = true)
    User toEntity(UserDTO userDTO);

    /**
     * Преобразует DTO пользователя в сущность для целей аутентификации.
     * Игнорирует поле id и mapBasket.
     *
     * @param userDTO DTO объект пользователя
     * @return сущность пользователя с базовыми данными аутентификации
     * @apiNote Используется при регистрации и аутентификации
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mapBasket", ignore = true)
    default User toEntityFromAuth(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        return user;
    }

    /**
     * Создает сущность User из DTO с инициализацией корзины.
     *
     * @param userDTO DTO объект пользователя
     * @return сущность пользователя с пустой корзиной
     * @apiNote Используется при создании нового пользователя
     */
    default User createUserFromDTO(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setMapBasket(new HashMap<>());
        return user;
    }

    /**
     * Преобразует список сущностей User в список UserDTO.
     *
     * @param users список сущностей пользователей
     * @return список DTO объектов пользователей
     */
    List<UserDTO> toDTOList(List<User> users);

    /**
     * Преобразует сущность User в упрощенный объект UserInfo.
     *
     * @param user исходная сущность пользователя
     * @return объект с информацией о пользователе (без пароля и корзины)
     * @apiNote Используется в ответах API, где не нужна полная информация
     */
    @Mapping(target = "username", source = "userName")
    UserDTO.UserInfo toUserInfo(User user);
}