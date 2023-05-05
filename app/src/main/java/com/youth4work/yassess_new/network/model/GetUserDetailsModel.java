package com.youth4work.yassess_new.network.model;

import com.google.gson.annotations.SerializedName;

public class GetUserDetailsModel {

    @SerializedName("userId")
    private Long userId;

    @SerializedName("emailid")
    private String emailid;

    @SerializedName("mobileno")
    private String mobileno;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String encryptPassword;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = encryptPassword;
    }
}
