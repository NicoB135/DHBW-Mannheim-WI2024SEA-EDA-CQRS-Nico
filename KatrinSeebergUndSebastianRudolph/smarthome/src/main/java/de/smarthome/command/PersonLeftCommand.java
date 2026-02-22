package de.smarthome.command;

public class PersonLeftCommand {

    private String person;

    public PersonLeftCommand(String person) {
        this.person = person;
    }

    public String getPerson() {
        return person;
    }
}

