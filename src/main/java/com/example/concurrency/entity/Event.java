package com.example.concurrency.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Integer totalTickets;
    
    @Column(nullable = false)
    private Integer availableTickets;
    
    @Column(nullable = false)
    private LocalDateTime eventDate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public Event(String name, Integer totalTickets, LocalDateTime eventDate) {
        this.name = name;
        this.totalTickets = totalTickets;
        this.availableTickets = totalTickets;
        this.eventDate = eventDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void reserveTicket() {
        if (this.availableTickets <= 0) {
            throw new IllegalStateException("예약 가능한 티켓이 없습니다");
        }
        this.availableTickets--;
        this.updatedAt = LocalDateTime.now();
    }
}