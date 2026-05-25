package ru.practicum.ewm.user.service;

import ru.practicum.ewm.interaction.dto.user.NewUserRequest;
import ru.practicum.ewm.interaction.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest request);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto getUser(Long userId);

    void deleteUser(Long userId);
}