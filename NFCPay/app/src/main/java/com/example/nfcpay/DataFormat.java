package com.example.nfcpay;

public class DataFormat {
    private String identity;
    private String user;
    private int action;

    public DataFormat(String identity, String user, int action) {
        this.identity = identity;
        this.user = user;
        this.action = action;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
