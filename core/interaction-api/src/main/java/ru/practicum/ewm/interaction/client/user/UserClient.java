package ru.practicum.ewm.interaction.client.user;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.interaction.dto.user.UserDto;

import java.util.Collection;

@FeignClient(
        name = "user-service",
        path="/admin/users",
        configuration = UserClientConfig.class)
public interface UserClient {
    @GetMapping("")
    Collection<UserDto> getUsers(
            @RequestParam(required = false) Collection<Long> ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size);

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable Long userId);
}
