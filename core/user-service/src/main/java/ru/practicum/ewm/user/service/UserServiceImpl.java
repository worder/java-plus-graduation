package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.interaction.dto.user.NewUserRequest;
import ru.practicum.ewm.interaction.dto.user.UserDto;
import ru.practicum.ewm.interaction.error.exception.ConflictException;
import ru.practicum.ewm.interaction.error.exception.NotFoundException;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto createUser(NewUserRequest request) {
        userDao.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new ConflictException("Email already in use");
        });

        User user = UserMapper.mapToUser(request);
        User savedUser = userDao.save(user);
        return UserMapper.mapToDto(savedUser);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userDao.findAll(pageable).getContent();
        } else {
            users = userDao.findAllByIdIn(ids, pageable).getContent();
        }

        return users.stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.mapToDto(userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден")));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден");
        }
        userDao.deleteById(userId);
    }
}