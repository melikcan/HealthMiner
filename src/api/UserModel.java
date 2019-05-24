package api;


public class UserModel {
    String type;
    String userName;
    String password;
    String name;
    String surname;

    public UserModel(String type, String userName, String password, String name, String surname) {
        this.type = type;
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }
}
