package com.InterviewSimulator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "response")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Response_Id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "User_Id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Q_Id")
    private Question question;

    @Column(name = "Response_Text", nullable = false)
    private String responseText;

    @Column(name = "AI_Score", precision = 5, scale = 2)
    private double aiScore;

    @Column(name = "AI_Feedback")
    private String aiFeedback;

    @Column(name = "Correct_Answer", nullable = false)
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "Answer_Id")
    private Answer answer;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        if (responseText == null || responseText.isEmpty()) {
            this.responseText = "No response provided"; // Default value
        } else {
            this.responseText = responseText;
        }
    }

    public double getAiScore() {
        return aiScore;
    }

    public void setAiScore(double aiScore) {
        this.aiScore = aiScore;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
