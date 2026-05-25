package ru.practicum.ewm.interaction.client.request;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.interaction.error.exception.NotFoundException;

@Configuration
public class RequestClientConfig {

    @Bean
    public ErrorDecoder requestClientErrorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new NotFoundException("Запрос на участие не найден");
            }
            return new ErrorDecoder.Default().decode(methodKey, response);
        };
    }
}