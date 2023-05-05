package com.youth4work.yassess_new.network.model.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    /*{
        "username": "dinesh0078",
            "password": "123456"
    }*/
    @SerializedName( "username")
    private String  username;

    @SerializedName( "password")
    private String  password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
