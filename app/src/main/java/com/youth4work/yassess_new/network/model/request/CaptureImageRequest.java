package com.youth4work.yassess_new.network.model.request;

import com.google.gson.annotations.SerializedName;

public class CaptureImageRequest {

    @SerializedName("questionId")
    private String questionId;
@SerializedName("image")
    private String image;
@SerializedName("testId")
    private String testId;
@SerializedName("userId")
    private Long userId;
@SerializedName("imageExtension")
    private String imageExtension;

    public CaptureImageRequest() {
    }

    public CaptureImageRequest(String questionId, String image, String testId, Long userId, String imageExtension) {
        this.questionId = questionId;
        this.image = image;
        this.testId = testId;
        this.userId = userId;
        this.imageExtension = imageExtension;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public void setImageExtension(String imageExtension) {
        this.imageExtension = imageExtension;
    }
}
