package com.youth4work.yassess_new.network.model.response;

import com.google.gson.annotations.SerializedName;

public class YassesgetTestModel {

    @SerializedName("testName")
    private String testName;

    @SerializedName("sectionId")
    private int sectionId;

    @SerializedName("questionsSlot")
    private int questionsSlot;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getQuestionsSlot() {
        return questionsSlot;
    }

    public void setQuestionsSlot(int questionsSlot) {
        this.questionsSlot = questionsSlot;
    }
}
