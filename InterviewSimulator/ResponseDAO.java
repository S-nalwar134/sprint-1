package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.Response;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;  // Import this for the new query API

public class ResponseDAO {

    public Response saveResponse(Response response) {
        // Ensure responseText is populated with a default value if it's null
        if (response.getResponseText() == null || response.getResponseText().isEmpty()) {
            response.setResponseText("No response provided"); // Default response text
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Log the response before saving it
            System.out.println("✅ Saving response: " + response);

            // Use the modern query API: createQuery returns Query<Response> now
            String hql = "FROM Response WHERE user = :user AND question = :question";
            Query<Response> query = session.createQuery(hql, Response.class);
            query.setParameter("user", response.getUser());
            query.setParameter("question", response.getQuestion());

            // Execute the query and check if the response already exists
            Response existingResponse = query.uniqueResult();

            if (existingResponse == null) {
                session.persist(response);  // Save if it doesn't already exist
            } else {
                System.out.println("❌ Response already exists for this question.");
            }

            // Flush to ensure the ID is generated (if it's a new entity)
            session.flush();

            transaction.commit();
            System.out.println("✅ Response saved successfully: ");

            return response;  // Return the saved response
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            // Rollback transaction if a constraint violation occurs
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            System.out.println("❌ Constraint violation: " + e.getMessage());
        } catch (Exception e) {
            // Rollback transaction if any other exception occurs
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            System.out.println("❌ Error saving Response: " + e.getMessage());
        }

        return null;  // Return null if an error occurred
    }
}
