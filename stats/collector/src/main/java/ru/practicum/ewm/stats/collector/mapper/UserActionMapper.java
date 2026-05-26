package ru.practicum.ewm.stats.collector.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.messages.UserActionProto;

import java.time.Instant;

@UtilityClass
public class UserActionMapper {
    public static UserActionAvro toUserActionAvro(UserActionProto record) {
        return UserActionAvro.newBuilder()
                .setUserId(record.getUserId())
                .setEventId(record.getEventId())
                .setActionType(switch (record.getActionType()) {
                    case ACTION_VIEW -> ActionTypeAvro.VIEW;
                    case ACTION_REGISTER ->  ActionTypeAvro.REGISTER;
                    case ACTION_LIKE ->  ActionTypeAvro.LIKE;
                    case UNRECOGNIZED ->
                            throw new IllegalArgumentException("Unrecognized action type: " + record.getActionType());
                })
                .setTimestamp(Instant.ofEpochSecond(
                        record.getTimestamp().getSeconds(),
                        record.getTimestamp().getNanos()))
                .build();
    }
}
