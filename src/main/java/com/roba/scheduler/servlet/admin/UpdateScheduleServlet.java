package com.roba.scheduler.servlet.admin;

import com.roba.scheduler.dao.*;
import com.roba.scheduler.model.*;
import com.roba.scheduler.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/UpdateScheduleServlet")
public class UpdateScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        try {
            int scheduleId = Integer.parseInt(request.getParameter("scheduleId"));
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            int maxCapacity = Integer.parseInt(request.getParameter("maxCapacity"));
            boolean isActive = "true".equals(request.getParameter("isActive"));

            Integer instructorId = null;
            Integer roomId = null;

            if (request.getParameter("instructorId") != null && !request.getParameter("instructorId").isEmpty()) {
                instructorId = Integer.parseInt(request.getParameter("instructorId"));
            }

            if (request.getParameter("roomId") != null && !request.getParameter("roomId").isEmpty()) {
                roomId = Integer.parseInt(request.getParameter("roomId"));
            }

            if (maxCapacity < 1 || maxCapacity > 500) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/edit-schedule.jsp?scheduleId=" + scheduleId + "&error=Invalid+capacity");
                return;
            }

            ScheduledClassDAO scheduledClassDAO = new ScheduledClassDAO();
            InstructorDAO instructorDAO = new InstructorDAO();
            ClassroomDAO classroomDAO = new ClassroomDAO();
            TimeSlotDAO timeSlotDAO = new TimeSlotDAO();

            ScheduledClass scheduledClass = scheduledClassDAO.getScheduledClassById(scheduleId);

            if (scheduledClass == null) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?error=Scheduled+class+not+found");
                return;
            }

            if (maxCapacity < scheduledClass.getCurrentEnrollment()) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/edit-schedule.jsp?scheduleId=" + scheduleId +
                    "&error=Capacity+cannot+be+less+than+current+enrollment+(" +
                    scheduledClass.getCurrentEnrollment() + ")");
                return;
            }

            if (roomId != null) {
                Classroom classroom = classroomDAO.getClassroomById(roomId);
                if (classroom != null && maxCapacity > classroom.getCapacity()) {
                    response.sendRedirect(request.getContextPath() +
                        "/admin/edit-schedule.jsp?scheduleId=" + scheduleId +
                        "&error=Class+capacity+cannot+exceed+classroom+capacity+(" +
                        classroom.getCapacity() + ")");
                    return;
                }
            }

            boolean hasConflict = false;
            StringBuilder conflictMessage = new StringBuilder();

            if (roomId != null) {
                if (scheduledClassDAO.hasRoomConflict(roomId, slotId, scheduledClass.getSemester().getSemesterId(), scheduleId)) {
                    hasConflict = true;
                    conflictMessage.append("Classroom is already occupied at this time. ");
                }
            }

            if (instructorId != null) {
                if (scheduledClassDAO.hasInstructorConflict(instructorId, slotId, scheduledClass.getSemester().getSemesterId(), scheduleId)) {
                    hasConflict = true;
                    conflictMessage.append("Instructor has another class at this time. ");
                }
            }

            TimeSlot timeSlot = timeSlotDAO.getTimeSlotById(slotId);
            scheduledClass.setTimeSlot(timeSlot);
            scheduledClass.setMaxCapacity(maxCapacity);
            scheduledClass.setIsActive(isActive);

            if (instructorId != null) {
                Instructor instructor = instructorDAO.getInstructorById(instructorId);
                scheduledClass.setInstructor(instructor);
            } else {
                scheduledClass.setInstructor(null);
            }

            if (roomId != null) {
                Classroom classroom = classroomDAO.getClassroomById(roomId);
                scheduledClass.setClassroom(classroom);
            } else {
                scheduledClass.setClassroom(null);
            }

            boolean success = scheduledClassDAO.updateScheduledClass(scheduledClass);

            if (success) {
                if (hasConflict) {
                    response.sendRedirect(request.getContextPath() +
                        "/admin/edit-schedule.jsp?scheduleId=" + scheduleId +
                        "&message=Class+updated+successfully&warning=" +
                        java.net.URLEncoder.encode(conflictMessage.toString(), "UTF-8"));
                } else {
                    response.sendRedirect(request.getContextPath() +
                        "/admin/edit-schedule.jsp?scheduleId=" + scheduleId +
                        "&message=Class+updated+successfully");
                }
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/edit-schedule.jsp?scheduleId=" + scheduleId +
                    "&error=Failed+to+update+class");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/scheduled-classes.jsp?error=Invalid+input+values");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/scheduled-classes.jsp?error=Server+error");
        }
    }
}