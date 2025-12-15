package ca.udem.maville;

import java.util.UUID;

public class Notification {
    private String id;
    private String role; // resident / prestataire / agent
    private String title;
    private String text; // message body (frontend expects 'text')
    private long time; // frontend expects 'time'
    private boolean read;
    private String type; // optional: info, success, warn

    public Notification() {
        this.id = UUID.randomUUID().toString();
        this.time = System.currentTimeMillis();
        this.read = false;
        this.type = "info";
    }

    public Notification(String role, String title, String text) {
        this();
        this.role = role;
        this.title = title;
        this.text = text;
    }

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
