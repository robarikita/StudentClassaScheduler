package com.roba.scheduler.servlet.admin;

import com.roba.scheduler.dao.ClassroomDAO;
import com.roba.scheduler.model.Classroom;
import com.roba.scheduler.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class ClassroomServlet extends HttpServlet {
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
            deleteClassroom(request, response);  // CHANGED FROM "toggle" TO "delete"
        } else {
            addClassroom(request, response);
        }
    }

    private void addClassroom(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String building = request.getParameter("building");
            String roomNumber = request.getParameter("roomNumber");
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String roomType = request.getParameter("roomType");
            boolean hasProjector = "true".equals(request.getParameter("hasProjector"));
            boolean hasComputers = "true".equals(request.getParameter("hasComputers"));

            if (building == null || building.trim().isEmpty() ||
                roomNumber == null || roomNumber.trim().isEmpty()) {

                response.sendRedirect(request.getContextPath() +
                    "/admin/classrooms.jsp?error=Building+and+room+number+are+required");
                return;
            }

            if (capacity < 1 || capacity > 500) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/classrooms.jsp?error=Capacity+must+be+between+1+and+500");
                return;
            }

            Classroom classroom = new Classroom();
            classroom.setBuilding(building);
            classroom.setRoomNumber(roomNumber);
            classroom.setCapacity(capacity);
            classroom.setRoomType(roomType);
            classroom.setHasProjector(hasProjector);
            classroom.setHasComputers(hasComputers);
            classroom.setIsActive(true);

            ClassroomDAO classroomDAO = new ClassroomDAO();
            boolean success = classroomDAO.addClassroom(classroom);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/classrooms.jsp?message=Classroom+added+successfully");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/classrooms.jsp?error=Failed+to+add+classroom");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/classrooms.jsp?error=Invalid+capacity+value");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/classrooms.jsp?error=Server+error");
        }
    }

    private void deleteClassroom(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            ClassroomDAO classroomDAO = new ClassroomDAO();
            boolean success = classroomDAO.deleteClassroom(roomId);  // Now calls hard delete

            if (success) {
                response.sendRedirect(request.getContextPath() +
                    "/admin/classrooms.jsp?message=Classroom+deleted+successfully+from+database");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/admin/classrooms.jsp?error=Failed+to+delete+classroom+(may+be+in+use+by+scheduled+classes)");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                "/admin/classrooms.jsp?error=Invalid+classroom+ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/admin/classrooms.jsp?error=Server+error");
        }
    }
}
