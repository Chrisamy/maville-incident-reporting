package ca.udem.maville;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true) //to ignore the _id property
public class ProblemForm {
    private final String id;
    private EnumWorkType workType;
    private String location;
    private EnumBoroughID boroughId;
    private String username;
    private EnumPriority priority;
    private String description;
    private EnumStatus status;
    private long date; // submission timestamp (server-side)

    //constructor for JSON deserialization
    public ProblemForm() {
        this.id = IdGenerator.generateID();
        this.workType = EnumWorkType.notDefined;
        this.priority = EnumPriority.notAssigned;
        this.status = EnumStatus.waitingForApproval;
    }

    public ProblemForm(String location, String username, String description) {
        this();
        this.location = location;
        this.username = username;
        this.description = description;
    }

    public ProblemForm(String id, EnumWorkType workType, EnumBoroughID boroughId, EnumPriority priority, String description , EnumStatus status, String username) {
        this.id = id;
        this.workType = workType;
        this.boroughId = boroughId;
        this.priority = priority;
        this.description = description;
        this.status = status;
        this.username = username;

    }

    // Getters
    public String getId() {
        return id;
    }

    public EnumWorkType getWorkType() {
        return workType;
    }

    public String getLocation() {
        return location;
    }

    public String getUsername() {
        return username;
    }

    public EnumPriority getPriority() {
        return priority;
    }


    public String getDescription() {
        return description;
    }

    public EnumStatus getStatus() {
        return status;
    }

    public EnumBoroughID getBoroughId() { return boroughId; }
    public long getDate() { return date; }

    // Setters (no setter for id)
    public void setWorkType(EnumWorkType workType) {
        this.workType = workType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPriority(EnumPriority priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(EnumStatus status) {
        this.status = status;
    }

    public void setBoroughId(EnumBoroughID boroughId) { this.boroughId = boroughId; }
    public void setDate(long date) { this.date = date; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProblemForm that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProblemForm{" +
                "id='" + id + '\'' +
                ", workType=" + workType +
                ", location='" + location + '\'' +
                ", username='" + username + '\'' +
                ", priority=" + priority +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
