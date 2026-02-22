package de.smarthome.event;

import java.time.LocalTime;

public class PersonArrivedEvent {

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
    public PersonArrivedEvent() {
    }

    public PersonArrivedEvent(String person) {
        this.person = person;
        this.time = LocalTime.now();
    }
}
