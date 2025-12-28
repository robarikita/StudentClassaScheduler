package com.roba.scheduler.servlet.student;

import com.roba.scheduler.dao.*;
import com.roba.scheduler.model.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/student/EnrollServlet")
public class EnrollServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"student".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String scheduleIdParam = request.getParameter("scheduleId");
        if (scheduleIdParam == null || scheduleIdParam.isEmpty()) {
            session.setAttribute("errorMessage", "No schedule ID provided");
            response.sendRedirect("DashboardServlet");
            return;
        }

        try {
            int scheduleId = Integer.parseInt(scheduleIdParam);

            Student student = (Student) session.getAttribute("student");
            if (student == null) {
                StudentDAO studentDAO = new StudentDAO();
                student = studentDAO.getStudentByUserId(user.getUserId());
                session.setAttribute("student", student);
            }

            StudentScheduleDAO scheduleDAO = new StudentScheduleDAO();

            // Check for schedule conflict
            if (scheduleDAO.hasScheduleConflict(student.getStudentId(), scheduleId)) {
                session.setAttribute("errorMessage", "Schedule conflict detected!");
                response.sendRedirect("DashboardServlet");
                return;
            }

            // Check if already enrolled
            if (scheduleDAO.isAlreadyEnrolled(student.getStudentId(), scheduleId)) {
                session.setAttribute("errorMessage", "Already enrolled in this class");
                response.sendRedirect("DashboardServlet");
                return;
            }

            // Enroll student
            boolean success = scheduleDAO.enrollStudent(student.getStudentId(), scheduleId);

            if (success) {
                ScheduledClassDAO classDAO = new ScheduledClassDAO();
                classDAO.updateEnrollmentCount(scheduleId, 1);
                session.setAttribute("successMessage", "Successfully enrolled!");
            } else {
                session.setAttribute("errorMessage", "Failed to enroll");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid schedule ID");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect("DashboardServlet");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}