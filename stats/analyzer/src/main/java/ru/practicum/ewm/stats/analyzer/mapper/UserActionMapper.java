package ru.practicum.ewm.stats.analyzer.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.analyzer.model.UserAction;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Component
public class UserActionMapper {
    private final double viewWeight;
    private final double registerWeight;
    private final double likeWeight;

    public UserActionMapper(
            @Value("${app.action-weight.view:0.4}") double viewWeight,
            @Value("${app.action-weight.register:0.8}") double registerWeight,
            @Value("${app.action-weight.like:1.0}") double likeWeight) {
        this.viewWeight = viewWeight;
        this.registerWeight = registerWeight;
        this.likeWeight = likeWeight;
    }

    public UserAction mapToUserAction(UserActionAvro avro) {
        UserAction userAction = new UserAction();

        userAction.setEventId(avro.getEventId());
        userAction.setUserId(avro.getUserId());
        userAction.setWeight(getWeight(avro.getActionType()));
        userAction.setTimestamp(avro.getTimestamp());

        return userAction;
    }

    private double getWeight(ActionTypeAvro type) {
        return switch (type) {
            case VIEW -> viewWeight;
            case REGISTER -> registerWeight;
            case LIKE -> likeWeight;
        };
    }
}
