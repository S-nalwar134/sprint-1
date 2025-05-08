package com.InterviewSimulator.dao;

import com.InterviewSimulator.entity.User;
import com.InterviewSimulator.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public class UserDAO {

    private Validator validator;

    public UserDAO() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public boolean emailExists(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {  // ✅ Fixed
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return !query.getResultList().isEmpty();
        }
    }

    public void saveUser(User user) {
        // Validate user before saving
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<User> violation : violations) {
                System.out.println("Validation error: " + violation.getMessage());
            }
            return;  // Prevent saving invalid user
        }

        // Check if email already exists
        if (emailExists(user.getEmail())) {
            System.out.println("❌ Email already exists! Please use a different email.");
            return;
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            System.out.println("✅ Account created successfully!");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public User getUserByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }

    public User getUserById(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, userId);
        }
    }
}
