package ru.practicum.ewm.stat.dto;

import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class StatEventViewDto {
    private String app;
    private String uri;
    private Long hits;
}
