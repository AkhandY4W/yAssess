package com.youth4work.yassess_new.network.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SampleQuestionModel {

    @SerializedName("id")
    private int id;
    @SerializedName("question")
    private String question;
    @SerializedName("correct")
    private String correct;
    @SerializedName("time2solve")
    private int time2solve;
    @SerializedName("difficultyID")
    private int difficultyID;
    @SerializedName("testID")
    private int testID;
    @SerializedName("options")
    private String options;
    @SerializedName("questionVideoUrl")
    private String questionVideoUrl;
    @SerializedName("questionImageUrl")
    private String questionImageUrl;
    @SerializedName("questionAudioUrl")
    private String questionAudioUrl;
    @SerializedName("difficulty")
    private String difficulty;
    @SerializedName("oiD")
    private String oiD;
    @SerializedName("sampleOptions")
    private List<sampleOptions> sampleOptions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public int getTime2solve() {
        return time2solve;
    }

    public void setTime2solve(int time2solve) {
        this.time2solve = time2solve;
    }

    public int getDifficultyID() {
        return difficultyID;
    }

    public void setDifficultyID(int difficultyID) {
        this.difficultyID = difficultyID;
    }

    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getQuestionVideoUrl() {
        return questionVideoUrl;
    }

    public void setQuestionVideoUrl(String questionVideoUrl) {
        this.questionVideoUrl = questionVideoUrl;
    }

    public String getQuestionImageUrl() {
        return questionImageUrl;
    }

    public void setQuestionImageUrl(String questionImageUrl) {
        this.questionImageUrl = questionImageUrl;
    }

    public String getQuestionAudioUrl() {
        return questionAudioUrl;
    }

    public void setQuestionAudioUrl(String questionAudioUrl) {
        this.questionAudioUrl = questionAudioUrl;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getOiD() {
        return oiD;
    }

    public void setOiD(String oiD) {
        this.oiD = oiD;
    }

    public List<SampleQuestionModel.sampleOptions> getSampleOptions() {
        return sampleOptions;
    }

    public void setSampleOptions(List<SampleQuestionModel.sampleOptions> sampleOptions) {
        this.sampleOptions = sampleOptions;
    }

    public class sampleOptions{
        @SerializedName("optionID")
        private int optionID;
        @SerializedName("option")
        private String option;
        @SerializedName("optionImgUrl")
        private String optionImgUrl;
        @SerializedName("optionVideoUrl")
        private String optionVideoUrl;

        public int getOptionID() {
            return optionID;
        }

        public void setOptionID(int optionID) {
            this.optionID = optionID;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public String getOptionImgUrl() {
            return optionImgUrl;
        }

        public void setOptionImgUrl(String optionImgUrl) {
            this.optionImgUrl = optionImgUrl;
        }

        public String getOptionVideoUrl() {
            return optionVideoUrl;
        }

        public void setOptionVideoUrl(String optionVideoUrl) {
            this.optionVideoUrl = optionVideoUrl;
        }
    }



}
