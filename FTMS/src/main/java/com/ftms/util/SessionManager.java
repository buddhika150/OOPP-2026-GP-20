package com.ftms.util;

public class SessionManager {
    private static SessionManager instance;
    private int loggedInUserId = -1;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public void logout() {
        loggedInUserId = -1;
    }
}