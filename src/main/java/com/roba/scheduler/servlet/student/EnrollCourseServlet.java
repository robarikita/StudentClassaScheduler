package com.roba.scheduler.servlet.student;

import com.roba.scheduler.dao.EnrollmentDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/student/enroll")
public class EnrollCourseServlet extends HttpServlet {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Integer studentId = (Integer) req.getSession().getAttribute("studentId");

        if (studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        int courseId = Integer.parseInt(req.getParameter("courseId"));
        enrollmentDAO.enrollStudent(studentId, courseId);

        resp.sendRedirect(req.getContextPath() + "/student/courses");
    }
}
