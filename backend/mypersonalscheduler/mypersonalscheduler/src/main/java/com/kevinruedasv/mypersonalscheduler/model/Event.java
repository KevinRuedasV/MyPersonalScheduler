package com.kevinruedasv.mypersonalscheduler.model;

import java.time.LocalDate;

public class Event extends Note {

    private LocalDate eventDate;

    private EventStatus status;

    public Event() {
        super();
        this.status = EventStatus.UPCOMING;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}