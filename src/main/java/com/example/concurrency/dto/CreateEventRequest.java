package com.example.concurrency.dto;

import java.time.LocalDateTime;

public record CreateEventRequest(
	String name,
	Integer totalTickets,
	LocalDateTime eventDate
) {}
