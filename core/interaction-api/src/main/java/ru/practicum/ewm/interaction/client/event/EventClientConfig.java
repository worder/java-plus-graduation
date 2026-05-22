package ru.practicum.ewm.interaction.client.event;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.interaction.error.exception.NotFoundException;

@Configuration
public class EventClientConfig {

    @Bean
    public ErrorDecoder eventClientErrorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new NotFoundException("Событие не найдено");
            }
            return new ErrorDecoder.Default().decode(methodKey, response);
        };
    }
}