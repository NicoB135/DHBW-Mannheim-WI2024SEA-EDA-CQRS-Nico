package smarthome.query;

public class HomeStatusView {
    private String homeId;
    private boolean presence;
    private String lastPersonId;
    private long lastSeenTs;

    public HomeStatusView() {}

    public HomeStatusView(String homeId, boolean presence, String lastPersonId, long lastSeenTs) {
        this.homeId = homeId;
        this.presence = presence;
        this.lastPersonId = lastPersonId;
        this.lastSeenTs = lastSeenTs;
    }

    public String getHomeId() { return homeId; }
    public void setHomeId(String homeId) { this.homeId = homeId; }

    public boolean isPresence() { return presence; }
    public void setPresence(boolean presence) { this.presence = presence; }

    public String getLastPersonId() { return lastPersonId; }
    public void setLastPersonId(String lastPersonId) { this.lastPersonId = lastPersonId; }

    public long getLastSeenTs() { return lastSeenTs; }
    public void setLastSeenTs(long lastSeenTs) { this.lastSeenTs = lastSeenTs; }
}