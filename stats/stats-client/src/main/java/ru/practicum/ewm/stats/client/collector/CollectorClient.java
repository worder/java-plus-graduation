package ru.practicum.ewm.stats.client.collector;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.messages.ActionTypeProto;
import ru.practicum.ewm.stats.messages.UserActionProto;
import ru.practicum.ewm.stats.services.UserActionControllerGrpc;

import java.time.Instant;

@Slf4j
@Component
public class CollectorClient {
    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorClient;

    private void sendUserAction(Long userId, Long eventId, ActionTypeProto actionType) {
        Timestamp timestamp = buildTimestamp();

        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(timestamp)
                .build();

        log.info("Sending user action [{}]: User {} on Event {}. Request: {}", actionType.name(), userId, eventId, request);
        collectorClient.collectUserAction(request);
    }

    private Timestamp buildTimestamp() {
        return Timestamp.newBuilder()
                .setSeconds(Instant.now().getEpochSecond())
                .setNanos(Instant.now().getNano())
                .build();
    }

    public void sendView(Long userId, Long eventId) {
        sendUserAction(userId, eventId, ActionTypeProto.ACTION_VIEW);
    }

    public void sendLike(Long userId, Long eventId) {
        sendUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE);
    }

    public void sendRegistration(Long userId, Long eventId) {
        sendUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER);
    }
}
