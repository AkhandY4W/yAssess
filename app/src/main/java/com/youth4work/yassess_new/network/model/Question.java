package com.youth4work.yassess_new.network.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {

    @SerializedName("id")
    private Integer id;
    @SerializedName("question")
    private String question;
    @SerializedName("questionImgUrl")
    private String questionImgUrl;
    @SerializedName("questionVideoUrl")
    private String questionVideoUrl;
    @SerializedName("correct")
    private String correct;
    @SerializedName("time2solve")
    private Integer time2solve;
    @SerializedName("difficulty")
    private String difficulty;
    @SerializedName("entestID")
    private String entestID;
    @SerializedName("testID")
    private Integer testID;
    @SerializedName("rating")
    private Integer rating;
    @SerializedName("options")
    private List<Option> options = null;
    @SerializedName("variables")
    private List<Object> variables = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionImgUrl() {
        return questionImgUrl;
    }

    public void setQuestionImgUrl(String questionImgUrl) {
        this.questionImgUrl = questionImgUrl;
    }

    public String getQuestionVideoUrl() {
        return questionVideoUrl;
    }

    public void setQuestionVideoUrl(String questionVideoUrl) {
        this.questionVideoUrl = questionVideoUrl;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public Integer getTime2solve() {
        return time2solve;
    }

    public void setTime2solve(Integer time2solve) {
        this.time2solve = time2solve;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getTestID() {
        return testID;
    }

    public void setTestID(Integer testID) {
        this.testID = testID;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<Object> getVariables() {
        return variables;
    }

    public void setVariables(List<Object> variables) {
        this.variables = variables;

    }

    public String getEntestID() {
        return entestID;
    }

    public void setEntestID(String entestID) {
        this.entestID = entestID;
    }

    public void setOptionSelected(int position) {
        for (int i = 0; i < this.getOptions().size(); i++) {
            this.getOptions().get(i).setSelected(false);
        }

        this.getOptions().get(position).setSelected(true);
    }
    @Nullable
    public String getSelectedAnswerId() {
        for (int i = 0; i < this.getOptions().size(); i++) {
            if (this.getOptions().get(i).isSelected()) {
                return this.getOptions().get(i).getOptionID();
            }
        }
        return null;
    }
    public boolean isCorrectOrWrong() {
        boolean correctOrWrong = false;
        for (int i = 0; i < this.getOptions().size(); i++) {
            if (this.getOptions().get(i).isSelected() && this.getOptions().get(i).isAnswer()) {
                correctOrWrong = true;
            }
        }
        return correctOrWrong;
    }

    public class Option {

        @SerializedName("optionID")
        private String optionID;

        @SerializedName("option")
        private String option;

        @SerializedName("optionImgUrl")
         private String optionImgUrl;

        @SerializedName("optionVideoUrl")
         private String optionVideoUrl;

        private boolean selected = false;
        private boolean answer = false;

        public String getOptionID() {
            return optionID;
        }

        public void setOptionID(String optionID) {
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

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
        public boolean isAnswer() {
            return answer;
        }

        public void setAnswer(boolean answer) {
            this.answer = answer;
        }

    }

}
