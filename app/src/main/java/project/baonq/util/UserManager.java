package project.baonq.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import project.baonq.model.User;

public class UserManager {
    private static User user;

    static {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserManager() {
    }

    public static void setUser(User user) {
        UserManager.user = user;
    }

    public static User getUser() {
        return user;
    }
}
