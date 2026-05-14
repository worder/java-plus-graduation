package ru.practicum.ewm.main.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.User;

public interface UserDatabaseDao extends UserDao, JpaRepository<User, Long> {
}