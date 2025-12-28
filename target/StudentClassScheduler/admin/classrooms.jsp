<%@ page import="com.roba.scheduler.model.User" %>
<%
    // Debug: Print session info
    System.out.println("=== Checking session for time-slots.jsp ===");
    System.out.println("Session ID: " + session.getId());

    User user = (User) session.getAttribute("user");
    if (user == null) {
        System.out.println("USER IS NULL - redirecting to login");
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    System.out.println("User found: " + user.getUsername());
    System.out.println("User role: " + user.getRole());

    if (!"admin".equals(user.getRole())) {
        System.out.println("User is not admin - redirecting");
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    System.out.println("Access granted to admin: " + user.getUsername());
%>







<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    if (session.getAttribute("user") == null || !"admin".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Classrooms - Admin Dashboard</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; }

        .header {
            background-color: #2c3e50;
            color: white;
            padding: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo { font-size: 1.5rem; font-weight: bold; }

        .nav-menu {
            display: flex;
            gap: 1rem;
            list-style: none;
        }

        .nav-menu a {
            color: white;
            text-decoration: none;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        .nav-menu a:hover { background-color: #34495e; }
        .nav-menu a.active { background-color: #34495e; }

        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }

        .dashboard-header {
            background-color: white;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .btn {
            background-color: #3498db;
            color: white;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }

        .btn:hover { background-color: #2980b9; }
        .btn-success { background-color: #27ae60; }
        .btn-success:hover { background-color: #219653; }
        .btn-danger { background-color: #e74c3c; }
        .btn-danger:hover { background-color: #c0392b; }

        .form-section {
            background-color: white;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .form-group {
            margin-bottom: 1rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: bold;
            color: #333;
        }

        .form-group input,
        .form-group select {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .checkbox-group {
            display: flex;
            gap: 1rem;
            margin-top: 0.5rem;
        }

        .checkbox-group label {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-weight: normal;
        }

        .table-container {
            background-color: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }

        th, td {
            padding: 0.75rem;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        th {
            background-color: #2c3e50;
            color: white;
        }

        tr:hover { background-color: #f8f9fa; }

        .status-active { color: #27ae60; font-weight: bold; }
        .status-inactive { color: #e74c3c; font-weight: bold; }

        .alert {
            padding: 1rem;
            border-radius: 6px;
            margin-bottom: 1rem;
        }

        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="logo">Admin Dashboard - Class Scheduler</div>
        <nav>
            <ul class="nav-menu">
                <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/ListCoursesServlet">Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/time-slots.jsp">Time Slots</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/classrooms.jsp" class="active">Classrooms</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/instructors.jsp">Instructors</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/semesters.jsp">Semesters</a></li>
                <li><a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a></li>
            </ul>
        </nav>
    </div>

    <div class="container">
        <div class="dashboard-header">
            <h1>Manage Classrooms</h1>
            <p>Add and manage classrooms for scheduling</p>
        </div>

        <!-- Messages -->
        <%
            String message = request.getParameter("message");
            String error = request.getParameter("error");

            if (message != null) {
        %>
        <div class="alert alert-success">
            <%= java.net.URLDecoder.decode(message, "UTF-8") %>
        </div>
        <%
            }

            if (error != null) {
        %>
        <div class="alert alert-error">
            <%= java.net.URLDecoder.decode(error, "UTF-8") %>
        </div>
        <%
            }
        %>

        <!-- Add Classroom Form -->
        <div class="form-section">
            <h3>Add New Classroom</h3>
            <form action="${pageContext.request.contextPath}/admin/ClassroomServlet" method="post">
                <div class="form-group">
                    <label for="building">Building</label>
                    <input type="text" id="building" name="building"
                           placeholder="e.g., Science Building, Main Building" required>
                </div>

                <div class="form-group">
                    <label for="roomNumber">Room Number</label>
                    <input type="text" id="roomNumber" name="roomNumber"
                           placeholder="e.g., 101, Lab-201, A-305" required>
                </div>

                <div class="form-group">
                    <label for="capacity">Capacity</label>
                    <input type="number" id="capacity" name="capacity"
                           min="1" max="500" value="30" required>
                </div>

                <div class="form-group">
                    <label for="roomType">Room Type</label>
                    <select id="roomType" name="roomType" required>
                        <option value="Lecture Hall">Lecture Hall</option>
                        <option value="Lab">Lab</option>
                        <option value="Computer Lab">Computer Lab</option>
                        <option value="Seminar Room">Seminar Room</option>
                        <option value="Conference Room">Conference Room</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>Facilities</label>
                    <div class="checkbox-group">
                        <label>
                            <input type="checkbox" name="hasProjector" value="true">
                            Has Projector
                        </label>
                        <label>
                            <input type="checkbox" name="hasComputers" value="true">
                            Has Computers
                        </label>
                    </div>
                </div>

                <button type="submit" class="btn btn-success">Add Classroom</button>
            </form>
        </div>

        <!-- Classrooms List -->
        <div class="table-container">
            <h3>Existing Classrooms</h3>

            <jsp:useBean id="classroomDAO" class="com.roba.scheduler.dao.ClassroomDAO" />
            <%
                java.util.List<com.roba.scheduler.model.Classroom> classrooms = classroomDAO.getAllClassrooms();

                if (classrooms.isEmpty()) {
            %>
            <div class="empty-state">
                <h4>No Classrooms Found</h4>
                <p>Add your first classroom using the form above.</p>
            </div>
            <%
                } else {
            %>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Building</th>
                        <th>Room Number</th>
                        <th>Type</th>
                        <th>Capacity</th>
                        <th>Facilities</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (com.roba.scheduler.model.Classroom room : classrooms) {
                    %>
                    <tr>
                        <td><%= room.getRoomId() %></td>
                        <td><%= room.getBuilding() %></td>
                        <td><%= room.getRoomNumber() %></td>
                        <td><%= room.getRoomType() %></td>
                        <td><%= room.getCapacity() %></td>
                        <td>
                            <% if (room.getHasProjector()) { %>📽️<% } %>
                            <% if (room.getHasComputers()) { %>💻<% } %>
                            <% if (!room.getHasProjector() && !room.getHasComputers()) { %>-<% } %>
                        </td>
                        <td class="<%= room.getIsActive() ? "status-active" : "status-inactive" %>">
                            <%= room.getIsActive() ? "Active" : "Inactive" %>
                        </td>
                        <td>
                            <form action="${pageContext.request.contextPath}/admin/ClassroomServlet"
                                  method="post" style="display: inline;">
                                <input type="hidden" name="action" value="toggle">
                                <input type="hidden" name="roomId" value="<%= room.getRoomId() %>">
                                <button type="submit" class="btn"
                                        title="<%= room.getIsActive() ? "Deactivate" : "Activate" %>">
                                    <%= room.getIsActive() ? "Deactivate" : "Activate" %>
                                </button>
                            </form>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
            <%
                }
            %>
        </div>
    </div>

    <script>
        // Clear messages after 5 seconds
        setTimeout(() => {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                alert.style.opacity = '0';
                alert.style.transition = 'opacity 0.5s';
                setTimeout(() => alert.remove(), 500);
            });
        }, 5000);
    </script>
</body>
</html>