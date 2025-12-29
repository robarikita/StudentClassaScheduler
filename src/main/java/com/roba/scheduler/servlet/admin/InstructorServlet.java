package com.roba.scheduler.servlet.admin;

import com.roba.scheduler.dao.InstructorDAO;
import com.roba.scheduler.model.Instructor;
import com.roba.scheduler.model.User;
import com.roba.scheduler.util.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/admin/InstructorServlet")
public class InstructorServlet extends HttpServlet {
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

        if ("delete".equals(action)) {
            deleteInstructor(request, response);
        } else {
            addInstructor(request, response);
        }
    }


    private void deleteInstructor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int instructorId = Integer.parseInt(request.getParameter("instructorId"));

            InstructorDAO instructorDAO = new InstructorDAO();
            boolean success = instructorDAO.deleteInstructor(instructorId);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/instructors.jsp?message=Instructor+deleted+successfully");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/instructors.jsp?error=Failed+to+delete+instructor+(may+be+teaching+classes)");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/instructors.jsp?error=Invalid+instructor+ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/instructors.jsp?error=Server+error");
        }
    }

    // Helper method to check if user exists by email
    private int getUserIdByEmail(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Helper method to create user account
    private int createUserAccount(String firstName, String lastName, String email) {
        // Generate username (first.last)
        String username = (firstName.toLowerCase() + "." + lastName.toLowerCase()).replaceAll(" ", "");

        // Check if username exists and modify if needed
        int counter = 1;
        String originalUsername = username;

        while (usernameExists(username)) {
            username = originalUsername + counter;
            counter++;
        }

        // Default password
        String password = "instructor123";

        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'instructor')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Helper method to check if username exists
    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void addInstructor(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    try {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String department = request.getParameter("department");
        String officeNumber = request.getParameter("officeNumber");
        String phone = request.getParameter("phone");
        int maxCourses = request.getParameter("maxCourses") != null ?
            Integer.parseInt(request.getParameter("maxCourses")) : 3;

        // Validate
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            department == null || department.trim().isEmpty()) {

            response.sendRedirect(request.getContextPath() +
                "/admin/instructors.jsp?error=Required+fields+are+missing");
            return;
        }

        // Now create instructor record WITHOUT setting userId
        Instructor instructor = new Instructor();
        // DO NOT set userId - let it remain 0 (default for int)

        instructor.setFirstName(firstName);
        instructor.setLastName(lastName);
        instructor.setEmail(email);
        instructor.setDepartment(department);
        instructor.setOfficeNumber(officeNumber);
        instructor.setPhone(phone);
        instructor.setMaxCoursesPerSemester(maxCourses);

        InstructorDAO instructorDAO = new InstructorDAO();
        boolean success = instructorDAO.addInstructor(instructor);

        if (success) {
            String msg = "Instructor added successfully";
            response.sendRedirect(request.getContextPath() +
                "/admin/instructors.jsp?message=" + java.net.URLEncoder.encode(msg, "UTF-8"));
        } else {
            response.sendRedirect(request.getContextPath() +
                "/admin/instructors.jsp?error=Failed+to+add+instructor");
        }

    } catch (NumberFormatException e) {
        response.sendRedirect(request.getContextPath() +
            "/admin/instructors.jsp?error=Invalid+max+courses+value");
    } catch (Exception e) {
        e.printStackTrace();
        response.sendRedirect(request.getContextPath() +
            "/admin/instructors.jsp?error=Server+error");
    }
}
}