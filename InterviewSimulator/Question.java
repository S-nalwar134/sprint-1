package com.InterviewSimulator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Q_Id")
    private int id;

    @Column(name = "Question_Text", nullable = false, columnDefinition = "TEXT")
    public String questionText;

    @Column(name = "Difficulty_Level", length = 50)
    private String difficultyLevel;

    @ManyToOne
    @JoinColumn(name = "Session_Id")
    private InterviewSession interviewSession;

    public Question() {}

    public Question(String questionText, String difficultyLevel, InterviewSession interviewSession) {
        this.questionText = questionText;
        this.difficultyLevel = difficultyLevel;
        this.interviewSession = interviewSession;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public InterviewSession getInterviewSession() { return interviewSession; }
    public void setInterviewSession(InterviewSession interviewSession) { this.interviewSession = interviewSession; }

    @Column(name = "Correct_Answer", nullable = false)
    private String correctAnswer;

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }


}
