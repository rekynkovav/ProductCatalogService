package com.productCatalogService.mapper;

import com.productCatalogService.dto.UserDTO;
import com.productCatalogService.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "basket", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateFromDTO(UserDTO.UpdateUserRequest request, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "basket", ignore = true)
    User toEntityFromAuth(UserDTO userDTO);

    List<UserDTO> toDTOList(List<User> users);

    @Mapping(target = "userName", source = "userName")
    UserDTO.UserInfo toUserInfo(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "basket", expression = "java(new java.util.HashMap<>())")
    User toEntity(UserDTO.RegisterRequest request);
}