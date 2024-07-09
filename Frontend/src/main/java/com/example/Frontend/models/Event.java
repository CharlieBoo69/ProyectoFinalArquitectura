package com.example.Frontend.models;

import java.time.LocalDateTime;

public class Event {
    private String idEvent;
    private String idOwner;
    private String name;
    private String description;
    private String place;
    private String type;
    private double price;
    private LocalDateTime eventTime;
    private LocalDateTime creationTime;
    private String[] guests;
    private String[] notifications;

    // Getters and Setters

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(String idOwner) {
        this.idOwner = idOwner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String[] getGuests() {
        return guests;
    }

    public void setGuests(String[] guests) {
        this.guests = guests;
    }

    public String[] getNotifications() {
        return notifications;
    }

    public void setNotifications(String[] notifications) {
        this.notifications = notifications;
    }
}
