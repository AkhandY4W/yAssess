package com.youth4work.yassess_new.network.model.response;

import com.google.gson.annotations.SerializedName;

public class CheckPasskeyStatusResp {
    @SerializedName("testId")
    private int testId;

    @SerializedName("status")
    private String status;

    @SerializedName("timeDiffInMin")
    private String timeDiffInMin;

    @SerializedName("userId")
    private long userId;

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeDiffInMin() {
        return timeDiffInMin;
    }

    public void setTimeDiffInMin(String timeDiffInMin) {
        this.timeDiffInMin = timeDiffInMin;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
