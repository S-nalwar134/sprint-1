package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.Answer;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AnswerDAO {
	public void saveAnswer(Answer answer) {
	    Session session = HibernateUtil.getSessionFactory().openSession();
	    Transaction transaction = null;
	    try {
	        transaction = session.beginTransaction();
	        session.persist(answer);  // Make sure the answer object is populated correctly before this
	        transaction.commit();
	    } catch (Exception e) {
	        if (transaction != null) {
	            transaction.rollback();
	        }
	        e.printStackTrace();
	    } finally {
	        session.close();
	    }
	}


}
