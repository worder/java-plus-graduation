package ru.practicum.ewm.main.service.user;

import ru.practicum.ewm.main.dto.user.NewUserRequest;
import ru.practicum.ewm.main.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest request);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);
}