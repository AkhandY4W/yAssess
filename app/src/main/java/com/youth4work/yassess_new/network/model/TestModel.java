//-----------------------------------com.youth4work.yassess_ytest.network.Question.TestModel.java-----------------------------------

package com.youth4work.yassess_new.network.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TestModel {

    @SerializedName("yAssessment")
    private List<YAssessment> yAssessment = null;
    @SerializedName("totalCount")
    private Integer totalCount;

    public List<YAssessment> getYAssessment() {
        return yAssessment;
    }

    public void setYAssessment(List<YAssessment> yAssessment) {
        this.yAssessment = yAssessment;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

public class YAssessment {

    @SerializedName("userId")
    private Integer userId;
    @SerializedName("testName")
    private String testName;
    @SerializedName("description")
    private String description;
    @SerializedName("questions")
    private Integer questions;
    @SerializedName("companyName")
    private String companyName;
    @SerializedName("logoUrl")
    private String logoUrl;
    @SerializedName("testid")
    private Integer testid;
    @SerializedName("canGiveTestFlag")
    private Integer canGiveTestFlag;
    @SerializedName("canviewFlag")
    private Integer canviewFlag;
    @SerializedName("userName")
    private String userName;
    @SerializedName("userType")
    private String userType;
    @SerializedName("testMaxStartTime")
    private Integer testMaxStartTime;
    @SerializedName("validityto")
    private String validityto;
    @SerializedName("validityfrom")
    private String validityfrom;

    @SerializedName("entestid")
    private String entestid;

    public String getValidityfrom() {
        return validityfrom;
    }

    public void setValidityfrom(String validityfrom) {
        this.validityfrom = validityfrom;
    }

    public String getEntestid() {
        return entestid;
    }

    public void setEntestid(String entestid) {
        this.entestid = entestid;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuestions() {
        return questions;
    }

    public void setQuestions(Integer questions) {
        this.questions = questions;
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

    public Integer getTestid() {
        return testid;
    }

    public void setTestid(Integer testid) {
        this.testid = testid;
    }

    public Integer getCanGiveTestFlag() {
        return canGiveTestFlag;
    }

    public void setCanGiveTestFlag(Integer canGiveTestFlag) {
        this.canGiveTestFlag = canGiveTestFlag;
    }

    public Integer getCanviewFlag() {
        return canviewFlag;
    }

    public void setCanviewFlag(Integer canviewFlag) {
        this.canviewFlag = canviewFlag;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getTestMaxStartTime() {
        return testMaxStartTime;
    }

    public void setTestMaxStartTime(Integer testMaxStartTime) {
        this.testMaxStartTime = testMaxStartTime;
    }

    public String getValidityto() {
        return validityto;
    }

    public void setValidityto(String validityto) {
        this.validityto = validityto;
    }

}
}