package com.roba.scheduler.servlet.admin;

import com.roba.scheduler.util.IDManager;
import com.roba.scheduler.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/IDManagementServlet")
public class IDManagementServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String action = request.getParameter("action");
        String tableName = request.getParameter("tableName");

        try {
            String message = "";

            if ("fix".equals(action) && tableName != null) {
                // Fix auto-increment for specific table
                switch(tableName) {
                    case "classrooms":
                        IDManager.fixAutoIncrement("classrooms", "room_id");
                        message = "Classrooms ID sequence fixed";
                        break;
                    case "instructors":
                        IDManager.fixAutoIncrement("instructors", "instructor_id");
                        message = "Instructors ID sequence fixed";
                        break;
                    case "courses":
                        IDManager.fixAutoIncrement("courses", "course_id");
                        message = "Courses ID sequence fixed";
                        break;
                    case "time_slots":
                        IDManager.fixAutoIncrement("time_slots", "slot_id");
                        message = "Time slots ID sequence fixed";
                        break;
                    case "semesters":
                        IDManager.fixAutoIncrement("semesters", "semester_id");
                        message = "Semesters ID sequence fixed";
                        break;
                    case "scheduled_classes":
                        IDManager.fixAutoIncrement("scheduled_classes", "schedule_id");
                        message = "Scheduled classes ID sequence fixed";
                        break;
                    case "students":
                        IDManager.fixAutoIncrement("students", "student_id");
                        message = "Students ID sequence fixed";
                        break;
                    default:
                        message = "Unknown table: " + tableName;
                }

            } else if ("compact".equals(action) && tableName != null) {
                // Compact IDs for specific table
                switch(tableName) {
                    case "classrooms":
                        IDManager.compactTableIds("classrooms", "room_id");
                        message = "Classrooms IDs compacted";
                        break;
                    case "instructors":
                        IDManager.compactTableIds("instructors", "instructor_id");
                        message = "Instructors IDs compacted";
                        break;
                    case "courses":
                        IDManager.compactTableIds("courses", "course_id");
                        message = "Courses IDs compacted";
                        break;
                    case "time_slots":
                        IDManager.compactTableIds("time_slots", "slot_id");
                        message = "Time slots IDs compacted";
                        break;
                    case "semesters":
                        IDManager.compactTableIds("semesters", "semester_id");
                        message = "Semesters IDs compacted";
                        break;
                    case "scheduled_classes":
                        IDManager.compactTableIds("scheduled_classes", "schedule_id");
                        message = "Scheduled classes IDs compacted";
                        break;
                    case "students":
                        IDManager.compactTableIds("students", "student_id");
                        message = "Students IDs compacted";
                        break;
                    default:
                        message = "Unknown table: " + tableName;
                }

            } else if ("fixAll".equals(action)) {
                // Fix all tables
                IDManager.fixAutoIncrement("classrooms", "room_id");
                IDManager.fixAutoIncrement("instructors", "instructor_id");
                IDManager.fixAutoIncrement("courses", "course_id");
                IDManager.fixAutoIncrement("time_slots", "slot_id");
                IDManager.fixAutoIncrement("semesters", "semester_id");
                IDManager.fixAutoIncrement("scheduled_classes", "schedule_id");
                IDManager.fixAutoIncrement("students", "student_id");
                message = "All ID sequences fixed";

            } else if ("compactAll".equals(action)) {
                // Compact all tables
                IDManager.compactTableIds("classrooms", "room_id");
                IDManager.compactTableIds("instructors", "instructor_id");
                IDManager.compactTableIds("courses", "course_id");
                IDManager.compactTableIds("time_slots", "slot_id");
                IDManager.compactTableIds("semesters", "semester_id");
                IDManager.compactTableIds("scheduled_classes", "schedule_id");
                IDManager.compactTableIds("students", "student_id");
                message = "All tables IDs compacted";

            } else {
                message = "Invalid action specified";
            }

            response.sendRedirect(request.getContextPath() +
                "/admin/dashboard.jsp?message=" + java.net.URLEncoder.encode(message, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/dashboard.jsp?error=ID+management+failed");
        }
    }
}