package com.roba.scheduler.servlet.student;

import com.roba.scheduler.dao.*;
import com.roba.scheduler.model.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/student/ViewScheduleServlet")
public class ViewScheduleServlet extends HttpServlet {

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
        List<Map<String, Object>> schedule = scheduleDAO.getStudentSchedule(student.getStudentId());

        // Group schedule by day
        Map<String, List<Map<String, Object>>> scheduleByDay = new LinkedHashMap<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        for (String day : days) {
            scheduleByDay.put(day, new ArrayList<>());
        }

        for (Map<String, Object> item : schedule) {
            String day = (String) item.get("dayOfWeek");
            if (day != null && scheduleByDay.containsKey(day)) {
                scheduleByDay.get(day).add(item);
            }
        }

        request.setAttribute("student", student);
        request.setAttribute("scheduleByDay", scheduleByDay);
        request.setAttribute("fullSchedule", schedule);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/student/my-schedule.jsp");
        dispatcher.forward(request, response);
    }
}