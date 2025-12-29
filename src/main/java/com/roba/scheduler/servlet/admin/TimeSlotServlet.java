package com.roba.scheduler.servlet.admin;

import com.roba.scheduler.dao.TimeSlotDAO;
import com.roba.scheduler.model.TimeSlot;
import com.roba.scheduler.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Time;

@WebServlet("/admin/TimeSlotServlet")
public class TimeSlotServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Check if user is logged in and is admin
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            deleteTimeSlot(request, response);
        } else {
            addTimeSlot(request, response);
        }
    }

    private void addTimeSlot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String slotName = request.getParameter("slotName");
            String dayOfWeek = request.getParameter("dayOfWeek");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");

            // Validate input
            if (slotName == null || slotName.trim().isEmpty() ||
                dayOfWeek == null || dayOfWeek.trim().isEmpty() ||
                startTimeStr == null || endTimeStr == null) {

                response.sendRedirect(request.getContextPath() +
                    "/admin/time-slots.jsp?error=All+fields+are+required");
                return;
            }

            // Parse times
            Time startTime = Time.valueOf(startTimeStr + ":00");
            Time endTime = Time.valueOf(endTimeStr + ":00");

            // Validate end time is after start time
            if (endTime.before(startTime) || endTime.equals(startTime)) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/time-slots.jsp?error=End+time+must+be+after+start+time");
                return;
            }

            // Create TimeSlot object
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setSlotName(slotName);
            timeSlot.setDayOfWeek(dayOfWeek);
            timeSlot.setStartTime(startTime);
            timeSlot.setEndTime(endTime);

            // Save to database
            TimeSlotDAO timeSlotDAO = new TimeSlotDAO();
            boolean success = timeSlotDAO.addTimeSlot(timeSlot);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/time-slots.jsp?message=Time+slot+added+successfully");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/time-slots.jsp?error=Failed+to+add+time+slot");
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/time-slots.jsp?error=Invalid+time+format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/time-slots.jsp?error=Server+error:+ " + e.getMessage());
        }
    }

    private void deleteTimeSlot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int slotId = Integer.parseInt(request.getParameter("slotId"));

            TimeSlotDAO timeSlotDAO = new TimeSlotDAO();
            boolean success = timeSlotDAO.deleteTimeSlot(slotId);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/time-slots.jsp?message=Time+slot+deleted+successfully");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/time-slots.jsp?error=Failed+to+delete+time+slot+(may+be+in+use)");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/time-slots.jsp?error=Invalid+time+slot+ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/time-slots.jsp?error=Server+error");
        }
    }
}