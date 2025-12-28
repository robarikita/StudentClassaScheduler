package com.roba.scheduler.servlet.auth;

import com.roba.scheduler.dao.UserDAO;
import com.roba.scheduler.dao.StudentDAO;
import com.roba.scheduler.model.User;
import com.roba.scheduler.model.Student;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        UserDAO userDAO = new UserDAO();
        User user = userDAO.validateUser(username, password);

        if (user != null) {
            System.out.println("Login SUCCESS for user: " + user.getUsername() + ", Role: " + user.getRole());

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setAttribute("userId", user.getUserId());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // Debug: Print session info
            System.out.println("Session created: " + session.getId());
            System.out.println("Session attributes set:");
            System.out.println("  - user: " + session.getAttribute("user"));
            System.out.println("  - role: " + session.getAttribute("role"));
            System.out.println("  - username: " + session.getAttribute("username"));

            // Role-based redirection
            if ("admin".equals(user.getRole())) {
                System.out.println("Redirecting admin to dashboard.jsp");
                response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
            } else if ("student".equals(user.getRole())) {
                System.out.println("Redirecting student to DashboardServlet");
                // Get student info
                StudentDAO studentDAO = new StudentDAO();
                Student student = studentDAO.getStudentByUserId(user.getUserId());
                if (student != null) {
                    session.setAttribute("student", student);
                    session.setAttribute("studentId", student.getStudentId());
                    System.out.println("Student ID set: " + student.getStudentId());
                }
                response.sendRedirect(request.getContextPath() + "/student/DashboardServlet");
            } else {
                System.out.println("Unknown role, redirecting to home");
                response.sendRedirect(request.getContextPath() + "/index.html");
            }
        } else {
            System.out.println("Login FAILED for username: " + username);
            request.setAttribute("errorMessage", "Invalid username or password");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/login.html");
            dispatcher.forward(request, response);
        }
    }
}