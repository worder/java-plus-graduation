package ru.practicum.infra.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public ResponseEntity<Map<String, Object>> fallback(ServerHttpRequest request) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service temporarily unavailable");
        response.put("path", request.getURI().getPath());
        response.put("timestamp", Instant.now().toString());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}