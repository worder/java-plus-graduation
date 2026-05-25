package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.interaction.client.event.EventClient;
import ru.practicum.ewm.interaction.client.user.UserClient;
import ru.practicum.ewm.interaction.dto.event.EventFullDto;
import ru.practicum.ewm.interaction.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestStatusUpdateRequest;
import ru.practicum.ewm.interaction.dto.request.ParticipationRequestStatusUpdateResultDto;
import ru.practicum.ewm.interaction.dto.user.UserDto;
import ru.practicum.ewm.interaction.enums.EventState;
import ru.practicum.ewm.interaction.enums.ParticipationRequestStatus;
import ru.practicum.ewm.interaction.error.exception.ConflictException;
import ru.practicum.ewm.interaction.error.exception.NotFoundException;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.ParticipationRequestDao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final UserClient userClient;
    private final EventClient eventClient;
    private final ParticipationRequestDao participationRequestDao;

    @Override
    public ParticipationRequestDto createEventParticipationRequest(Long userId, Long eventId) {
        UserDto user = userClient.getUser(userId);
        EventFullDto event;

        try {
            event = eventClient.getEvent(eventId);
        } catch (NotFoundException e) {
            throw new ConflictException("Event not found or not published");
        }

        participationRequestDao.findByEventIdAndRequesterId(eventId, userId).ifPresent(r -> {
            throw new ConflictException("User already sent participation request");
        });

        if (event.getInitiator().getId().equals(user.getId())) {
            throw new ConflictException("User can not participate to it's own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Can not participate, event is not published");
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ConflictException("Participation limit has reached for the event");
        }

        ParticipationRequestStatus status = (event.getRequestModeration()) ?
                ParticipationRequestStatus.PENDING :
                ParticipationRequestStatus.CONFIRMED;

        // according to postman test specification from yap
        if (event.getParticipantLimit() == 0) {
            status = ParticipationRequestStatus.CONFIRMED;
        }

        ParticipationRequest model = new ParticipationRequest();
        model.setRequesterId(userId);
        model.setEventId(eventId);
        model.setStatus(status);
        model.setCreatedOn(LocalDateTime.now());

        ParticipationRequest savedModel = participationRequestDao.save(model);

        if (status == ParticipationRequestStatus.CONFIRMED) {
            UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
            updateRequest.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventClient.updateEvent(event.getId(), updateRequest);
        }

        return ParticipationRequestMapper.mapToParticipationRequestDto(savedModel);
    }

    @Override
    public ParticipationRequestDto cancelEventParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = participationRequestDao.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        if (!request.getRequesterId().equals(userId)) {
            throw new NotFoundException("Invalid user id");
        }

        if (request.getStatus() == ParticipationRequestStatus.CONFIRMED) {
            EventFullDto event = eventClient.getEvent(request.getEventId());
            UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
            updateRequest.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventClient.updateEvent(event.getId(), updateRequest);
        }

        request.setStatus(ParticipationRequestStatus.CANCELED);
        ParticipationRequest savedRequest = participationRequestDao.save(request);
        return ParticipationRequestMapper.mapToParticipationRequestDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getUserParticipationRequests(Long userId) {
        return participationRequestDao.findByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsForUserEvent(Long userId, Long eventId) {
        EventFullDto event = eventClient.getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Invalid user id");
        }

        return participationRequestDao.findByEventId(eventId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestStatusUpdateResultDto updateParticipationRequestsStatus(
            Long userId,
            Long eventId,
            ParticipationRequestStatusUpdateRequest request) {
        EventFullDto event = eventClient.getEvent(eventId);

        int confirmedRequests = participationRequestDao
                .countByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
        int slotsAvailable = event.getParticipantLimit() - confirmedRequests;
        boolean slotsLimited = event.getParticipantLimit() > 0;

        if (slotsLimited && slotsAvailable == 0) {
            throw new ConflictException("Participation requests limit has reached for the event");
        }

        List<ParticipationRequest> approvedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        int confirmations = 0;

        List<ParticipationRequest> requests = participationRequestDao.findByIdIn(request.getRequestIds());
        for (ParticipationRequest r : requests) {
            if (!r.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                throw new ConflictException("Participation request ID: " + r.getId() + " must be in pending status");
            }

            if (request.getStatus()
                    .equals(ParticipationRequestStatusUpdateRequest.ParticipationRequestStatusUpdate.CONFIRMED)
                    && (slotsAvailable > 0 || !slotsLimited)) {
                r.setStatus(ParticipationRequestStatus.CONFIRMED);
                approvedRequests.add(r);
                slotsAvailable--;
                confirmations++;
            } else {
                r.setStatus(ParticipationRequestStatus.REJECTED);
                rejectedRequests.add(r);
            }

            participationRequestDao.save(r);
        }

        if (confirmations > 0) {
            UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
            updateRequest.setConfirmedRequests(event.getConfirmedRequests() + confirmations);
            eventClient.updateEvent(event.getId(), updateRequest);
        }

        return new ParticipationRequestStatusUpdateResultDto(
                approvedRequests.stream().map(ParticipationRequestMapper::mapToParticipationRequestDto).toList(),
                rejectedRequests.stream().map(ParticipationRequestMapper::mapToParticipationRequestDto).toList());
    }

    @Override
    public ParticipationRequestDto getRequesterParticipationRequest(Long requesterId, Long requestId) {
        ParticipationRequest request = participationRequestDao
                .findByIdAndRequesterId(requestId, requesterId)
                .orElseThrow(() -> new NotFoundException("Participation request not found"));

        return ParticipationRequestMapper.mapToParticipationRequestDto(request);
    }
}
