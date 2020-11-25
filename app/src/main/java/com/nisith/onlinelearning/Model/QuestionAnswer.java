package com.nisith.onlinelearning.Model;

public class QuestionAnswer {
    private String question;
    private String answer;
    private long timeStamp;

    public QuestionAnswer(){}

    public QuestionAnswer(String question, String answer, long timeStamp) {
        this.question = question;
        this.answer = answer;
        this.timeStamp = timeStamp;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
