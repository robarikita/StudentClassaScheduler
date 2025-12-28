package com.roba.scheduler.servlet.student;

import com.roba.scheduler.dao.*;
import com.roba.scheduler.model.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/student/DashboardServlet")
public class StudentDashboardServlet extends HttpServlet {

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

        StudentScheduleDAO scheduleDAO = new StudentScheduleDAO();
        List<Map<String, Object>> currentSchedule =
            scheduleDAO.getStudentSchedule(student.getStudentId());

        ScheduledClassDAO classDAO = new ScheduledClassDAO();
        List<Map<String, Object>> availableClasses =
            classDAO.getAvailableClassesForStudent(student.getStudentId());

        request.setAttribute("student", student);
        request.setAttribute("currentSchedule", currentSchedule);
        request.setAttribute("availableClasses", availableClasses);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/dashboard.jsp");
        dispatcher.forward(request, response);
    }
}