package org.runaway.constructors;

public class User {

    private long user_id;
    private String username;
    private String firstName;
    private String lastName;

    public User(long user_id, String username, String firstName, String lastName) {
        this.user_id = user_id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getUserId() {
        return user_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }
}
