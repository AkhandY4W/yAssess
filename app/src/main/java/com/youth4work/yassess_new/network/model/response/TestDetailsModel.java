package com.youth4work.yassess_new.network.model.response;

import com.google.gson.annotations.SerializedName;

public class TestDetailsModel {
    @SerializedName("testId")
    private int testId;
    @SerializedName("testName")
    private String testName;
    @SerializedName("logoUrl")
    private String logoUrl;
    @SerializedName("questions")
    private int questions;
    @SerializedName("visibleAnswer")
    private Boolean visibleAnswer;
    @SerializedName("duration")
    private int duration;
    @SerializedName("proctoring")
    private int proctoring;
    @SerializedName("canminchangeWin")
    private Boolean canminchangeWin;
    @SerializedName("assessmentype")
    private String assessmentype;
    @SerializedName("canminchangeWinCnt")
    private int canminchangeWinCnt;
    @SerializedName("isbrowsescreen")
    private Boolean isbrowsescreen;
    @SerializedName("sectionId")
    private int sectionId;

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    public Boolean getVisibleAnswer() {
        return visibleAnswer;
    }

    public void setVisibleAnswer(Boolean visibleAnswer) {
        this.visibleAnswer = visibleAnswer;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getProctoring() {
        return proctoring;
    }

    public void setProctoring(int proctoring) {
        this.proctoring = proctoring;
    }

    public Boolean getCanminchangeWin() {
        return canminchangeWin;
    }

    public void setCanminchangeWin(Boolean canminchangeWin) {
        this.canminchangeWin = canminchangeWin;
    }

    public String getAssessmentype() {
        return assessmentype;
    }

    public void setAssessmentype(String assessmentype) {
        this.assessmentype = assessmentype;
    }

    public int getCanminchangeWinCnt() {
        return canminchangeWinCnt;
    }

    public void setCanminchangeWinCnt(int canminchangeWinCnt) {
        this.canminchangeWinCnt = canminchangeWinCnt;
    }

    public Boolean getIsbrowsescreen() {
        return isbrowsescreen;
    }

    public void setIsbrowsescreen(Boolean isbrowsescreen) {
        this.isbrowsescreen = isbrowsescreen;
    }
}
