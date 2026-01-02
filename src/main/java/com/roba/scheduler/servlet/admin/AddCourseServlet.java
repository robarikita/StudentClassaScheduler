package com.roba.scheduler.servlet.admin;

import com.roba.scheduler.dao.CourseDAO;
import com.roba.scheduler.model.Course;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/admin/AddCourseServlet")
public class AddCourseServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("../../login.html");
            return;
        }

        try {
          
            String courseCode = request.getParameter("courseCode");
            String courseName = request.getParameter("courseName");
            String description = request.getParameter("description");
            int credits = Integer.parseInt(request.getParameter("credits"));

           
            Course course = new Course();
            course.setCourseCode(courseCode);
            course.setCourseName(courseName);
            course.setDescription(description);
            course.setCredits(credits);

            
            CourseDAO courseDAO = new CourseDAO();
            boolean success = courseDAO.addCourse(course);

            
            if (success) {
                session.setAttribute("message", "Course added successfully!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Failed to add course. Course code might already exist.");
                session.setAttribute("messageType", "error");
            }

            
            response.sendRedirect("ListCoursesServlet");

        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid credits value.");
            session.setAttribute("messageType", "error");
            response.sendRedirect("add-course.html");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "Error: " + e.getMessage());
            session.setAttribute("messageType", "error");
            response.sendRedirect("add-course.html");
        }
    }
}
