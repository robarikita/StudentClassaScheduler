package com.roba.scheduler.servlet.admin;

import com.roba.scheduler.dao.SemesterDAO;
import com.roba.scheduler.model.Semester;
import com.roba.scheduler.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/admin/SemesterServlet")
public class SemesterServlet extends HttpServlet {
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

        if ("activate".equals(action)) {
            activateSemester(request, response);
        } else if ("delete".equals(action)) {
            deleteSemester(request, response);
        } else {
            addSemester(request, response);
        }
    }

    private void addSemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String semesterCode = request.getParameter("semesterCode");
            String semesterName = request.getParameter("semesterName");
            Date startDate = Date.valueOf(request.getParameter("startDate"));
            Date endDate = Date.valueOf(request.getParameter("endDate"));
            Date registrationStart = Date.valueOf(request.getParameter("registrationStart"));
            Date registrationEnd = Date.valueOf(request.getParameter("registrationEnd"));
            boolean isActive = "true".equals(request.getParameter("isActive"));

            // Validate
            if (semesterCode == null || semesterCode.trim().isEmpty() ||
                semesterName == null || semesterName.trim().isEmpty()) {

                response.sendRedirect(request.getContextPath() +
                    "/admin/semesters.jsp?error=Semester+code+and+name+are+required");
                return;
            }

            if (endDate.before(startDate) || endDate.equals(startDate)) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/semesters.jsp?error=End+date+must+be+after+start+date");
                return;
            }

            if (registrationEnd.before(registrationStart) || registrationEnd.equals(registrationStart)) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/semesters.jsp?error=Registration+end+must+be+after+registration+start");
                return;
            }

            Semester semester = new Semester();
            semester.setSemesterCode(semesterCode);
            semester.setSemesterName(semesterName);
            semester.setStartDate(startDate);
            semester.setEndDate(endDate);
            semester.setRegistrationStart(registrationStart);
            semester.setRegistrationEnd(registrationEnd);
            semester.setIsActive(isActive);

            SemesterDAO semesterDAO = new SemesterDAO();
            boolean success = semesterDAO.addSemester(semester);

            if (success) {
                // If this semester is set as active, activate it
                if (isActive) {
                    semesterDAO.setActiveSemester(semester.getSemesterId());
                }

                response.sendRedirect(request.getContextPath() +
                    "/admin/semesters.jsp?message=Semester+added+successfully");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/semesters.jsp?error=Failed+to+add+semester");
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/semesters.jsp?error=Invalid+date+format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/semesters.jsp?error=Server+error");
        }
    }

    private void activateSemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int semesterId = Integer.parseInt(request.getParameter("semesterId"));

            SemesterDAO semesterDAO = new SemesterDAO();
            boolean success = semesterDAO.setActiveSemester(semesterId);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/semesters.jsp?message=Semester+activated+successfully");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/semesters.jsp?error=Failed+to+activate+semester");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/semesters.jsp?error=Invalid+semester+ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/semesters.jsp?error=Server+error");
        }
    }

    private void deleteSemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int semesterId = Integer.parseInt(request.getParameter("semesterId"));

            SemesterDAO semesterDAO = new SemesterDAO();
            boolean success = semesterDAO.deleteSemester(semesterId);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/semesters.jsp?message=Semester+deleted+successfully");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/semesters.jsp?error=Failed+to+delete+semester");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/semesters.jsp?error=Invalid+semester+ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/semesters.jsp?error=Server+error");
        }
    }
}