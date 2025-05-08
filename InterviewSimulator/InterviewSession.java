package com.InterviewSimulator.entity;

import jakarta.persistence.*;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.Root;

import java.util.Date;

//import org.hibernate.Session;

//import com.InterviewSimulator.util.HibernateUtil;
import java.sql.Time;
//import jakarta.persistence.*;

@Entity
@Table(name = "interview_session")
public class InterviewSession {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Session_Id", updatable = false, nullable = false)  // ✅ Match the database column name exactly
    private int sessionId;
	
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "schedule_date", nullable = false)
    private Date scheduleDate;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)  // ✅ Re-adding end_time
    private Time endTime;

    // Getters and Setters
    public int getId() {
        return sessionId;
    }

    public void sessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {  // ✅ Add getter for end_time
        return endTime;
    }

    public void setEndTime(Time endTime) {  // ✅ Add setter for end_time
        this.endTime = endTime;
    }
}
