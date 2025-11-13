package ca.udem.maville;

public class User {
    public String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password; //meme pas necessaire dans ce contexte
    }

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public void setPassword() {
        this.password = password;
    }
}
