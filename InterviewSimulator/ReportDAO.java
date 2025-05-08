package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.Report;
import com.InterviewSimulator.entity.Response;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ReportDAO {

	public void saveReport(Report report) {
	    Transaction transaction = null;
	    Session session = null;  // Declare session outside try block
	    try {
	        session = HibernateUtil.getSessionFactory().openSession();
	        transaction = session.beginTransaction();

	        // ✅ First, persist the Response entity
	        Response response = report.getResponse();
	        
	        // Merge the response entity if it is detached
	        if (response.getId() != null) {
	            session.merge(response);  // Merge to attach it to the session
	        } else {
	            session.persist(response);  // Otherwise, just persist it
	        }
	        session.flush();  // ✅ Ensure the ID is generated

	        // ✅ Now persist the Report entity with the generated response_id
	        session.persist(report);
	        transaction.commit();
	        System.out.println("✅ Report saved successfully.");
	    } catch (Exception e) {
	        if (transaction != null) {
	            transaction.rollback();
	        }
	        e.printStackTrace();
	        System.out.println("❌ Error saving Report: " + e.getMessage());
	    } finally {
	        if (session != null) {
	            session.close();  // Close session here to avoid errors
	        }
	    }
	}

}
