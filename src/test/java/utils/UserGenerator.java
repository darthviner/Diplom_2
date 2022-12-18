package utils;

import models.User;

import java.util.Random;

public class UserGenerator {
    public static User getUnique(){
        Random random = new Random();
        String email = "vinertest"+random.nextInt(1000000000)+"@mail.ru";
        return new User(email,"123","test"+random.nextInt(10000000));
    }
    public static User getUserWithEmptyName(){
        return new User("123","123","");
    }
    public static User getUserWithEmptyPassword(){
        return new User("123","","123");
    }
    public static User getUserWithEmptyEmail(){
        return new User("","123","123");
    }

}
