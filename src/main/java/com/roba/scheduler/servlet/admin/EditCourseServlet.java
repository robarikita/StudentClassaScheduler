package com.roba.scheduler.servlet.admin;

import com.roba.scheduler.dao.CourseDAO;
import com.roba.scheduler.model.Course;
import com.roba.scheduler.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/EditCourseServlet")
public class EditCourseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            CourseDAO courseDAO = new CourseDAO();
            Course course = courseDAO.getCourseById(courseId);

            if (course != null) {
                request.setAttribute("course", course);
                request.getRequestDispatcher("/admin/edit-course.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/ListCoursesServlet?error=Course+not+found");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/ListCoursesServlet?error=Invalid+course+ID");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            String courseCode = request.getParameter("courseCode");
            String courseName = request.getParameter("courseName");
            String description = request.getParameter("description");
            int credits = Integer.parseInt(request.getParameter("credits"));

            CourseDAO courseDAO = new CourseDAO();
            Course course = courseDAO.getCourseById(courseId);

            if (course == null) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/ListCoursesServlet?error=Course+not+found");
                return;
            }

            course.setCourseCode(courseCode);
            course.setCourseName(courseName);
            course.setDescription(description);
            course.setCredits(credits);

            boolean success = courseDAO.updateCourse(course);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/ListCoursesServlet?message=Course+updated+successfully");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/ListCoursesServlet?error=Failed+to+update+course");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/ListCoursesServlet?error=Invalid+input+values");
        }
    }
}