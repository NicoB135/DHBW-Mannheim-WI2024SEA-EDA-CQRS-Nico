package de.smarthome.event;

import java.time.LocalTime;

public class PersonLeftEvent {

    private String person;
    private LocalTime time;

    // Getter und Setter
    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    // Konstruktoren
    public PersonLeftEvent() {
    }

    public PersonLeftEvent(String person) {
        this.person = person;
        this.time = LocalTime.now();
    }
}
