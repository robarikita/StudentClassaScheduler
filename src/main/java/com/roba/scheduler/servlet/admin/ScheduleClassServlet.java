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

@WebServlet("/admin/ScheduleClassServlet")
public class ScheduleClassServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String action = request.getParameter("action");

        if ("toggle".equals(action)) {
            toggleSchedule(request, response);
        } else if ("delete".equals(action)) {
            deleteSchedule(request, response);
        } else {
            addSchedule(request, response);
        }
    }

    private void addSchedule(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            int semesterId = Integer.parseInt(request.getParameter("semesterId"));
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            String sectionNumber = request.getParameter("sectionNumber");
            int maxCapacity = Integer.parseInt(request.getParameter("maxCapacity"));

            Integer instructorId = null;
            Integer roomId = null;

            if (request.getParameter("instructorId") != null && !request.getParameter("instructorId").isEmpty()) {
                instructorId = Integer.parseInt(request.getParameter("instructorId"));
            }

            if (request.getParameter("roomId") != null && !request.getParameter("roomId").isEmpty()) {
                roomId = Integer.parseInt(request.getParameter("roomId"));
            }

            if (sectionNumber == null || sectionNumber.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?error=Section+number+is+required");
                return;
            }

            if (maxCapacity < 1 || maxCapacity > 500) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?error=Invalid+capacity");
                return;
            }

            CourseDAO courseDAO = new CourseDAO();
            SemesterDAO semesterDAO = new SemesterDAO();
            TimeSlotDAO timeSlotDAO = new TimeSlotDAO();
            InstructorDAO instructorDAO = new InstructorDAO();
            ClassroomDAO classroomDAO = new ClassroomDAO();
            ScheduledClassDAO scheduledClassDAO = new ScheduledClassDAO();

            Course course = courseDAO.getCourseById(courseId);
            Semester semester = semesterDAO.getSemesterById(semesterId);
            TimeSlot timeSlot = timeSlotDAO.getTimeSlotById(slotId);

            if (course == null || semester == null || timeSlot == null) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?error=Invalid+course,+semester,+or+time+slot");
                return;
            }

            Instructor instructor = null;
            if (instructorId != null) {
                instructor = instructorDAO.getInstructorById(instructorId);
            }

            Classroom classroom = null;
            if (roomId != null) {
                classroom = classroomDAO.getClassroomById(roomId);
                if (classroom != null && maxCapacity > classroom.getCapacity()) {
                    response.sendRedirect(request.getContextPath() +
                        "/admin/scheduled-classes.jsp?error=Class+capacity+cannot+exceed+classroom+capacity+(" +
                        classroom.getCapacity() + ")");
                    return;
                }
            }

            boolean hasConflict = false;
            StringBuilder conflictMessage = new StringBuilder();

            if (roomId != null) {
                if (scheduledClassDAO.hasRoomConflict(roomId, slotId, semesterId, 0)) {
                    hasConflict = true;
                    conflictMessage.append("Classroom is already occupied at this time. ");
                }
            }

            if (instructorId != null) {
                if (scheduledClassDAO.hasInstructorConflict(instructorId, slotId, semesterId, 0)) {
                    hasConflict = true;
                    conflictMessage.append("Instructor has another class at this time. ");
                }
            }

            ScheduledClass scheduledClass = new ScheduledClass();
            scheduledClass.setCourse(course);
            scheduledClass.setSemester(semester);
            scheduledClass.setTimeSlot(timeSlot);
            scheduledClass.setSectionNumber(sectionNumber);
            scheduledClass.setMaxCapacity(maxCapacity);
            scheduledClass.setCurrentEnrollment(0);
            scheduledClass.setIsActive(true);

            if (instructor != null) {
                scheduledClass.setInstructor(instructor);
            }

            if (classroom != null) {
                scheduledClass.setClassroom(classroom);
            }

            boolean success = scheduledClassDAO.addScheduledClass(scheduledClass);

            if (success) {
                if (hasConflict) {
                    response.sendRedirect(request.getContextPath() +
                        "/admin/scheduled-classes.jsp?message=Class+scheduled+successfully&warning=" +
                        java.net.URLEncoder.encode(conflictMessage.toString(), "UTF-8"));
                } else {
                    response.sendRedirect(request.getContextPath() +
                        "/admin/scheduled-classes.jsp?message=Class+scheduled+successfully");
                }
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?error=Failed+to+schedule+class");
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

    private void toggleSchedule(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int scheduleId = Integer.parseInt(request.getParameter("scheduleId"));

            ScheduledClassDAO scheduledClassDAO = new ScheduledClassDAO();
            ScheduledClass scheduledClass = scheduledClassDAO.getScheduledClassById(scheduleId);

            if (scheduledClass == null) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?error=Scheduled+class+not+found");
                return;
            }

            scheduledClass.setIsActive(!scheduledClass.getIsActive());
            boolean success = scheduledClassDAO.updateScheduledClass(scheduledClass);

            if (success) {
                String message = scheduledClass.getIsActive() ?
                    "Class+activated+successfully" : "Class+deactivated+successfully";
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?message=" + message);
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?error=Failed+to+update+class");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/scheduled-classes.jsp?error=Invalid+schedule+ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/scheduled-classes.jsp?error=Server+error");
        }
    }

    private void deleteSchedule(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int scheduleId = Integer.parseInt(request.getParameter("scheduleId"));

            ScheduledClassDAO scheduledClassDAO = new ScheduledClassDAO();
            boolean success = scheduledClassDAO.deleteScheduledClass(scheduleId);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?message=Scheduled+class+deleted+successfully");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/scheduled-classes.jsp?error=Failed+to+delete+scheduled+class");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/scheduled-classes.jsp?error=Invalid+schedule+ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/scheduled-classes.jsp?error=Server+error");
        }
    }
}