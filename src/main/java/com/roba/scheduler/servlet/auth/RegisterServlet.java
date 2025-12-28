package com.roba.scheduler.servlet.auth;


import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If you don't need registration yet, redirect to login
        response.sendRedirect("login.html");

        /*
         * // If you need registration, uncomment this:
         * String username = request.getParameter("username");
         * String password = request.getParameter("password");
         * String email = request.getParameter("email");
         * String firstName = request.getParameter("firstName");
         * String lastName = request.getParameter("lastName");
         * String major = request.getParameter("major");
         *
         * // First create user
         * User user = new User();
         * user.setUsername(username);
         * user.setPassword(password);
         * user.setEmail(email);
         * user.setRole("student");
         *
         * // Then create student
         * Student student = new Student();
         * student.setFirstName(firstName);
         * student.setLastName(lastName);
         * student.setEmail(email);
         * student.setMajor(major);
         *
         * // TODO: Add methods to UserDAO and StudentDAO for registration
         * // For now, redirect to login
         * response.sendRedirect("login.html");
         */
    }
}