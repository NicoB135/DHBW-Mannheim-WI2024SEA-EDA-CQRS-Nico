package de.smarthome.command;

public class PersonArrivedCommand {

    private String person;

    public PersonArrivedCommand(String person) {
        this.person = person;
    }

    public String getPerson() {
        return person;
    }
}
