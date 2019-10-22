package sample.service.responses;

public class StackOverflowUser {
    public final String display_name;
    public final int age;
    public final String location;
    public final int reputation;
    public final int user_id;

    public StackOverflowUser(String display_name, int age, String location, int reputation, int user_id) {
        this.display_name = display_name;
        this.age = age;
        this.location = location;
        this.reputation = reputation;
        this.user_id = user_id;
    }
}
