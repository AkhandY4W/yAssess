package com.youth4work.yassess_new.network.model.response;

import com.google.gson.annotations.SerializedName;

public class InstructionInfoModel {

    @SerializedName("pkCompanyId")
    private long pkCompanyId;

    @SerializedName("companyName")
    private String companyName;

    @SerializedName("logoUrl")
    private String logoUrl;

    @SerializedName("about")
    private String about;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("startInMinute")
    private int startInMinute;

    public long getPkCompanyId() {
        return pkCompanyId;
    }

    public void setPkCompanyId(long pkCompanyId) {
        this.pkCompanyId = pkCompanyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getStartInMinute() {
        return startInMinute;
    }

    public void setStartInMinute(int startInMinute) {
        this.startInMinute = startInMinute;
    }
}
