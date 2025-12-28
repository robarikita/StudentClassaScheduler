package com.roba.scheduler.servlet.student;

import com.roba.scheduler.dao.*;
import com.roba.scheduler.model.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/student/DropClassServlet")
public class DropClassServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"student".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String studentScheduleIdParam = request.getParameter("studentScheduleId");
        if (studentScheduleIdParam == null || studentScheduleIdParam.isEmpty()) {
            session.setAttribute("errorMessage", "❌ No class specified");
            response.sendRedirect("DashboardServlet");
            return;
        }

        try {
            int studentScheduleId = Integer.parseInt(studentScheduleIdParam);

            StudentScheduleDAO scheduleDAO = new StudentScheduleDAO();
            boolean success = scheduleDAO.dropClass(studentScheduleId);

            if (success) {
                // Update enrollment count
                int scheduleId = scheduleDAO.getScheduleIdFromStudentSchedule(studentScheduleId);
                if (scheduleId > 0) {
                    ScheduledClassDAO classDAO = new ScheduledClassDAO();
                    classDAO.updateEnrollmentCount(scheduleId, -1);
                }
                session.setAttribute("successMessage", "✅ Successfully dropped the class!");
            } else {
                session.setAttribute("errorMessage", "❌ Failed to drop class.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "❌ Invalid class ID");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "❌ Error: " + e.getMessage());
            e.printStackTrace();
        }

        response.sendRedirect("DashboardServlet");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}