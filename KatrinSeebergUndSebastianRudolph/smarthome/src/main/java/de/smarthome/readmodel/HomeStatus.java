package de.smarthome.readmodel;

import java.util.ArrayList;
import java.util.List;

public class HomeStatus {
    private List<String> presentPersons = new ArrayList<>();
    private boolean lightsOn = false;
    private int temperature = 16;

    public List<String> getPresentPersons() { return presentPersons; }
    public boolean isLightsOn() { return lightsOn; }
    public int getTemperature() { return temperature; }

    public void setLightsOn(boolean lightsOn) { this.lightsOn = lightsOn; }
    public void setTemperature(int temperature) { this.temperature = temperature; }

    @Override
    public String toString() {
        return "Anwesend: " + (presentPersons.isEmpty() ? "niemand" : presentPersons) +
               ", Licht: " + (lightsOn ? "an" : "aus") +
               ", Temperatur: " + temperature + "°C";
    }
}