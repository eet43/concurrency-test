package com.example.concurrency.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.concurrency.annotation.DistributedLock;
import com.example.concurrency.entity.Event;
import com.example.concurrency.repository.EventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveService {
	private final EventRepository eventRepository;

	@DistributedLock(key = "'event:' + #eventId", waitTime = 3000L, leaseTime = 5000L)
	@Transactional
	public Event reserveTicketWithDistributedLock(Long eventId) {
		log.info("이벤트 {}번에 대한 티켓 예약을 시도합니다 (분산락 사용)", eventId);

		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다"));

		event.reserveTicket();
		Event savedEvent = eventRepository.saveAndFlush(event);

		log.info("이벤트 {}번 티켓 예약 완료, 남은 티켓: {}장",
			eventId, savedEvent.getAvailableTickets());

		return savedEvent;
	}

	@Transactional
	public Event reserveTicketWithPessimisticLock(Long eventId) {
		log.info("이벤트 {}번에 대한 티켓 예약을 시도합니다 (비관적 락 사용)", eventId);

		Event event = eventRepository.findByIdWithNativeLock(eventId)
			.orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다"));

		event.reserveTicket();
		Event savedEvent = eventRepository.save(event);

		log.info("이벤트 {}번 티켓 예약 완료 (비관적 락), 남은 티켓: {}장",
			eventId, savedEvent.getAvailableTickets());

		return savedEvent;
	}

	@Transactional
	public Event reserveTicketWithoutLock(Long eventId) {
		log.info("이벤트 {}번에 대한 티켓 예약을 시도합니다 (락 없음)", eventId);

		Event event = eventRepository.findById(eventId)
			.orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다"));

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		event.reserveTicket();
		Event savedEvent = eventRepository.save(event);

		log.info("이벤트 {}번 티켓 예약 완료 (락 없음), 남은 티켓: {}장",
			eventId, savedEvent.getAvailableTickets());

		return savedEvent;
	}
}
