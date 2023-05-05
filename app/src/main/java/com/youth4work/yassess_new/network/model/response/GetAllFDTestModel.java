package com.youth4work.yassess_new.network.model.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.youth4work.yassess_new.network.model.Question;

import java.util.ArrayList;
import java.util.List;

public class GetAllFDTestModel {

    @SerializedName("questions")
    private ArrayList<MockQuestion> mockQuestions;
    @SerializedName("test")
    private MockTest mockTest;

    public MockTest getMockTest() {
        return mockTest;
    }

    public void setMockTest(MockTest mockTest) {
        this.mockTest = mockTest;
    }

    public ArrayList<MockQuestion> getMockQuestions() {
        return mockQuestions;
    }

    public void setMockQuestions(ArrayList<MockQuestion> mockQuestions) {
        this.mockQuestions = mockQuestions;
    }

    public class MockTest{
        @SerializedName("testId")
        protected int testId;
        @SerializedName("duration")
        protected int duration;
        @SerializedName("questionsSlot")
        protected int noOfQuestions;
        @SerializedName("testName")
        protected String testName;
        @SerializedName("testType")
        protected String testType;
        @SerializedName("langId")
        protected int langId;
        @SerializedName("isVisiblAnswer")
        protected boolean isVisiblAnswer;
        @SerializedName("isProctoring")
        protected boolean isProctoring;
        @SerializedName("can_Min_Change_Win")
        protected boolean can_Min_Change_Win;
        @SerializedName("canPrintQsAns")
        protected boolean canPrintQsAns;
        @SerializedName("canAllowFullscreen")
        protected boolean canAllowFullscreen;

        public int getNoOfQuestions() {
            return noOfQuestions;
        }

        public void setNoOfQuestions(int noOfQuestions) {
            this.noOfQuestions = noOfQuestions;
        }

        public int getTestId() {
            return testId;
        }

        public void setTestId(int testId) {
            this.testId = testId;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }


        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public String getTestType() {
            return testType;
        }

        public void setTestType(String testType) {
            this.testType = testType;
        }

        public int getLangId() {
            return langId;
        }

        public void setLangId(int langId) {
            this.langId = langId;
        }

        public boolean isVisiblAnswer() {
            return isVisiblAnswer;
        }

        public void setVisiblAnswer(boolean visiblAnswer) {
            isVisiblAnswer = visiblAnswer;
        }

        public boolean isProctoring() {
            return isProctoring;
        }

        public void setProctoring(boolean proctoring) {
            isProctoring = proctoring;
        }

        public boolean isCan_Min_Change_Win() {
            return can_Min_Change_Win;
        }

        public void setCan_Min_Change_Win(boolean can_Min_Change_Win) {
            this.can_Min_Change_Win = can_Min_Change_Win;
        }

        public boolean isCanPrintQsAns() {
            return canPrintQsAns;
        }

        public void setCanPrintQsAns(boolean canPrintQsAns) {
            this.canPrintQsAns = canPrintQsAns;
        }

        public boolean isCanAllowFullscreen() {
            return canAllowFullscreen;
        }

        public void setCanAllowFullscreen(boolean canAllowFullscreen) {
            this.canAllowFullscreen = canAllowFullscreen;
        }
    }



    public class MockQuestion {
        @SerializedName("sNo")
        protected int sNo;
        @SerializedName("sectionName")
        protected String sectionName;
        @SerializedName("sectionId")
        protected int sectionId;
        @SerializedName("questionid")
        protected int questionid;
        @SerializedName("question")
        protected String question;
        @SerializedName("questionImageUrl")
        protected String QuestionImageUrl;
        @SerializedName("questionVideoUrl")
        protected String questionVideoUrl;
        @SerializedName("difficulty")
        protected String Difficulty;
        @SerializedName("correct")
        protected String Correct;
        @SerializedName("rating")
        protected int Rating;
        @SerializedName("time2solve")
        protected int time2solve;
        @SerializedName("isattempted")
        protected boolean Isattempted;
        @SerializedName("optionChoosen")
        protected int optionChoosen;
        @SerializedName("Isflagged")
        protected boolean Isflagged;
        @SerializedName("isVaraible")
        protected boolean isVaraible;
        @SerializedName("options")
        protected List<Question.Option> options;
        private int selectedAns;
        public int getsNo() {
            return sNo;
        }

        public void setsNo(int sNo) {
            this.sNo = sNo;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public int getSectionId() {
            return sectionId;
        }

        public void setSectionId(int sectionId) {
            this.sectionId = sectionId;
        }

        public int getQuestionid() {
            return questionid;
        }

        public void setQuestionid(int questionid) {
            this.questionid = questionid;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getQuestionImageUrl() {
            return QuestionImageUrl;
        }

        public void setQuestionImageUrl(String questionImageUrl) {
            QuestionImageUrl = questionImageUrl;
        }

        public String getDifficulty() {
            return Difficulty;
        }

        public void setDifficulty(String difficulty) {
            Difficulty = difficulty;
        }

        public String getCorrect() {
            return Correct;
        }

        public void setCorrect(String correct) {
            Correct = correct;
        }

        public int getRating() {
            return Rating;
        }

        public void setRating(int rating) {
            Rating = rating;
        }

        public int getTime2solve() {
            return time2solve;
        }

        public void setTime2solve(int time2solve) {
            this.time2solve = time2solve;
        }

        public boolean isIsattempted() {
            return Isattempted;
        }

        public void setIsattempted(boolean isattempted) {
            Isattempted = isattempted;
        }

        public int getOptionChoosen() {
            return optionChoosen;
        }

        public void setOptionChoosen(int optionChoosen) {
            this.optionChoosen = optionChoosen;
        }

        public boolean isIsflagged() {
            return Isflagged;
        }

        public void setIsflagged(boolean isflagged) {
            Isflagged = isflagged;
        }

        public List<Question.Option> getOptions() {
            return options;
        }

        public void setOptions(List<Question.Option> options) {
            this.options = options;
        }

        public void setOptionSelected(int position) {
            for (int i = 0; i < this.getOptions().size(); i++) {
                this.getOptions().get(i).setSelected(false);
            }

            this.getOptions().get(position).setSelected(true);
        }
        public int getSelectedAns() {
            return selectedAns;
        }

        public void setSelectedAns(int selectedAns) {
            this.selectedAns = selectedAns;
        }
        public boolean isCorrectOrWrong() {
            boolean correctOrWrong = false;
            for (int i = 0; i < this.getOptions().size(); i++) {
                if (this.getOptions().get(i).isSelected() && this.getOptions().get(i).getOptionID().equals(String.valueOf(this.Correct))) {
                    correctOrWrong = true;
                }
            }
            return correctOrWrong;
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
    }


}
