package ru.practicum.ewm.main.dao.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsById(Long id);

    void deleteById(Long id);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);
}