package com.roba.scheduler.servlet.admin;

import com.roba.scheduler.dao.CourseDAO;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/admin/DeleteCourseServlet")
public class DeleteCourseServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("../../login.html");
            return;
        }

        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            CourseDAO courseDAO = new CourseDAO();

            if (courseDAO.deleteCourse(courseId)) {
                session.setAttribute("message", "Course deleted successfully!");
            } else {
                session.setAttribute("message", "Failed to delete course.");
            }

            response.sendRedirect("ListCoursesServlet");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}