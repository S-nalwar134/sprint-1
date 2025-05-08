package com.InterviewSimulator.service;

import com.InterviewSimulator.dao.*;
import com.InterviewSimulator.entity.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Date;
import java.sql.Time;
import java.util.Scanner;

public class InterviewService {
    private final UserDAO userDAO = new UserDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();
    private final ReportDAO reportDAO = new ReportDAO();
    private final InterviewSessionDAO sessionDAO = new InterviewSessionDAO();
    private final AIService aiService = new AIService();
    private final ResponseDAO responseDAO = new ResponseDAO();

    public void createAccount(Scanner scanner) {
        String name, email, mobileNo, password, experienceLevel;

        // Validate Name
        System.out.print("Enter Name: ");
        name = scanner.nextLine().trim();

        // Validate Email
        while (true) {
            System.out.print("Enter Email: ");
            email = scanner.nextLine().trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            System.out.println("❌ Invalid email format! Please enter a valid email.");
            continue;
        }	
        if (userDAO.emailExists(email)) {  // Check if email is already registered
            System.out.println("❌ Email already exists! Please use a different email.");
            continue;
        }
        break;
        }
        // Validate Mobile Number
        while (true) {
            System.out.print("Enter Mobile No: ");
            mobileNo = scanner.nextLine().trim();
            if (mobileNo.matches("\\d{10}")) {
                break;
            }
            System.out.println("❌ Mobile number must be exactly 10 digits. Try again.");
        }

        // Validate Password
        while (true) {
            System.out.print("Enter Password: ");
            password = scanner.nextLine().trim();
            if (password.length() >= 6) {
                break;
            }
            System.out.println("❌ Password must be at least 6 characters long. Try again.");
        }

        // Validate Experience Level
        while (true) {
            System.out.print("Enter Experience Level (beginner/intermediate/expert): ");
            experienceLevel = scanner.nextLine().trim().toLowerCase();
            if (experienceLevel.matches("beginner|intermediate|expert")) {
                break;
            }
            System.out.println("❌ Invalid experience level! Choose 'beginner', 'intermediate', or 'expert'.");
        }

        User user = new User(name, email, mobileNo, password, experienceLevel);
        userDAO.saveUser(user);
        System.out.println("✅ Account created successfully!");
    }

    public void scheduleInterviewSession(Scanner scanner) {
        System.out.print("Enter your registered email: ");
        String email = scanner.nextLine().trim();

        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            System.out.println("❌ Email not found! Please create an account first.");
            return;
        }

        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();

            if (user.getPassword().trim().equals(password.trim())) {
                System.out.println("✅ Authentication successful!");
                break;
            } else {
                attempts--;
                System.out.println("❌ Incorrect password! Attempts left: " + attempts);
                if (attempts == 0) {
                    System.out.println("⛔ Too many failed attempts. Try again later.");
                    return;
                }
            }
        }

        // Schedule interview session
        InterviewSession interviewSession = new InterviewSession();
        interviewSession.setUser(user);
        interviewSession.setScheduleDate(new Date(System.currentTimeMillis()));

        Time startTime;
        while (true) {
            System.out.print("Enter Start Time (HH:mm:ss): ");
            String startTimeStr = scanner.nextLine().trim();
            try {
                startTime = Time.valueOf(startTimeStr);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid time format! Please enter in HH:mm:ss format.");
            }
        }

        // Take End Time from User and Ensure it's after Start Time
        Time endTime;
        while (true) {
            System.out.print("Enter End Time (HH:mm:ss): ");
            String endTimeStr = scanner.nextLine().trim();
            try {
                endTime = Time.valueOf(endTimeStr);
                if (endTime.after(startTime)) {
                    break;
                } else {
                    System.out.println("❌ End Time must be after Start Time! Try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid time format! Please enter in HH:mm:ss format.");
            }
        }

        interviewSession.setStartTime(startTime);
        interviewSession.setEndTime(endTime);

        // Debugging log
        System.out.println("Saving Interview Session:");
        System.out.println("📅 Date: " + interviewSession.getScheduleDate());
        System.out.println("🕒 Start Time: " + interviewSession.getStartTime());
        System.out.println("🕒 End Time: " + interviewSession.getEndTime());

        // Save session to database
        sessionDAO.saveSession(interviewSession);

        System.out.println("✅ Interview session scheduled successfully!");
    }

    public void startInstantInterview(String email, Scanner scanner) {
        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            System.out.println("❌ Email not found! Please create an account first.");
            return;
        }

        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();

            if (user.getPassword().trim().equals(password.trim())) {
                System.out.println("✅ Authentication successful!");
                break;
            } else {
                attempts--;
                System.out.println("❌ Incorrect password! Attempts left: " + attempts);
                if (attempts == 0) {
                    System.out.println("⛔ Too many failed attempts. Try again later.");
                    return;
                }
            }
        }

        // Ensure interview session exists
        InterviewSession interviewSession = sessionDAO.getLatestSessionForUser(user.getId());
        if (interviewSession == null) {
            interviewSession = new InterviewSession();
            interviewSession.setUser(user);
            interviewSession.setScheduleDate(new Date(System.currentTimeMillis()));
            sessionDAO.saveSession(interviewSession);
        }

        int totalScore = 0;
        StringBuilder reportCard = new StringBuilder();
        reportCard.append("\n📋 Interview Report Card\n");

        for (int i = 1; i <= 5; i++) {
            System.out.println("\n📢 Fetching trivia question " + i + "...");

            JSONObject questionData = null;
            int retryCount = 0;
            while (retryCount < 3) {
                questionData = aiService.fetchTriviaQuestionData();
                if (questionData != null) {
                    break;
                } else {
                    retryCount++;
                    System.out.println("⚠️ API rate limit reached. Retrying...");
                    try {
                        Thread.sleep(2000); // Wait for 2 seconds before retrying
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (questionData == null) {
                System.out.println("❌ Failed to fetch a question after multiple retries. Skipping.");
                continue;
            }

            String questionText = questionData.getString("question");
            String correctAnswer = questionData.getString("correct_answer");
            String questionType = questionData.getString("type");

            if (correctAnswer == null || correctAnswer.isEmpty()) {
                System.out.println("❌ No correct answer found for this question. Skipping.");
                continue;
            }

            Question question = new Question();
            question.setQuestionText(questionText);
            question.setDifficultyLevel("medium");
            question.setInterviewSession(interviewSession);
            question.setCorrectAnswer(correctAnswer);
            questionDAO.saveQuestion(question);

            System.out.println("❓ Question " + i + ": " + question.getQuestionText());

            String userAnswer = "";
            int score = 0; // Declare the score variable

            // Detect boolean question type
            if ("boolean".equals(questionType)) {
                System.out.println("💡 Your Answer (True/False): ");
                System.out.print("1. True  2. False\nChoose (1/2): ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline
                if (choice == 1) {
                    userAnswer = "True";
                } else if (choice == 2) {
                    userAnswer = "False";
                }
            } else if ("multiple".equals(questionType)) {
                JSONArray incorrectAnswers = questionData.getJSONArray("incorrect_answers");
                System.out.println("💡 Your Answer (choose a number): ");
                System.out.println("1. " + correctAnswer);
                for (int j = 0; j < incorrectAnswers.length(); j++) {
                    System.out.println((j + 2) + ". " + incorrectAnswers.getString(j));
                }
                System.out.print("Your choice (1-" + (incorrectAnswers.length() + 1) + "): ");
                int answerChoice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline
                if (answerChoice == 1) {
                    userAnswer = correctAnswer;
                } else {
                    userAnswer = incorrectAnswers.getString(answerChoice - 2);
                }
            }

            // After saving the answer, ensure that you get the ID properly
            Answer answer = new Answer();
            answer.setAnswerText(userAnswer);
            answer.setQuestion(question);
            answer.setQuestionText(question.getQuestionText());  // Set the question text
            answerDAO.saveAnswer(answer);

            boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
            score = isCorrect ? 1 : 0; // Update score based on answer correctness
            String feedback = isCorrect ? "Correct!" : "Incorrect. Keep practicing!";

            Response response = new Response();
            response.setUser(user);
            response.setQuestion(question);
            response.setResponseText(userAnswer);
            response.setAiScore(score);
            response.setAiFeedback(feedback);
            response.setCorrectAnswer(correctAnswer);
            response.setAnswer(answer);  // Set the answer object to associate the response with the answer
            responseDAO.saveResponse(response);

            totalScore += score;

            // Save the report after each question
            Report report = new Report();
            report.setUser(user);
            report.setInterviewSession(interviewSession); // Use the correct interview session
            report.setResponse(response);  // Link to the response
            report.setQuestion(question);  // Link to the question
            report.setImprovementSuggestions(generateImprovementSuggestion(userAnswer, correctAnswer)); // Dynamic suggestion
            reportDAO.saveReport(report);  // Save the report

            // Append to report card
            reportCard.append("\nQuestion " + i + ": " + questionText + "\n");
            reportCard.append("Your Answer: " + userAnswer + "\n");
            reportCard.append("Correct Answer: " + correctAnswer + "\n");
            reportCard.append("Result: " + feedback + "\n");
            reportCard.append("Improvement Suggestion: " + generateImprovementSuggestion(userAnswer, correctAnswer) + "\n");
        }

        System.out.println("\n✅ Interview session completed! Your total score: " + totalScore + "/5");

        // Print full report card
        System.out.println(reportCard.toString());
    }

    // Helper method to generate improvement suggestion
    private String generateImprovementSuggestion(String userAnswer, String correctAnswer) {
        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            return "Great job! Keep up the good work.";
        } else {
            return "Review the concepts related to this question. You can study more on the topic.";
        }
    }
}
