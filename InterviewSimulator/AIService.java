package com.InterviewSimulator.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class AIService {
    private static final String TRIVIA_API_URL = "https://opentdb.com/api.php?amount=10&category=18&difficulty=easy&type=multiple";
    private static final Logger logger = Logger.getLogger(AIService.class.getName());

    /**
     * Fetches an AI-generated question from Open Trivia Database API.
     */
    public JSONObject fetchTriviaQuestionData() {
        String response = callTriviaAPI(TRIVIA_API_URL);
        if (response == null) {
            return null;
        }

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray results = jsonResponse.getJSONArray("results");

        return results.length() > 0 ? results.getJSONObject(0) : null;
    }

    /**
     * Calls the Trivia API to fetch questions.
     */
    private String callTriviaAPI(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                logger.warning("⚠️ Trivia API returned error: " + conn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            return response.toString();
        } catch (Exception e) {
            logger.severe("❌ Trivia API Request Failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Generates an improvement suggestion based on the average score.
     */
    public String generateImprovementSuggestion(double avgScore) {
        if (avgScore >= 8) {
            return "🌟 Excellent performance! Keep practicing to maintain your skills.";
        } else if (avgScore >= 5) {
            return "✅ Good effort! Focus on improving accuracy and confidence.";
        } else {
            return "❌ You need improvement. Revise the concepts and practice more.";
        }
    }
}
