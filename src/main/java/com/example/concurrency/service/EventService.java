package com.example.concurrency.service;

import com.example.concurrency.annotation.DistributedLock;
import com.example.concurrency.entity.Event;
import com.example.concurrency.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    
    private final EventRepository eventRepository;
    
    @Transactional
    public Event createEvent(String name, Integer totalTickets, LocalDateTime eventDate) {
        Event event = new Event(name, totalTickets, eventDate);
        return eventRepository.save(event);
    }
    
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다"));
    }
}