package com.youth4work.yassess_new.network.model.response;

import com.google.gson.annotations.SerializedName;

public class EditprofileResponse {
    @SerializedName("isAadharNo")
    private boolean isAadharNo;
    @SerializedName("isGender")
    private boolean isGender;
    @SerializedName("isProfileHeading")
    private boolean isProfileHeading;
    @SerializedName("isComReq")
    private boolean isComReq;

    public boolean isAadharNo() {
        return isAadharNo;
    }

    public void setAadharNo(boolean aadharNo) {
        isAadharNo = aadharNo;
    }

    public boolean isGender() {
        return isGender;
    }

    public void setGender(boolean gender) {
        isGender = gender;
    }

    public boolean isProfileHeading() {
        return isProfileHeading;
    }

    public void setProfileHeading(boolean profileHeading) {
        isProfileHeading = profileHeading;
    }

    public boolean isComReq() {
        return isComReq;
    }

    public void setComReq(boolean comReq) {
        isComReq = comReq;
    }
}
