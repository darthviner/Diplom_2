package models;

public class EmailAndName {
    private String email;
    private String name;

    public static EmailAndName from(User user){
        return new EmailAndName(user.getEmail(), user.getName());
    }
    public EmailAndName(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
