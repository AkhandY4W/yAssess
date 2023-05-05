package com.youth4work.yassess_new.network.model.request;

import com.google.gson.annotations.SerializedName;

public class PushAnswerFDTestRequest {
    @SerializedName("AnswerID")
    private String AnswerID;
    @SerializedName("Correct")
    private String Correct;
    @SerializedName("questionid")
    private int questionid;
    @SerializedName("Use    rId")
    private Long UserId;
    @SerializedName("time2solve")
    private int time2solve;
    @SerializedName("Timetaken")
    private int timetaken;

    public PushAnswerFDTestRequest(String answerID, String correct, int questionid, Long userId, int time2solve, int timetaken) {
        AnswerID = answerID;
        Correct = correct;
        this.questionid = questionid;
        UserId = userId;
        this.time2solve = time2solve;
        this.timetaken = timetaken;
    }

    public String getAnswerID() {
        return AnswerID;
    }

    public void setAnswerID(String answerID) {
        AnswerID = answerID;
    }

    public String getCorrect() {
        return Correct;
    }

    public void setCorrect(String correct) {
        Correct = correct;
    }

    public int getQuestionid() {
        return questionid;
    }

    public void setQuestionid(int questionid) {
        this.questionid = questionid;
    }

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    public int getTime2solve() {
        return time2solve;
    }

    public void setTime2solve(int time2solve) {
        this.time2solve = time2solve;
    }

    public int getTimetaken() {
        return timetaken;
    }

    public void setTimetaken(int timetaken) {
        this.timetaken = timetaken;
    }
}
