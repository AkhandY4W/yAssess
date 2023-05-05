package com.youth4work.yassess_new.network.model;

import com.google.gson.annotations.SerializedName;

public class IsCorect {

    /*@Query("Userid")
    @Query("Correct") String CorrectId,
    @Query("Timetaken") String Timetaken,
    @Query("AnswerID") String AnswerID,
    @Query("guid") String guid,
    @Query("questionid") String questionid,
    @Query("time2solve") String time2solve
    */
    @SerializedName("Userid")
    private long UserId;
    @SerializedName("Correct")
    private String CorrectId;
    @SerializedName("Timetaken")
    private String Timetaken;
    @SerializedName("AnswerID")
    private String AnswerID;
    @SerializedName("guid")
    private String guid;
    @SerializedName("questionid")
    private String questionid;
    @SerializedName("time2solve")
    private String time2solve;

    public IsCorect() {
    }

    public IsCorect(long userId, String correctId, String answerID,String timetaken,  String guid, String questionid, String time2solve) {
        UserId = userId;
        CorrectId = correctId;
        Timetaken = timetaken;
        AnswerID = answerID;
        this.guid = guid;
        this.questionid = questionid;
        this.time2solve = time2solve;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public String getCorrectId() {
        return CorrectId;
    }

    public void setCorrectId(String correctId) {
        CorrectId = correctId;
    }

    public String getTimetaken() {
        return Timetaken;
    }

    public void setTimetaken(String timetaken) {
        Timetaken = timetaken;
    }

    public String getAnswerID() {
        return AnswerID;
    }

    public void setAnswerID(String answerID) {
        AnswerID = answerID;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getQuestionid() {
        return questionid;
    }

    public void setQuestionid(String questionid) {
        this.questionid = questionid;
    }

    public String getTime2solve() {
        return time2solve;
    }

    public void setTime2solve(String time2solve) {
        this.time2solve = time2solve;
    }
}
