package com.example.concurrency.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrency.entity.Event;
import com.example.concurrency.service.ReserveService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events/reserve")
@RequiredArgsConstructor
public class ReserveController {

	private final ReserveService reserveService;

	@PostMapping("/{eventId}/distributed-lock")
	public ResponseEntity<Event> reserveTicketWithDistributedLock(@PathVariable Long eventId) {
		try {
			Event event = reserveService.reserveTicketWithDistributedLock(eventId);
			return ResponseEntity.ok(event);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(423).build();
		}
	}

	@PostMapping("/{eventId}/pessimistic-lock")
	public ResponseEntity<Event> reserveTicketWithPessimisticLock(@PathVariable Long eventId) {
		try {
			Event event = reserveService.reserveTicketWithPessimisticLock(eventId);
			return ResponseEntity.ok(event);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/{eventId}/no-lock")
	public ResponseEntity<Event> reserveTicketWithoutLock(@PathVariable Long eventId) {
		try {
			Event event = reserveService.reserveTicketWithoutLock(eventId);
			return ResponseEntity.ok(event);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
