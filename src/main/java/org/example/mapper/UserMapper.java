package org.example.mapper;

import org.example.dto.UserDTO;
import org.example.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashMap;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    @Mapping(target = "mapBasket", ignore = true)
    User toEntity(UserDTO userDTO);

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

    List<UserDTO> toDTOList(List<User> users);

    @Mapping(target = "username", source = "userName")
    UserDTO.UserInfo toUserInfo(User user);
}