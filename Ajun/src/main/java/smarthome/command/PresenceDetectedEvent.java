package smarthome.command;

import java.util.Objects;

public class PresenceDetectedEvent {

    private String homeId;
    private String personId;
    private long ts;

    public PresenceDetectedEvent() {
    }

    public PresenceDetectedEvent(String homeId, String personId, long ts) {
        this.homeId = homeId;
        this.personId = personId;
        this.ts = ts;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PresenceDetectedEvent that = (PresenceDetectedEvent) o;
        return ts == that.ts &&
                Objects.equals(homeId, that.homeId) &&
                Objects.equals(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeId, personId, ts);
    }

    @Override
    public String toString() {
        return "PresenceDetectedEvent{" +
                "homeId='" + homeId + '\'' +
                ", personId='" + personId + '\'' +
                ", ts=" + ts +
                '}';
    }
}