package com.arom.with_travel.domain.notification.service;

import com.arom.with_travel.domain.accompanies.dto.event.AccompanyAppliedEvent;
import com.arom.with_travel.domain.notification.Notification;
import com.arom.with_travel.domain.notification.dto.NotificationResponse;
import com.arom.with_travel.domain.notification.properties.NotificationProperties;
import com.arom.with_travel.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));

        return emitter;
    }

    @Transactional
    public void sendNotification(AccompanyAppliedEvent event) {
        // 알림 메시지 생성
        String notificationMessage = event.getProposerNickName() + NotificationProperties.ACCOMPANY_APPLY_MESSAGE;
        String targetUrl = NotificationProperties.ACCOMPANY_NOTICE_TARGET_URL_PREFIX + event.getAccompanyId();

        // 1️⃣ DB 먼저 저장 (장애 발생 시 유실 방지)
        Notification notification = Notification.from(
                event.getProposerId(),
                event.getOwnerId(),
                notificationMessage,
                targetUrl,
                event.getOccurredAt()
        );
        notificationRepository.save(notification);

        // 2️⃣ 등록된 emitter 들에 push
        List<SseEmitter> userEmitters = emitters.get(event.getOwnerId());
        if (userEmitters != null) {
            List<SseEmitter> deadEmitters = new ArrayList<>();

            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(NotificationResponse.from(notification))); // DTO로 변환 가능
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            }

            // 3️⃣ 실패한 emitter 제거
            userEmitters.removeAll(deadEmitters);
        }
    }

    public Slice<NotificationResponse> getNotifications(Long receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("occurredAt").descending());
        Slice<Notification> notifications = notificationRepository.findByReceiverIdOrderByOccurredAtDesc(receiverId, pageable);
        return notifications.map(NotificationResponse::from);
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
        }
    }
}