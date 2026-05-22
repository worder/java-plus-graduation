package ru.practicum.ewm.interaction.client.user;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.interaction.error.exception.NotFoundException;

@Configuration
public class UserClientConfig {

    @Bean
    public ErrorDecoder userClientErrorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new NotFoundException("Пользователь не найден");
            }
            return new ErrorDecoder.Default().decode(methodKey, response);
        };
    }
}