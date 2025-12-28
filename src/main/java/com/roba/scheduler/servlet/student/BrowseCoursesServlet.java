package com.roba.scheduler.servlet.student;

import com.roba.scheduler.dao.*;
import com.roba.scheduler.model.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/student/BrowseCoursesServlet")
public class BrowseCoursesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"student".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        Student student = (Student) session.getAttribute("student");
        if (student == null) {
            StudentDAO studentDAO = new StudentDAO();
            student = studentDAO.getStudentByUserId(user.getUserId());
            session.setAttribute("student", student);
        }

        ScheduledClassDAO classDAO = new ScheduledClassDAO();
        List<Map<String, Object>> availableClasses =
            classDAO.getAvailableClassesForStudent(student.getStudentId());

        CourseDAO courseDAO = new CourseDAO();
        List<Course> allCourses = courseDAO.getAllCourses();

        request.setAttribute("student", student);
        request.setAttribute("availableClasses", availableClasses);
        request.setAttribute("allCourses", allCourses);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/available-courses.jsp");
        dispatcher.forward(request, response);
    }
}