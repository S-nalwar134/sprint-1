package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.InterviewSession;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class InterviewSessionDAO {

	public void saveSession(InterviewSession session) {
	    Transaction transaction = null;
	    try (Session sessionObj = HibernateUtil.getSessionFactory().openSession()) {
	        transaction = sessionObj.beginTransaction();

	        // Debugging statement
	        System.out.println("🔍 Before saving, Session ID: " + session.getId());

	        sessionObj.persist(session);
	        transaction.commit();

	        System.out.println("✅ Interview Session Saved! Assigned ID: " + session.getId());
	    } catch (Exception e) {
	        if (transaction != null) transaction.rollback();
	        e.printStackTrace();
	    }
	}



    public InterviewSession getLatestSessionForUser(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<InterviewSession> query = session.createQuery(
                "FROM InterviewSession WHERE user.id = :userId ORDER BY sessionId DESC",
                InterviewSession.class
            );
            query.setParameter("userId", userId);
            query.setMaxResults(1);
            return query.uniqueResult();
        }
    }
}
